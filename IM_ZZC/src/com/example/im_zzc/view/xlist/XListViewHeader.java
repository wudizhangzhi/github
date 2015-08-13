package com.example.im_zzc.view.xlist;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.im_zzc.R;

public class XListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ImageView mImageView_arrow;
	private ProgressBar mProgressbar;
	private TextView mTextView_TimeLabel, mTextView_Time, mTextView_Hint;

	private Animation mRotateUpAnim, mRotateDownAnim;

	public static final int STATE_NORMAL = 0;// 正常状态
	public static final int STATE_READY = 1;// 准备
	public static final int STATE_REFRESHLING = 2;// 刷新
	public static final int ANIMATION_TIME = 180;

	private int mState = STATE_NORMAL;

	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public XListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams param = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.xlistview_header, null);
		addView(mContainer, param);
		// TODO 为什么是bottom
		setGravity(Gravity.BOTTOM);

		mImageView_arrow = (ImageView) mContainer
				.findViewById(R.id.xlistview_header_arrow);
		mProgressbar = (ProgressBar) mContainer
				.findViewById(R.id.xlistview_header_progressbar);
		mTextView_TimeLabel = (TextView) mContainer
				.findViewById(R.id.xlistview_header_time_label);
		mTextView_Time = (TextView) mContainer
				.findViewById(R.id.xlistview_header_time);
		mTextView_Hint = (TextView) mContainer.findViewById(R.id.xlistview_header_hint_textview);

		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateUpAnim.setDuration(ANIMATION_TIME);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		mRotateDownAnim.setDuration(ANIMATION_TIME);
		mRotateDownAnim.setFillAfter(true);
	}

	/**
	 * 设置状态
	 * 
	 * @param state
	 */
	public void setState(int state) {
		if (state == mState) {
			return;
		}
		if (state == STATE_REFRESHLING) {
			mImageView_arrow.setVisibility(View.INVISIBLE);
			mProgressbar.setVisibility(View.VISIBLE);
		} else {
			mImageView_arrow.setVisibility(View.VISIBLE);
			mProgressbar.setVisibility(View.INVISIBLE);
		}
		switch (state) {
		case STATE_NORMAL:
			if (mState == STATE_READY) {
				mImageView_arrow.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHLING) {
				mImageView_arrow.clearAnimation();
			}
			mTextView_Hint.setText("下拉刷新");
			break;
		case STATE_READY:
			if (mState!= STATE_READY) {
				mImageView_arrow.clearAnimation();
				mImageView_arrow.startAnimation(mRotateUpAnim);
				mTextView_Hint.setText("松开手指刷新");
			} 
			
			break;
		case STATE_REFRESHLING:
			mTextView_Hint.setText("刷新成功");
			Time time=new Time();
			time.setToNow();
			setRefreshTime(time.format("%Y-%M-%D %T"));
			break;
		default:
			break;
		}
	}

	public void setRefreshTime(String time) {
		// TODO Auto-generated method stub
		mTextView_Hint.setVisibility(View.VISIBLE);
		mTextView_Hint.setText(time);
	}
	
	/**
	 * 设置可见的高度
	 * @param height
	 */
	public void setVisiableHeight(int height){
		if (height<=0) {
			height=0;
		}
		LinearLayout.LayoutParams param=(LayoutParams) mContainer.getLayoutParams();
		param.height=height;
		mContainer.setLayoutParams(param);
	}
	
	public int getVisiableHeight(){
		return mContainer.getHeight();
	}
}
