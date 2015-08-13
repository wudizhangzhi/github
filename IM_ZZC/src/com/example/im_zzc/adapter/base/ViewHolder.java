package com.example.im_zzc.adapter.base;

import android.util.SparseArray;
import android.view.View;

public class ViewHolder {
	//TODO 为什么<T extends View>T，而不是直接view
	public static <T extends View>T get(View view,int id){
		SparseArray<View> viewHolder=(SparseArray<View>) view.getTag();
		if (viewHolder==null) {
			viewHolder=new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView=viewHolder.get(id);
		if (childView==null) {
			childView=view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
