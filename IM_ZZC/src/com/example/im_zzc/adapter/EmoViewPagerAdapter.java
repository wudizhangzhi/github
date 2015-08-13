package com.example.im_zzc.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class EmoViewPagerAdapter extends PagerAdapter {
	private List<View> view;
	
	
	
	public EmoViewPagerAdapter(List<View> view) {
		super();
		this.view = view;
	}

	@Override
	public int getCount() {
		return view.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager)container).removeView(view.get(position));
	}
	
	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager)container).addView(view.get(position));
		return view.get(position);
				
	}
}
