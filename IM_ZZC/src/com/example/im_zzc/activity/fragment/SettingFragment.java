package com.example.im_zzc.activity.fragment;

import android.content.Intent;
import android.hardware.camera2.params.BlackLevelPattern;
import android.media.session.PlaybackState.CustomAction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.R;
import com.example.im_zzc.activity.BlackListActivity;
import com.example.im_zzc.activity.LoginActivity;
import com.example.im_zzc.activity.SetMyInfoActivity;
import com.example.im_zzc.activity.base.FragmentBase;
import com.example.im_zzc.util.CheckUpdateApp;
import com.example.im_zzc.util.SharePreferenceUtil;

public class SettingFragment extends FragmentBase implements OnClickListener {
	Button btn_logout;
	TextView tv_set_name,tv_version;
	RelativeLayout layout_info, rl_switch_notification, rl_switch_voice,
			rl_switch_vibrate, layout_blacklist,rl_checkUpdate;

	ImageView iv_open_notification, iv_close_notification, iv_open_voice,
			iv_close_voice, iv_open_vibrate, iv_close_vibrate;

	View view1, view2;
	SharePreferenceUtil mSharedUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedUtil = mApplication.getSpUtil();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_set, null);
	}
	
	private void initData() {
		tv_set_name.setText(userManager.getCurrentUserName());
	}


	private void initView() {
		initTopbarForOnlyTitle("设置");
		layout_blacklist = (RelativeLayout) findViewById(R.id.layout_blacklist);

		layout_info = (RelativeLayout) findViewById(R.id.layout_info);
		rl_switch_notification = (RelativeLayout) findViewById(R.id.rl_switch_notification);
		rl_switch_voice = (RelativeLayout) findViewById(R.id.rl_switch_voice);
		rl_switch_vibrate = (RelativeLayout) findViewById(R.id.rl_switch_vibrate);
		
		iv_open_notification = (ImageView) findViewById(R.id.iv_open_notification);
		iv_close_notification = (ImageView) findViewById(R.id.iv_close_notification);
		iv_open_voice = (ImageView) findViewById(R.id.iv_open_voice);
		iv_close_voice = (ImageView) findViewById(R.id.iv_close_voice);
		iv_open_vibrate = (ImageView) findViewById(R.id.iv_open_vibrate);
		iv_close_vibrate = (ImageView) findViewById(R.id.iv_close_vibrate);
		view1 = (View) findViewById(R.id.view1);
		view2 = (View) findViewById(R.id.view2);
		
		//TODO 检查更新控件
		tv_version=(TextView) findViewById(R.id.tv_version);
		rl_checkUpdate=(RelativeLayout) findViewById(R.id.layout_checkupdate);
		
		tv_set_name = (TextView) findViewById(R.id.tv_set_name);
		btn_logout = (Button) findViewById(R.id.btn_logout);
		
		boolean isAllowNotify = mSharedUtil.isAllowPushNotify();
		if (isAllowNotify) {
			iv_open_notification.setVisibility(View.VISIBLE);
			iv_close_notification.setVisibility(View.GONE);
			rl_switch_voice.setVisibility(View.VISIBLE);
			rl_switch_vibrate.setVisibility(View.VISIBLE);
		} else {
			iv_open_notification.setVisibility(View.GONE);
			iv_close_notification.setVisibility(View.VISIBLE);
			rl_switch_voice.setVisibility(View.GONE);
			rl_switch_vibrate.setVisibility(View.GONE);
		}

		boolean isAllowVoice = mSharedUtil.isAllowVoice();
		if (isAllowVoice) {
			iv_open_voice.setVisibility(View.VISIBLE);
			iv_close_voice.setVisibility(View.GONE);
		} else {
			iv_open_voice.setVisibility(View.GONE);
			iv_close_voice.setVisibility(View.VISIBLE);
		}

		boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
		if (isAllowVibrate) {
			iv_open_vibrate.setVisibility(View.VISIBLE);
			iv_close_vibrate.setVisibility(View.GONE);
		} else {
			iv_open_vibrate.setVisibility(View.GONE);
			iv_close_vibrate.setVisibility(View.VISIBLE);
		}
		rl_switch_notification.setOnClickListener(this);
		rl_switch_voice.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
		layout_info.setOnClickListener(this);
		layout_blacklist.setOnClickListener(this);
		rl_checkUpdate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_info:
			Intent intent=new Intent(getActivity(),SetMyInfoActivity.class);
			intent.putExtra("from", "me");
			startAnimActivity(intent);
			break;
		case R.id.layout_blacklist:
			startAnimActivity(new Intent(getActivity(),BlackListActivity.class));
			break;
		case R.id.btn_logout:
			CustomApplication.getInstance().Logout();
			getActivity().finish();
			startAnimActivity(new Intent(getActivity(),LoginActivity.class));
			break;
		case R.id.rl_switch_notification:
			if (iv_close_notification.getVisibility()==View.VISIBLE) {//原来是关
				iv_close_notification.setVisibility(View.GONE);
				iv_open_notification.setVisibility(View.VISIBLE);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				rl_switch_voice.setVisibility(View.VISIBLE);
				mSharedUtil.setPushNotifyEnable(true);
				view1.setVisibility(View.VISIBLE);
				view2.setVisibility(View.VISIBLE);
			}else {//原来是开
				iv_close_notification.setVisibility(View.VISIBLE);
				iv_open_notification.setVisibility(View.GONE);
				rl_switch_vibrate.setVisibility(View.GONE);
				rl_switch_voice.setVisibility(View.GONE);
				mSharedUtil.setPushNotifyEnable(false);
			}
			break;
		case R.id.rl_switch_vibrate:
			if (iv_open_vibrate.getVisibility()==View.VISIBLE) {
				iv_open_vibrate.setVisibility(View.GONE);
				iv_close_vibrate.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVibrate(false);
			}else {
				iv_open_vibrate.setVisibility(View.VISIBLE);
				iv_close_vibrate.setVisibility(View.GONE);
				mSharedUtil.setAllowVibrate(true);
			}
			
			break;
		case R.id.rl_switch_voice:
			if (iv_open_voice.getVisibility()==View.VISIBLE) {
				iv_open_voice.setVisibility(View.GONE);
				iv_close_voice.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVoiceEnable(false);
			}else {
				iv_open_voice.setVisibility(View.VISIBLE);
				iv_close_voice.setVisibility(View.GONE);
				mSharedUtil.setAllowVoiceEnable(true);
			}
			break;
		case R.id.layout_checkupdate:
			//TODO 检查版本，对比后更新
//			CheckUpdateApp.getInstance()
			break;
		default:
			break;
		}
	}

}
