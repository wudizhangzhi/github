package com.example.im_zzc.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.example.im_zzc.R;

public class ClearEditText extends EditText implements OnFocusChangeListener,
		TextWatcher {
	private Drawable mClearDrawable;

	public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public ClearEditText(Context context, AttributeSet attrs) {
		//这里构造方法也很重要，不加这个很多属性不能再XML里面定义
		this(context, attrs, android.R.attr.editTextStyle);
	}

	public ClearEditText(Context context) {
		this(context, null);
	}

	private void init() {
		// 获得edittextview右边的drawable
		// getCompoundDrawables()：Returns drawables for the left, top, right,
		// and bottom borders.
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			mClearDrawable = getResources()
					.getDrawable(R.drawable.search_clear);
		}
		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
				mClearDrawable.getIntrinsicHeight());
		setClearIconVisible(false);
		setOnFocusChangeListener(this);
		addTextChangedListener(this);
	}

	/**
	 * 设置图标可见性
	 * 
	 * @param b
	 */
	private void setClearIconVisible(boolean b) {
		Drawable right = b ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0],
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after) {
		setClearIconVisible(s.length() > 0);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	/**
	 * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		} else {
			setClearIconVisible(false);
		}
	}

	/**
	 * 手指松开时判断x坐标位置 大于（控件宽度-右icon宽度-右padding）且小于控件宽度时候 判定为按下了清除按钮
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getCompoundDrawables()[2] != null) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (event.getX() > (getWidth() - getPaddingRight() - mClearDrawable
						.getIntrinsicWidth()) && event.getX() < getWidth()) {
					// 清除输入框
					setText("");
				}
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 设置晃动动画
	 */
	public void setShakeAnimation() {
		this.setAnimation(ShakeAnimation(5));
	}

	/**
	 * 晃动动画
	 * 
	 * @param count
	 * @return
	 */
	public static Animation ShakeAnimation(int count) {
		Animation translateAnim = new TranslateAnimation(0, 10, 0, 0);
		translateAnim.setInterpolator(new CycleInterpolator(count));
		translateAnim.setDuration(1000);
		return translateAnim;
	}
}
