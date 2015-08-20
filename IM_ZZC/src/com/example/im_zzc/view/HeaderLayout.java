package com.example.im_zzc.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.im_zzc.R;
import com.example.im_zzc.util.PixelUtil;

public class HeaderLayout extends LinearLayout {
	private LayoutInflater mInflater;

	private View mHeader;
	private LinearLayout mLinearLayoutContainerLeft;// actionbar的左边容器

	private LinearLayout mLinearLayoutContainerRight;// actionbar的右边容器
	private TextView mTvSubTitle;// actionbar中间的textview

	private LinearLayout mLinearLayoutLeft;// actionbar的item左边的大布局
	private LinearLayout mLinearLayoutRight;// actionbar的item右边的大布局
	private ImageButton mImageButtonLeft;// actionbar的item左边布局中的imagebutton
	private Button mButtonRight;// actionbar的item右边布局中的button

	public HeaderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public HeaderLayout(Context context) {
		this(context, null);
	}
	
	//TODO 测试
	private  Context mContext;
	private void init(Context context) {
		mContext=context;
		mInflater = LayoutInflater.from(context);
		mHeader = mInflater.inflate(R.layout.common_header, null);
		addView(mHeader);
		initView();
	}

	private void initView() {
		mLinearLayoutContainerLeft = (LinearLayout) mHeader
				.findViewById(R.id.header_layout_container_leftview);
		mLinearLayoutContainerRight = (LinearLayout) mHeader
				.findViewById(R.id.header_layout_container_rightview);
		mTvSubTitle = (TextView) mHeader
				.findViewById(R.id.header_textview_subtitle);
	}

	// 设置header的样式的方法
	public enum HeaderStyle {
		DEFAULT_TITLE, TITLE_LEFT_IMAGEBUTTON, TITLE_RIGHT_IMAGEBUTTON, TITLE_DOUBLE_IMAGEBUTTON;
	}

	/**
	 * 初始化样式
	 * 
	 * @param style
	 */
	public void init(HeaderStyle style) {
		switch (style) {
		case DEFAULT_TITLE:
			defaultTitle();
			break;
		case TITLE_LEFT_IMAGEBUTTON:
			defaultTitle();
			LeftImageButton();
			break;
		case TITLE_RIGHT_IMAGEBUTTON:
			defaultTitle();
			RightImageButton();
			break;
		case TITLE_DOUBLE_IMAGEBUTTON:
			defaultTitle();
			LeftImageButton();
			RightImageButton();
			break;

		default:
			break;
		}
	}

	// 初始化标题栏
	private void defaultTitle() {
		mLinearLayoutContainerLeft.removeAllViews();
		mLinearLayoutContainerRight.removeAllViews();
	}

	// 初始化左边控件
	private void LeftImageButton() {
		View layoutleft = mInflater.inflate(
				R.layout.common_header_left_imagebutton, null);
		mLinearLayoutContainerLeft.addView(layoutleft);
		mLinearLayoutLeft = (LinearLayout) layoutleft
				.findViewById(R.id.header_layout_imageviewlayout_left);
		mImageButtonLeft = (ImageButton) layoutleft
				.findViewById(R.id.header_ib_imagebutton_left);
		mLinearLayoutLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLeftImageButtonClickListener != null) {
					mLeftImageButtonClickListener.onClick();
				}
			}
		});
	}

	// 初始化右边控件
	private void RightImageButton() {
		View layoutright = mInflater.inflate(
				R.layout.common_header_right_imagebutton, null);
		mLinearLayoutContainerRight.addView(layoutright);
		mLinearLayoutRight = (LinearLayout) layoutright
				.findViewById(R.id.header_layout_buttonlayout_right);
		mButtonRight = (Button) layoutright
				.findViewById(R.id.header_bt_button_right);
		mLinearLayoutRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mRightImageButtonClickListener != null) {
					mRightImageButtonClickListener.onClick();
				}
			}
		});
	}

	public void setDefaultTitle(CharSequence text) {
		if (!TextUtils.isEmpty(text)) {
			mTvSubTitle.setText(text);
		} else {
			mTvSubTitle.setVisibility(View.GONE);
		}
	}

	public void setTitleAndLeftImageButton(CharSequence title, int bacid,
			onLeftImageButtonClickListener listener) {
		setDefaultTitle(title);
		mLinearLayoutContainerLeft.setVisibility(View.VISIBLE);
		if (mImageButtonLeft != null && bacid > 0) {
			mImageButtonLeft.setBackgroundResource(bacid);
			setOnLeftImageButtonClickListener(listener);
		}
		// TODO
		// mLayoutRightContainer.setVisibility(View.INVISIBLE)为什么只有这里设置了可见性;
	}
	

	public void setTitleAndRightImageButton(CharSequence title, int bacid,
			onRightImageButtonClickListener listener) {
		setDefaultTitle(title);
		mLinearLayoutContainerRight.setVisibility(View.VISIBLE);
		if (mButtonRight != null && bacid > 0) {
//			mButtonRight.setWidth(PixelUtil.dip2px(30));
//			mButtonRight.setHeight(PixelUtil.dip2px(30));
//			mButtonRight.setWidth(PixelUtil.dip2px(10, mContext));
//			mButtonRight.setHeight(PixelUtil.dip2px(10, mContext));
			mButtonRight.setBackgroundResource(bacid);
			setOnRightImageButtonClickListener(listener);
		}
	}

	public void setTitleAndRightImageButton(CharSequence title, int bacid,
			String text, onRightImageButtonClickListener listener) {
		setDefaultTitle(text);
		mLinearLayoutContainerRight.setVisibility(View.VISIBLE);
		if (mButtonRight != null && bacid > 0) {
//			mButtonRight.setWidth(PixelUtil.dip2px(45, mContext));
//			mButtonRight.setHeight(PixelUtil.dip2px(40, mContext));
			mButtonRight.setText(text);
			mButtonRight.setBackgroundResource(bacid);
			setOnRightImageButtonClickListener(listener);
		}
	}

	/**
	 * 点击事件接口
	 */
	private onLeftImageButtonClickListener mLeftImageButtonClickListener;
	private onRightImageButtonClickListener mRightImageButtonClickListener;

	public interface onLeftImageButtonClickListener {
		void onClick();
	}

	public interface onRightImageButtonClickListener {
		void onClick();
	}
	
	public void setOnLeftImageButtonClickListener(
			onLeftImageButtonClickListener listener) {
		this.mLeftImageButtonClickListener = listener;
	}

	public void setOnRightImageButtonClickListener(
			onRightImageButtonClickListener listener) {
		this.mRightImageButtonClickListener = listener;
	}

	public Button getRightImageButton(){
		if (mButtonRight!=null) {
			return mButtonRight;
		}
		return null;
	}
	
}
