package com.example.im_zzc.activity.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.camera2.params.BlackLevelPattern;
import android.media.session.PlaybackState.CustomAction;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.autoupdatesdk.CPUpdateDownloadCallback;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.R;
import com.example.im_zzc.activity.BlackListActivity;
import com.example.im_zzc.activity.LoginActivity;
import com.example.im_zzc.activity.SetMyInfoActivity;
import com.example.im_zzc.activity.base.FragmentBase;
import com.example.im_zzc.util.CheckUpdateApp;
import com.example.im_zzc.util.SharePreferenceUtil;
import com.example.im_zzc.view.dialog.DialogTip;

public class SettingFragment extends FragmentBase implements OnClickListener {
	Button btn_logout;
	TextView tv_set_name, tv_version;
	RelativeLayout layout_info, rl_switch_notification, rl_switch_voice,
			rl_switch_vibrate, layout_blacklist, rl_checkUpdate;

	ImageView iv_open_notification, iv_close_notification, iv_open_voice,
			iv_close_voice, iv_open_vibrate, iv_close_vibrate;

	View view1, view2;
	SharePreferenceUtil mSharedUtil;

	ProgressDialog dialog;

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
		// 更新进度
		dialog = new ProgressDialog(getActivity());
		// dialog.setMessage("正在搜索更新...");
		dialog.setIndeterminate(true);

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

		// TODO 检查更新控件
		tv_version = (TextView) findViewById(R.id.tv_version);
		rl_checkUpdate = (RelativeLayout) findViewById(R.id.layout_checkupdate);

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
			Intent intent = new Intent(getActivity(), SetMyInfoActivity.class);
			intent.putExtra("from", "me");
			startAnimActivity(intent);
			break;
		case R.id.layout_blacklist:
			startAnimActivity(new Intent(getActivity(), BlackListActivity.class));
			break;
		case R.id.btn_logout:
			CustomApplication.getInstance().Logout();
			getActivity().finish();
			startAnimActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.rl_switch_notification:
			if (iv_close_notification.getVisibility() == View.VISIBLE) {// 原来是关
				iv_close_notification.setVisibility(View.GONE);
				iv_open_notification.setVisibility(View.VISIBLE);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				rl_switch_voice.setVisibility(View.VISIBLE);
				mSharedUtil.setPushNotifyEnable(true);
				view1.setVisibility(View.VISIBLE);
				view2.setVisibility(View.VISIBLE);
			} else {// 原来是开
				iv_close_notification.setVisibility(View.VISIBLE);
				iv_open_notification.setVisibility(View.GONE);
				rl_switch_vibrate.setVisibility(View.GONE);
				rl_switch_voice.setVisibility(View.GONE);
				mSharedUtil.setPushNotifyEnable(false);
			}
			break;
		case R.id.rl_switch_vibrate:
			if (iv_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_open_vibrate.setVisibility(View.GONE);
				iv_close_vibrate.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVibrate(false);
			} else {
				iv_open_vibrate.setVisibility(View.VISIBLE);
				iv_close_vibrate.setVisibility(View.GONE);
				mSharedUtil.setAllowVibrate(true);
			}

			break;
		case R.id.rl_switch_voice:
			if (iv_open_voice.getVisibility() == View.VISIBLE) {
				iv_open_voice.setVisibility(View.GONE);
				iv_close_voice.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVoiceEnable(false);
			} else {
				iv_open_voice.setVisibility(View.VISIBLE);
				iv_close_voice.setVisibility(View.GONE);
				mSharedUtil.setAllowVoiceEnable(true);
			}
			break;
		case R.id.layout_checkupdate:
			// TODO 检查版本，对比后更新
			tv_version.setText("");
			dialog.show();
			BDAutoUpdateSDK.cpUpdateCheck(getActivity(), new MyCPCheckUpdateCallback());
			break;
		default:
			break;
		}
	}
	

	private class MyCPCheckUpdateCallback implements CPCheckUpdateCallback {

		@Override
		public void onCheckUpdateCallback(AppUpdateInfo info,
				AppUpdateInfoForInstall infoForInstall) {
			if (infoForInstall != null
					&& !TextUtils.isEmpty(infoForInstall.getInstallPath())) {
				dialog.setMessage( "install info: "
						+ infoForInstall.getAppSName() + ", \nverion="
						+ infoForInstall.getAppVersionName()
						+ ", \nchange log=" + infoForInstall.getAppChangeLog());
				dialog.setMessage(tv_version.getText()
						+ "\n we can install the apk file in: "
						+ infoForInstall.getInstallPath());
				dialog.dismiss();
				BDAutoUpdateSDK.cpUpdateInstall(getActivity(),
						infoForInstall.getInstallPath());
			} else if (info != null) {
				BDAutoUpdateSDK.cpUpdateDownload(getActivity(), info,
						new UpdateDownloadCallback());
			} else {
				showToast("当前已经是最新版本");
				dialog.dismiss();
			}
//			dialog.dismiss();
		}

		private class UpdateDownloadCallback implements
				CPUpdateDownloadCallback {

			@Override
			public void onDownloadComplete(String apkPath) {
//				tv_version.setText(tv_version.getText() + "\n onDownloadComplete: "
//						+ apkPath);
				dialog.setMessage("下载完成");
				dialog.dismiss();
				BDAutoUpdateSDK.cpUpdateInstall(getActivity(), apkPath);
			}

			@Override
			public void onStart() {
//				tv_version.setText(tv_version.getText() + "\n Download onStart");
				dialog.setMessage("开始下载:0%");
			}

			@Override
			public void onPercent(int percent, long rcvLen, long fileSize) {
//				tv_version.setText("Download onPercent: "
//						+ percent + "%");
				dialog.setMessage("开始下载:"+percent+"%");
			}

			@Override
			public void onFail(Throwable error, String content) {
//				tv_version.setText(tv_version.getText() + "\n Download onFail: "
//						+ content);
				dialog.setMessage("下载失败:"+content);
			}

			@Override
			public void onStop() {
//				tv_version.setText(tv_version.getText() + "\n Download onStop");
				dialog.setMessage("下载停止");
			}
		}
	}
	
}
