package com.example.im_zzc.activity.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.FindListener;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.ChatActivity;
import com.example.im_zzc.activity.base.FragmentBase;
import com.example.im_zzc.adapter.MessageRecentAdapter;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.view.ClearEditText;
import com.example.im_zzc.view.HeaderLayout;
import com.example.im_zzc.view.dialog.DialogTip;
import com.example.im_zzc.view.dialog.DialogTip.onPositiveButtonClickListener;

public class RecentFragment extends FragmentBase implements
		OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener {
	// TODO 解决头像问题
	ClearEditText mClearEditText;
	ListView listview;

	private MessageRecentAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recent, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}
	
	
	private void initView() {
		initTopbarForOnlyTitle("会话");
//		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
//		mHeaderLayout.setDefaultTitle("会话");
		mClearEditText = (ClearEditText) findViewById(R.id.recent_edt_search);
		listview = (ListView) findViewById(R.id.recent_listview);


		mAdapter = new MessageRecentAdapter(getActivity(),
				R.layout.item_conversation, BmobDB.create(getActivity())
						.queryRecents());

		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(RecentFragment.this);
		// edittext的监控
//		mClearEditText.setCursorVisible(false);
//		mClearEditText.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				mClearEditText.requestFocus();
//				mClearEditText.setCursorVisible(true);
//			}
//		});
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mAdapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	// 点击进入聊天界面
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final BmobRecent recent = mAdapter.getItem(position);
		// 消除未读标记
		// TODO 测试!!!!因为官方recent中头像为空，需要向服务器寻求数据
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		BmobUserManager.getInstance(getActivity()).queryUserById(
				recent.getTargetid(), new FindListener<BmobChatUser>() {

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						// TODO Auto-generated method stub
						// 传入的数据
						BmobChatUser user = arg0.get(0);
						Log.i("最近消息", "头像" + recent.getAvatar());
						Intent intent = new Intent(getActivity(),
								ChatActivity.class);
						intent.putExtra("user", user);
						startAnimActivity(intent);
					}

					@Override
					public void onError(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});
		// // 传入的数据
		// BmobChatUser user = new BmobChatUser();
		// user.setAvatar(user.getAvatar());
		// user.setUsername(recent.getUserName());
		// user.setObjectId(recent.getTargetid());
		// user.setNick(recent.getNick());
		// Log.i("最近消息", "头像" + recent.getAvatar());
		// Intent intent = new Intent(getActivity(), ChatActivity.class);
		// intent.putExtra("user", user);
		// startAnimActivity(intent);
	}

	// 删除方法
	public void deleteRecent(BmobRecent recent) {
		mAdapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}

	// 显示删除对话框
	public void showDeleteDialog(final BmobRecent recent) {
		// TODO TipsDialog extends BaseDialog
		DialogTip dialog = new DialogTip(getActivity(), recent.getUserName(),
				"你确定要删除吗？", "确定", "取消", true);
		dialog.setOnPositiveButtonClickListener(new onPositiveButtonClickListener() {

			@Override
			public void onClick() {
				// TODO 测试按钮点击能否触法，等待验证
				deleteRecent(recent);
			}
		});
		dialog.show();
		dialog = null;
	}

	// 刷新界面
	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mAdapter = new MessageRecentAdapter(getActivity(),
							R.layout.item_conversation, BmobDB.create(
									getActivity()).queryRecents());
					listview.setAdapter(mAdapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 判断fragment是否显示隐藏的状态改变
	private boolean isHidden;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		isHidden = hidden;
		if (!isHidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isHidden) {
			refresh();
		}
	}

	/**
	 * 列表item长按事件
	 * 
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 * @return
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		showDeleteDialog(mAdapter.getItem(position));
		return true;
	}

}
