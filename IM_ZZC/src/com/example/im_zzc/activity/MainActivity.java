package com.example.im_zzc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.MyMessageReceiver;
import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.ActivityBase;
import com.example.im_zzc.activity.fragment.ContactFragment;
import com.example.im_zzc.activity.fragment.RecentFragment;
import com.example.im_zzc.activity.fragment.SettingFragment;

public class MainActivity extends ActivityBase implements EventListener {
	// private Button btn_message, btn_contract, btn_set;
	private ImageView iv_message_tip, iv_contact_tip;
	private View mContainer;

	private Button[] mTabButtons;
	private Fragment[] fragments;

	private int index;
	private int currentTabIndex;

	private ContactFragment contactFragment;
	private RecentFragment recentFragment;
	private SettingFragment settingFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//TODO 开启定时检测,不知道用处
		BmobChat.getInstance(this).startPollService(30);
		// 开启消息接收器
		initNewTagReceiver();
		initNewMessageReceiver();
		initView();
		initTabFragment();
	}

	private void initView() {
		iv_message_tip = (ImageView) findViewById(R.id.main_iv_recent_tips);
		iv_contact_tip = (ImageView) findViewById(R.id.main_iv_contact_tips);
		mContainer = findViewById(R.id.main_fragment_container);

		mTabButtons = new Button[3];
		mTabButtons[0] = (Button) findViewById(R.id.main_btn_message);
		mTabButtons[1] = (Button) findViewById(R.id.main_btn_contract);
		mTabButtons[2] = (Button) findViewById(R.id.main_btn_set);

		mTabButtons[0].setSelected(true);
	}

	/**
	 * 初始化3个fragment页面
	 */
	private void initTabFragment() {
		contactFragment = new ContactFragment();
		recentFragment = new RecentFragment();
		settingFragment = new SettingFragment();
		fragments = new Fragment[] { recentFragment, contactFragment,
				settingFragment };
		getSupportFragmentManager().beginTransaction()
				.add(R.id.main_fragment_container, recentFragment)
				.add(R.id.main_fragment_container, contactFragment)
				.hide(contactFragment).show(recentFragment).commit();
	}
	
	/**
	 * 底部3个按钮点击事件
	 * @param view
	 */
	public void onTabSelect(View view) {
		switch (view.getId()) {
		case R.id.main_btn_message:
			index = 0;
			break;

		case R.id.main_btn_contract:
			index = 1;
			break;
		case R.id.main_btn_set:
			index = 2;
			break;
		default:
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				transaction.add(R.id.main_fragment_container, fragments[index]);
			}
			transaction.show(fragments[index]).commit();

			mTabButtons[currentTabIndex].setSelected(false);
			mTabButtons[index].setSelected(true);
			currentTabIndex = index;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播接收器
		try {
			unregisterReceiver(newMessageReceiver);
			unregisterReceiver(tagReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//取消定时服务
		BmobChat.getInstance(this).stopPollService();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 清除事件监听
		MyMessageReceiver.ehList.remove(this);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// 刷新两个tip的显示
		if (BmobDB.create(this).hasUnReadMsg()) {
			iv_message_tip.setVisibility(View.VISIBLE);
		} else {
			iv_message_tip.setVisibility(View.GONE);
		}
		if (BmobDB.create(this).hasNewInvite()) {
			iv_contact_tip.setVisibility(View.VISIBLE);
		} else {
			iv_contact_tip.setVisibility(View.GONE);
		}
		// 获取事件监听
		 MyMessageReceiver.ehList.add(this);
		 MyMessageReceiver.mNewNum=0;
	}

	@Override
	public void onAddUser(BmobInvitation invitation) {
		refreshInvite(invitation);
	}

	@Override
	public void onMessage(BmobMsg msg) {
		refreshNewMessage(msg);
	}

	@Override
	public void onNetChange(boolean arg0) {
		showToast("网络出现了问题，请检查连接");
	}

	@Override
	public void onOffline() {
		showOfflineDialog(this);
	}

	@Override
	public void onReaded(String arg0, String arg1) {

	}

	// 刷新消息
	public void refreshNewMessage(BmobMsg msg) {
		// 声音提示
		if (CustomApplication.getInstance().getSpUtil().isAllowVoice()) {
			CustomApplication.getInstance().getMediaPlayer().start();
		}
		// 保存
		if (msg != null) {
			BmobChatManager.getInstance(this).saveReceiveMessage(true, msg);
		}
		// 如果是recent页面，刷新
		if (currentTabIndex == 0) {
			if (recentFragment != null) {
				recentFragment.refresh();
			}
		}
	}

	/**
	 * 初始化新消息接收器
	 */
	NewMessageReceiver newMessageReceiver;

	public void initNewMessageReceiver() {
		newMessageReceiver = new NewMessageReceiver();
		IntentFilter filter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		// 优先级要低于ChatActivity
		filter.setPriority(3);
		registerReceiver(newMessageReceiver, filter);
	}

	/**
	 * 新消息广播接收器
	 */
	public class NewMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO 刷新界面，不知道为什么只传入null，是否能够刷新数据
			refreshNewMessage(null);
			// 终结广播
			abortBroadcast();
		}
	}

	// 刷新好友请求消息
	public void refreshInvite(BmobInvitation invitation) {
		if (CustomApplication.getInstance().getSpUtil().isAllowVoice()) {
			CustomApplication.getInstance().getMediaPlayer().start();
		}
		iv_contact_tip.setVisibility(View.VISIBLE);
		if (currentTabIndex == 1) {
			if (contactFragment != null) {
				 contactFragment.refresh();
			}
		} else {
			// 消息提醒
			String tickerText = invitation.getFromname() + "请求添加好友";
			boolean isAllowVoice = CustomApplication.getInstance().getSpUtil()
					.isAllowVoice();
			boolean isAllowVibrate = CustomApplication.getInstance()
					.getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(this).showNotify(isAllowVoice,
					isAllowVibrate, R.drawable.ic_launcher, tickerText,
					invitation.getFromname(), tickerText.toString(),
					NewFriendActivity.class);
		}
	}

	// 初始化标签接收器
	NewTagReceiver tagReceiver;

	public void initNewTagReceiver() {
		tagReceiver=new NewTagReceiver();
		IntentFilter filter=new IntentFilter(BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		filter.setPriority(3);
		registerReceiver(tagReceiver, filter);
	}

	// 标签消息接收器
	public class NewTagReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation invitation = (BmobInvitation) intent
					.getSerializableExtra("invate");
			refreshInvite(invitation);
			//终结广播
			abortBroadcast();
		}
	}

	private static long firstTime;

	/**
	 * 双击返回按钮退出
	 */
	@Override
	public void onBackPressed() {
		// 如果间隔小于2秒则退出
		if (System.currentTimeMillis() - firstTime < 2000) {
			super.onBackPressed();
		} else {
			showToast("再按一次退出");
		}
	}
}
