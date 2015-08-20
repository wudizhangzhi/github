package com.example.im_zzc.activity.base;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.R;
import com.example.im_zzc.activity.LoginActivity;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.util.CollectionUtils;
import com.example.im_zzc.view.HeaderLayout;
import com.example.im_zzc.view.HeaderLayout.HeaderStyle;
import com.example.im_zzc.view.HeaderLayout.onLeftImageButtonClickListener;
import com.example.im_zzc.view.HeaderLayout.onRightImageButtonClickListener;
import com.example.im_zzc.view.dialog.DialogTip;
import com.example.im_zzc.view.dialog.DialogTip.onPositiveButtonClickListener;

public class BaseActivity extends FragmentActivity {
	protected BmobUserManager userManager;
	protected BmobChatManager manager;
	protected CustomApplication mApplicaton;
	protected HeaderLayout mHeadLayout;

	protected int mScreenWidth;
	protected int mScreenHeight;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		// 获取BmobUserManager,BmobChatManager,Application,屏幕尺寸
		userManager = BmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
		mApplicaton = CustomApplication.getInstance();
		DisplayMetrics outMetrics = new DisplayMetrics();
		// WindowManager wm=(WindowManager)
		// getSystemService(Context.WINDOW_SERVICE);
		// wm.getDefaultDisplay().getMetrics(outMetrics);
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;
		mScreenHeight = outMetrics.heightPixels;
	}

	/**
	 * 用于登陆或者自动登陆状态下用户资料以及好友资料的检测跟新
	 */
	public void updateUserInfo() {
		//更新地理位置
		updateUserLocation();
		// 默认获取好友资料成功后保存到数据库中，并更新到本地内存
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				CustomApplication.getInstance().setContactList(
						CollectionUtils.list2map(arg0));
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					showLog(arg1);
				} else {
					showLog("查询好友列表失败" + arg1);
				}
			}
		});
	}
	
	/**
	 * 更新位置信息
	 */
	private void updateUserLocation() {
		if (CustomApplication.getInstance().mLastLocation!=null) {
			//保存的位置
			String saveLatitude=mApplicaton.getLatitude();
			String saveLongtitude=mApplicaton.getLongitude();
			//新的位置
			String newLatitude=String.valueOf(CustomApplication.mLastLocation.getLatitude());
			String newLongtitude=String.valueOf(CustomApplication.mLastLocation.getLongitude());
			if (!saveLatitude.equals(newLatitude)||!saveLongtitude.equals(newLongtitude)) {//坐标有变化
				final User user=userManager.getCurrentUser(User.class);
				user.setLocation(CustomApplication.mLastLocation);
				user.update(this, new UpdateListener(){

					@Override
					public void onFailure(int arg0, String arg1) {
						showToast("更新位置失败："+arg1);
					}

					@Override
					public void onSuccess() {
						CustomApplication.getInstance().setLatitude(String.valueOf(user.getLocation().getLatitude()));
						CustomApplication.getInstance().setLongtitude(String.valueOf(user.getLocation().getLongitude()));
					}
				} );
			}
		}
	}

	// 更新控件的方法
	// 点击事件接口
	// 转换界面方法
	public void startAnimActivity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
	}

	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}
	
	public void initTopBarForLeft(String title) {
		mHeadLayout=(HeaderLayout) findViewById(R.id.common_actionbar);
		mHeadLayout.init(HeaderStyle.TITLE_LEFT_IMAGEBUTTON);
		mHeadLayout.setTitleAndLeftImageButton(title,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
	}
	
	public void initTopBarForBoth(String title,int rightImageId,onRightImageButtonClickListener listen) {
		mHeadLayout=(HeaderLayout) findViewById(R.id.common_actionbar);
		mHeadLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeadLayout.setTitleAndLeftImageButton(title,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeadLayout.setTitleAndRightImageButton(title, rightImageId, listen);
	}
	
	/**
	 * 左边返回按钮的点击事件
	 */
	public class OnLeftButtonClickListener implements
			onLeftImageButtonClickListener {
		@Override
		public void onClick() {
			finish();
		}
	}

	Toast mToast;

	public void showToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,
								Toast.LENGTH_SHORT);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});
		}
	}

	public void showToast(final int resId) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mToast == null) {
					mToast = Toast.makeText(getApplicationContext(), resId,
							Toast.LENGTH_SHORT);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	public void showLog(String text) {
		BmobLog.i(text);
	}

	public void showOfflineDialog(Context context) {
		final DialogTip dialog = new DialogTip(context, "您的账号已经在其他设备登陆！",
				"重新登陆", false);
		dialog.setOnPositiveButtonClickListener(new onPositiveButtonClickListener() {

			@Override
			public void onClick() {
				CustomApplication.getInstance().Logout();
				startAnimActivity(LoginActivity.class);
				finish();
				dialog.dismiss();
			}
		});
		dialog.show();
		// TODO 按钮的监控不一样。所以这里没办法dialog=null；
	}
}
