package com.example.im_zzc.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.im_zzc.R;

 //TODO 很多地方不明白
public abstract class DialogBase extends Dialog {
	protected Context mContext;

	private boolean hasTitle = true;
	private boolean isFullScreen = false;
	private boolean isCancel = true;
	// 对话框宽度,高度,位置
	private int width = 0, height = 0, x = 0, y = 0;
	protected ImageView iv_title_icon;
	protected TextView tv_title, tv_message;
	protected Button negativeButton, positiveButton;

	private int iconTitle;// 标题图标
	private String title, message;// 标题文字，内容文字
	private String negativeButtonName, positiveButtonName;// 确认按钮文字。取消按钮文字
	private View view;// 可能会插入的view

	private static final int MATCH_PARENT = android.view.ViewGroup.LayoutParams.MATCH_PARENT;

	public DialogBase(Context context) {
		 super(context,R.style.alert);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_base);
		this.onBuilding();

		LinearLayout dialog_top = (LinearLayout) findViewById(R.id.dialogbase_ll_top);
		View view_redline = findViewById(R.id.dialogbase_view_redline);
		tv_title = (TextView) findViewById(R.id.dialogbase_tv_title);
		tv_message = (TextView) findViewById(R.id.dialogbase_tv_message);
		iv_title_icon = (ImageView) findViewById(R.id.dialogbase_iv_title_icon);
		positiveButton = (Button) findViewById(R.id.dialogbase_bt_positivebutton);
		negativeButton = (Button) findViewById(R.id.dialogbase_bt_negativebutton);
		// 设置标题:判断是否有标题
		if (hasTitle) {
			dialog_top.setVisibility(View.VISIBLE);
			view_redline.setVisibility(View.VISIBLE);
			tv_title.setText(getTitle());
			if (iconTitle > 0) {
				iv_title_icon.setImageResource(iconTitle);
				iv_title_icon.setVisibility(View.VISIBLE);
			} else {
				iv_title_icon.setVisibility(View.GONE);
			}
		} else {
			dialog_top.setVisibility(View.GONE);
			view_redline.setVisibility(View.GONE);
		}
		tv_message.setText(getMessage());
		// 判断是否有插入的view
		if (view!= null) {
			FrameLayout custompanel = (FrameLayout) findViewById(R.id.dialogbase_fl_customPanel);
			custompanel.addView(view, MATCH_PARENT, MATCH_PARENT);
			custompanel.setVisibility(View.VISIBLE);
			findViewById(R.id.dialogbase_ll_contentPanel).setVisibility(
					View.GONE);
		} else {
			findViewById(R.id.dialogbase_fl_customPanel).setVisibility(
					View.GONE);
		}
		// 判断按钮的显示
		if (positiveButtonName != null && positiveButtonName.length() > 0) {
			positiveButton.setText(positiveButtonName);
			positiveButton.setVisibility(View.VISIBLE);
			positiveButton.setOnClickListener(getPositiveButtonOnClickListener());
		} else {
			positiveButton.setVisibility(View.GONE);
		}

		if (negativeButtonName != null && negativeButtonName.length() > 0) {
			negativeButton.setText(negativeButtonName);
			negativeButton.setVisibility(View.VISIBLE);
			negativeButton.setOnClickListener(getNegativeButtonOnClickListener());
		} else {
			negativeButton.setVisibility(View.GONE);
		}

		// 设置对话框属性
		LayoutParams param = this.getWindow().getAttributes();
		if (width > 0) {
			param.width = width;
		}
		if (height > 0) {
			param.height = height;
		}
		if (x > 0) {
			param.x = x;
		}
		if (y > 0) {
			param.y = y;
		}
		if (isFullScreen) {
			param.width = WindowManager.LayoutParams.MATCH_PARENT;
			param.height = WindowManager.LayoutParams.MATCH_PARENT;
		}
		// 判断能否外侧点击退出
		if (isCancel) {
			this.setCancelable(true);
			setCanceledOnTouchOutside(true);
		} else {
			this.setCancelable(false);
			setCanceledOnTouchOutside(false);
		}
		getWindow().setAttributes(param);
		// 消失监听，清除
		 this.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				DialogBase.this.onDismiss();
				DialogBase.this.setOnDismissListener(null);
				positiveButton = null;
				negativeButton = null;
				view=null;
				mContext=null;
			}
		});
		// 软键盘设置
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}


	/**
	 * 获得确认点击监听
	 * 
	 * @return
	 */
	private android.view.View.OnClickListener getPositiveButtonOnClickListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onPositiveButtonClick();
				DialogBase.this.dismiss();
			}
		};
	}

	/*
	 * 获得取消点击监听
	 * 
	 * @return
	 */
	private android.view.View.OnClickListener getNegativeButtonOnClickListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onNegativeButtonClick();
				DialogBase.this.dismiss();
			}
		};
	}
	
	/**
	 *用于定制dialog创建过程 
	 */
	protected abstract void onBuilding();
	
	/**
	 * 必须继承的消失监听
	 */
	protected abstract void onDismiss();
	
	/**
	 * 必须继承的确认按钮事件
	 */
	protected abstract void onPositiveButtonClick();

	/**
	 * 必须继承的取消按钮事件
	 */
	protected abstract void onNegativeButtonClick();

	public boolean isHasTitle() {
		return hasTitle;
	}

	public void setHasTitle(boolean hasTitle) {
		this.hasTitle = hasTitle;
	}

	public boolean isFullScreen() {
		return isFullScreen;
	}

	public void setFullScreen(boolean isFullScreen) {
		this.isFullScreen = isFullScreen;
	}

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public ImageView getIv_title_icon() {
		return iv_title_icon;
	}

	public void setIv_title_icon(ImageView iv_title_icon) {
		this.iv_title_icon = iv_title_icon;
	}

	public int getIconTitle() {
		return iconTitle;
	}

	public void setIconTitle(int iconTitle) {
		this.iconTitle = iconTitle;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getNegativeButtonName() {
		return negativeButtonName;
	}

	public void setNegativeButtonName(String negativeButtonName) {
		this.negativeButtonName = negativeButtonName;
	}

	public String getPositiveButtonName() {
		return positiveButtonName;
	}

	public void setPositiveButtonName(String positiveButtonName) {
		this.positiveButtonName = positiveButtonName;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

}
