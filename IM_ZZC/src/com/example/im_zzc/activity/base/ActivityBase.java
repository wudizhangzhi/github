package com.example.im_zzc.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;

import com.example.im_zzc.activity.LoginActivity;

/**
 * 除了登陆界面和欢迎界面外继承的基类，用于检测是否在同一设备上登陆
 * 
 * @author wudizhangzhi
 *
 */
public class ActivityBase extends BaseActivity {
	public BmobChatUser currentUser;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		checkLogin();
	}

	/**
	 * 用于检测是否在同一设备上登陆
	 */
	private void checkLogin() {
		BmobUserManager userManager = BmobUserManager.getInstance(this);
		if (userManager.getCurrentUser() == null) {
			// TODO showtoast,文字放value的string
			showToast("您的设备已经在别的地方登陆!");
			// ->登陆界面
			startAnimActivity(LoginActivity.class);
			finish();
		}else {
			currentUser=userManager.getCurrentUser();
			updateUserLocation();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 锁屏状态下的检测
		checkLogin();
	}

	// TODO 隐藏软键盘
	public void hideSoftInputView(){
		InputMethodManager imm=(InputMethodManager)this. getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (getWindow().getAttributes().softInputMode!=WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus()!=null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}
}
