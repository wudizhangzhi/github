package com.example.im_zzc.activity;

import android.os.Bundle;
import android.widget.EditText;
import cn.bmob.v3.listener.UpdateListener;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.ActivityBase;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.view.HeaderLayout.onRightImageButtonClickListener;

public class UpdateInfoActivity extends ActivityBase{
	private EditText edit_nick;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_set_updateinfo);
		initView();
	}

	private void initView() {
		edit_nick = (EditText) findViewById(R.id.edit_nick);
		initTopBarForBoth("修改昵称", R.drawable.base_action_bar_true_bg_selector, new onRightImageButtonClickListener() {
			
			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				String nick=edit_nick.getText().toString();
				if (nick.equals("")) {
					showToast("昵称不能为空");
					return;
				}
				updateInfo(nick);
			}
		});
	}

	private void updateInfo(String nick) {
		// TODO Auto-generated method stub
		User user=userManager.getCurrentUser(User.class);
		user.setNick(nick);
		user.update(this, new UpdateListener(){

			@Override
			public void onFailure(int arg0, String arg1) {
				showToast("修改失败："+arg1);
			}

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				showToast("修改成功");
				finish();
			}
		});
	}
}
