package com.example.im_zzc.view.dialog;

import android.content.Context;

import com.example.im_zzc.util.PixelUtil;

public class DialogTip extends DialogBase {
	Context mContext;

	public DialogTip(Context context, String title, String message,
			String positiveName, String negativeName, boolean iscancel) {
		super(context);
		// TODO Auto-generated constructor stub
		super.setTitle(title);
		super.setMessage(message);
		super.setNegativeButtonName(negativeName);
		super.setPositiveButtonName(positiveName);
		super.setCancel(iscancel);
		super.setHasTitle(true);
		super.setFullScreen(false);
		
	}

	public DialogTip(Context context,String message,
			String positiveName,boolean iscancel) {
		super(context);
		// TODO Auto-generated constructor stub
		super.setMessage(message);
		super.setPositiveButtonName(positiveName);
		super.setCancel(iscancel);
		super.setHasTitle(false);
		super.setFullScreen(false);
	}

	@Override
	protected void onDismiss() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onPositiveButtonClick() {
		// TODO Auto-generated method stub
		if (positiveButtonClickListener != null) {
			positiveButtonClickListener.onClick();
		}
	}

	@Override
	protected void onNegativeButtonClick() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onBuilding() {
		super.setWidth(PixelUtil.dip2px(300, mContext));

	}

	// TODO 测试
	private onPositiveButtonClickListener positiveButtonClickListener;

	public interface onPositiveButtonClickListener {
		void onClick();
	}

	public void setOnPositiveButtonClickListener(
			onPositiveButtonClickListener listener) {
		this.positiveButtonClickListener = listener;
	}
}
