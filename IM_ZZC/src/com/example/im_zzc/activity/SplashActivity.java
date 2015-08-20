package com.example.im_zzc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.session.PlaybackState.CustomAction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.bmob.im.BmobChat;
import cn.bmob.im.bean.BmobChatUser;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.BaseActivity;
import com.example.im_zzc.config.config;

public class SplashActivity extends BaseActivity {
	private LocationClient mLocationClient;
	private BaiduReceicer mReceiver;
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
		//// 开启定位
		initLocationClient();
		// 注册地图 SDK 广播监听者
		IntentFilter iFilter=new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver=new BaiduReceicer();
		registerReceiver(mReceiver, iFilter);
		
		if (userManager.getCurrentUser()!=null) {
			updateUserInfo();
			//TODO 延迟时间是否可以设置一下啊
			mHandler.sendEmptyMessageDelayed(GO_HOME, 1000);
		}else {
			mHandler.sendEmptyMessageDelayed(GO_LOGIN, 1000);
		}
		
	}

	/**
	 * 开启定位，更新当前用户的经纬度坐标
	 */
	private void initLocationClient() {
		mLocationClient=CustomApplication.getInstance().mLocationClient;
		LocationClientOption option=new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式:高精度模式
		option.setCoorType("bd09ll"); // 设置坐标类型:百度经纬度
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms:低于1000为手动定位一次，大于或等于1000则为定时定位
		option.setIsNeedAddress(false);// 不需要包含地址信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
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
				startAnimActivity(LoginActivity.class);
				finish();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		};
	};
	
	
	public class BaiduReceicer extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				showToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			}else if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				showToast("当前网络连接不稳定，请检查您的网络设置!");
			}
		}
	}
	
	
}
