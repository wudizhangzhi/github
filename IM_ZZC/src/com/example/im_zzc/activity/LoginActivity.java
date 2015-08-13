package com.example.im_zzc.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.v3.listener.SaveListener;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.BaseActivity;
import com.example.im_zzc.config.BmobConstants;
import com.example.im_zzc.util.CommonUtil;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private Button btn_register, btn_confirm;
	private EditText edt_username, edt_password;

	private MyBroadcastReceiver receiver = new MyBroadcastReceiver();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_login);
		IntentFilter filter = new IntentFilter();
		filter.addAction(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH);
		registerReceiver(receiver, filter);
		init();
	}

	private void init() {
		btn_register = (Button) findViewById(R.id.login_btn_register);
		btn_confirm = (Button) findViewById(R.id.login_btn_confirm);
		edt_username = (EditText) findViewById(R.id.login_edt_username);
		edt_password = (EditText) findViewById(R.id.login_edt_password);
		btn_register.setOnClickListener(this);
		btn_confirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == btn_confirm) {
			// 检测网路连接
			boolean isConnected = CommonUtil.isNetworkAvailable(this);
			if (!isConnected) {
				showToast("请检查网络连接");
				return;
			}
			login();
		} else {
			startAnimActivity(RegisterActivity.class);
		}
	}

	/**
	 * 登陆
	 */
	private void login() {
		String username = edt_username.getText().toString();
		String password = edt_password.getText().toString();
		if (TextUtils.isEmpty(username)) {
			showToast("用户名不能为空");
			return;
		}
		if (TextUtils.isEmpty(password)) {
			showToast("密码不能为空");
			return;
		}
		// 显示正在登陆dialog
		final ProgressDialog progressdialog = new ProgressDialog(
				LoginActivity.this);
		progressdialog.setMessage("正在登陆...");
		progressdialog.setCancelable(false);
		progressdialog.show();

		userManager.login(username, password, new SaveListener() {

			@Override
			public void onSuccess() {
				// 登陆完成后更新好友列表，dialog显示获取好友列表，成功后退出

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						progressdialog.setMessage("正在获取好友列表");
					}
				});
				updateUserInfo();
				progressdialog.dismiss();
				startAnimActivity(MainActivity.class);
				finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showToast(arg1);
			}
		});
	}

	/**
	 * 用于接收注册成功的广播
	 * @author wudizhangzhi
	 */
	public class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null
					&& BmobConstants.ACTION_REGISTER_SUCCESS_FINISH
							.equals(intent.getAction())) {
				finish();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	
}
