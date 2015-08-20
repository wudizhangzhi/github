package com.example.im_zzc.activity.base;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.R;
import com.example.im_zzc.view.HeaderLayout;
import com.example.im_zzc.view.HeaderLayout.HeaderStyle;
import com.example.im_zzc.view.HeaderLayout.onLeftImageButtonClickListener;
import com.example.im_zzc.view.HeaderLayout.onRightImageButtonClickListener;

public class FragmentBase extends Fragment {
	public BmobChatManager manager;
	public BmobUserManager userManager;
	public CustomApplication mApplication;
	/**
	 * 公共的view布局
	 */
	public LayoutInflater mInflate;
	public HeaderLayout mHeaderLayout;
	protected View contentView;

	private Handler handler = new Handler();

	public void FragmentBase() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userManager = BmobUserManager.getInstance(getActivity());
		manager = BmobChatManager.getInstance(getActivity());
		mApplication = CustomApplication.getInstance();
		mInflate = LayoutInflater.from(getActivity());
	}

	public void runOnWorkThread(Runnable runnable) {
		new Thread(runnable).start();
	}

	public void runUiThread(Runnable runnable) {
		handler.post(runnable);
	}

	protected Toast mToast;

	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public void showToast(int text) {
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public void showLog(String msg) {
		BmobLog.i(msg);
	}

	public View findViewById(int id) {
		return getView().findViewById(id);
	}

	/**
	 * 动画启动页面 startAnimActivity
	 * 
	 * @throws
	 */
	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}

	public void startAnimActivity(Class<?> cla) {
		getActivity().startActivity(new Intent(getActivity(), cla));
	}

	// 初始化设置headerlayout的方法，因为外面xml中用的include，所以需要调用这个方法
	// TODO 待优化
	public void initTopbarForOnlyTitle(String title) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(title);
	}

	public void initTopbarForBoth(String title, int leftId, int rightId,
			onLeftImageButtonClickListener leftlistener,
			onRightImageButtonClickListener rightlistener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(title, leftId, leftlistener);
		mHeaderLayout.setTitleAndRightImageButton(title, rightId, title,
				rightlistener);
	}

	public void initTopbarForRight(String title, int rightId,
			onRightImageButtonClickListener rightlistener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_RIGHT_IMAGEBUTTON);
		mHeaderLayout
				.setTitleAndRightImageButton(title, rightId, rightlistener);
	}

	public void initTopbarForLeft(String title, int leftId,
			onLeftImageButtonClickListener leftlistener) {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_LEFT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(title, leftId, leftlistener);
	}
}
