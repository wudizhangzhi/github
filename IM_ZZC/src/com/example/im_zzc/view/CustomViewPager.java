package com.example.im_zzc.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 显示图片的viewpager
 */
public class CustomViewPager extends ViewPager {
	private boolean isEnable = true;

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (isEnable) {
			try {
				return super.onInterceptTouchEvent(arg0);
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (isEnable) {
			return super.onTouchEvent(arg0);
		}else {
			return false;
		}
	}
	
	@Override
	public void setAdapter(PagerAdapter arg0) {
		super.setAdapter(arg0);
	}
	
	public void setAdapter(PagerAdapter arg0,int index) {
		super.setAdapter(arg0);
		setCurrentItem(index);
	}
	
	public void setEnableTouchScroll(boolean enable){
		isEnable=enable;
	}
}


