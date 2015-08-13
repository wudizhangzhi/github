package com.example.im_zzc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.bmob.im.BmobChat;
import cn.bmob.im.bean.BmobChatUser;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.BaseActivity;
import com.example.im_zzc.config.config;

public class SplashActivity extends BaseActivity {
	// TODO
	// 连接后台，设置后台id
	// 检查用户登陆,yes->获取好友列表，主界面；no->登陆界面
	// init设置
	//开启定位，获取位置initLocClient()
	//baidu的receiver
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_splash);
		// 设置应用密钥
		BmobChat.getInstance(this).init(config.APPLICATION_ID);
		
		if (userManager.getCurrentUser()!=null) {
			updateUserInfo();
			//TODO 延迟时间是否可以设置一下啊
			mHandler.sendEmptyMessageDelayed(GO_HOME, 1000);
		}else {
			mHandler.sendEmptyMessageDelayed(GO_LOGIN, 1000);
		}
		
	}

	private final static int GO_HOME = 100;
	private final static int GO_LOGIN = 200;

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				Intent intent=new Intent(SplashActivity.this,MainActivity.class);
				startAnimActivity(intent);
				finish();
				break;
			case GO_LOGIN:
				startAnimActivity(MainActivity.class);
				finish();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		};
	};
}
