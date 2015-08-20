package com.example.im_zzc.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.BaseActivity;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.config.BmobConstants;
import com.example.im_zzc.util.CheckRegisterInfo;

public class RegisterActivity extends BaseActivity implements OnClickListener {
	private EditText edt_username, edt_password, edt_password_confirm;
	private Button bt_register;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		init();
	}

	private void init() {
		edt_username = (EditText) findViewById(R.id.register_edt_username);
		edt_password = (EditText) findViewById(R.id.register_edt_password);
		edt_password_confirm = (EditText) findViewById(R.id.register_edt_password_confirm);
		bt_register = (Button) findViewById(R.id.register_btn_register);
		bt_register.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		register();
	}

	private void register() {
		String username = edt_username.getText().toString();
		String password = edt_password.getText().toString();
		String password_confirm = edt_password_confirm.getText().toString();

		if (TextUtils.isEmpty(username)) {
			showToast("用户名不能为空!");
			return;
		}
		if (TextUtils.isEmpty(password) || TextUtils.isEmpty(password_confirm)) {
			showToast("密码不能为空!");
			return;
		}
		if (!password.equals(password_confirm)) {
			showToast("两次输入的密码不一致!");
			return;
		}
		//检查密码
		if (password.length()<6) {
			showToast("密码不能少于6位");
			return;
		}
		if (password.length()>16) {
			showToast("密码不能大于16位");
			return;
		}
		// 检测用户名规则
		int result = CheckRegisterInfo.checkUserName(username);
		switch (result) {
		case CheckRegisterInfo.USERNAME_CONTAINSPACE:
			showToast("用户名不能含有空格!");
			break;
		case CheckRegisterInfo.USERNAME_TOOLONG:
			showToast("用户名太长，不能超过6个字或12个字母!");
			break;
		case CheckRegisterInfo.USERNAME_TOOSHORT:
			showToast("用户名太短，不能少于2个字或4个字母!");
			break;
		case CheckRegisterInfo.USERNAME_OK:
			// 创建dialog，显示提示
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("正在注册...");
			dialog.show();
			// 创建user，上传注册
			final User u = new User();
			u.setUsername(username);
			u.setPassword(password);
			// 将user和设备id进行绑定
			u.setDeviceType("android");
			u.setInstallId(BmobInstallation.getInstallationId(this));
			u.signUp(this, new SaveListener() {

				@Override
				public void onSuccess() {
					dialog.dismiss();
					showToast("注册成功");
					// 将设备与username进行绑定
					userManager.bindInstallationForRegister(u.getUsername());
					// 更新地理位置信息，如果需要LBS功能，建议添加此代码
					// updateUserLocation();
					// TODO 转换页面
					// 1.第一次登陆，设置个人资料页面
					// 2.若不是第一次，主界面
					// 发送注册成功广播销毁登陆界面
					sendBroadcast(new Intent(
							BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
					startAnimActivity(MainActivity.class);
					finish();
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					showToast("注册失败：" + arg1);
				}
			});
			break;
		default:
			break;
		}

	}

	// private static final int username_tooshort = 100;//用户名太短
	// private static final int username_toolong = 200;
	// private static final int password_tooshort = 300;
	// private static final int password_allnumber = 400;
	// private static final int password_containillegalcontent = 500;
	//
	/**
	 * 账号:汉字。不少于2个字，不多于8个字,(2,8] 字母:（4,16]
	 */
	// private void isRegisterUsernameEnable(String name) {
	// // TextUtils.
	// }

	/**
	 * 密码不少于6位，不能是纯数字,不能出现除字母和数字其他的字符 [6,12]
	 */
	// private void isRegisterPasswordEnable(String password) {
	// //出现除字母和数字其他的字符
	// // if (condition) {
	// // mHandler.sendEmptyMessage(password_containillegalcontent);
	// // }
	// //是纯数字
	// if (TextUtils.isDigitsOnly(password)) {
	// mHandler.sendEmptyMessage(password_allnumber);
	// return;
	// }
	// //位数少于6
	// if (password.length()<6) {
	//
	// }
	// }
	//
	// /**
	// * 根据发回的message判断dialog显示内容
	// */
	// Handler mHandler = new Handler() {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// super.handleMessage(msg);
	// // TODO Auto-generated method stub
	// switch (msg.what) {
	// case username_tooshort:
	//
	// break;
	//
	// default:
	// break;
	// }
	// }
	// };
}
