package com.example.im_zzc.view.xlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.im_zzc.R;

public class XListViewFooter extends LinearLayout {
	private ProgressBar mProgressbar;
	private TextView mTextView_Hint;
	private LinearLayout mContainer;

	public static final int STATE_NORMAL = 0;
	public static final int STATE_READY = 1;
	public static final int STATE_REFRESHING = 2;

	public XListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public XListViewFooter(Context context) {
		this(context, null);
	}

	private void initView(Context context) {
		// TODO Auto-generated method stub
		mContainer = (LinearLayout) LayoutInflater.from(context)
				.inflate(R.layout.xlistview_footer, null);

		LinearLayout.LayoutParams param = new LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		addView(mContainer, param);

		mProgressbar = (ProgressBar) mContainer
				.findViewById(R.id.xlistview_footer_progressbar);
		mTextView_Hint = (TextView) mContainer
				.findViewById(R.id.xlistview_footer_hint_textview);
	}

	public void setState(int state) {
		mProgressbar.setVisibility(View.INVISIBLE);
		mTextView_Hint.setVisibility(View.INVISIBLE);
		switch (state) {
		case STATE_NORMAL:
			mTextView_Hint.setVisibility(View.VISIBLE);
			mTextView_Hint.setText("查看更多");
			break;

		case STATE_READY:
			mTextView_Hint.setVisibility(View.VISIBLE);
			mTextView_Hint.setText("松开载入更多");
			break;
		case STATE_REFRESHING:
			mProgressbar.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}
	
	public void hide(){
		LinearLayout.LayoutParams param=(LayoutParams) mContainer.getLayoutParams();
		param.height=0;
		mContainer.setLayoutParams(param);
	}
	
	public void show(){
		LinearLayout.LayoutParams param=(LayoutParams) mContainer.getLayoutParams();
		param.height=LinearLayout.LayoutParams.WRAP_CONTENT;
		mContainer.setLayoutParams(param);
	}
	
	public int getBottomMargin(){
		LinearLayout.LayoutParams param=(LayoutParams) mContainer.getLayoutParams();
		return param.bottomMargin;
	}
	
	public void setBottomMargin(int margin){
		if (margin<0) {
			return;
		}
		LinearLayout.LayoutParams param=(LayoutParams) mContainer.getLayoutParams();
		param.bottomMargin=margin;
		mContainer.setLayoutParams(param);
	}
}
