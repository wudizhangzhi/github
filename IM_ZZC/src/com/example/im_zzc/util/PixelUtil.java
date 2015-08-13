package com.example.im_zzc.util;

import android.content.Context;
import android.content.res.Resources;

import com.example.im_zzc.CustomApplication;

public class PixelUtil {
	private static Context mContext = CustomApplication.getInstance();
	
	/**
	 * dip转化为pixel
	 * 
	 * @param value
	 * @param context
	 * @return
	 */
	public static int dip2px(float value, Context context) {
		final float scale = context.getResources().getDisplayMetrics().densityDpi;
		return (int) ((value * 160) / scale + 0.5f);
	}
	
	public static int dip2px(float value){
		final float scale = mContext.getResources().getDisplayMetrics().densityDpi;
		return (int) ((value * 160) / scale + 0.5f);
	}
	

	public static int sp2px(float value) {
		Resources r;
		if (mContext == null) {
			r = Resources.getSystem();
		} else {
			r = mContext.getResources();
		}
		float spvalue = value * r.getDisplayMetrics().scaledDensity;
		return (int) (spvalue + 0.5f);
	}

	/**
	 * sp转化为px.
	 */
	public static int sp2px(float value, Context context) {
		Resources r;
		if (context == null) {
			r = Resources.getSystem();
		} else {
			r = context.getResources();
		}
		float spvalue = value * r.getDisplayMetrics().scaledDensity;
		return (int) (spvalue + 0.5f);
	}
}
