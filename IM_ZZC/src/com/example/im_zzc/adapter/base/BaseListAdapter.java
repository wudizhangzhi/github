package com.example.im_zzc.adapter.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.im_zzc.bean.User;

import cn.bmob.im.bean.BmobChatUser;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

public abstract class BaseListAdapter<E> extends BaseAdapter {
	public List<E> list;
	public Context mContext;
	public LayoutInflater mInflater;
	public BmobChatUser mTargetUser;
	public BmobChatUser mUser;
	
	public BaseListAdapter( Context context,List<E> list) {
		super();
		this.list = list;
		this.mContext = context;
		mInflater=LayoutInflater.from(context);
	}
	
	public BaseListAdapter( Context context,List<E> list,BmobChatUser target,BmobChatUser user) {
		super();
		this.list = list;
		this.mContext = context;
		mInflater=LayoutInflater.from(context);
		this.mTargetUser=target;
		this.mUser=user;
	}

	public List<E> getList(){
		return list;
	}
	
	public void setList(List<E> list){
		this.list=list;
		notifyDataSetChanged();
	}
	
	public void remove(int position){
		this.list.remove(position);
		notifyDataSetChanged();
	}
	
	public void addAll(List<E> list){
		this.list.addAll(list);
		notifyDataSetChanged();
	}
	
	public void add(E e){
		this.list.add(e);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView=bindView( position,convertView, parent);
		addInternalClickListener(convertView, position, list.get(position));
		return convertView;
	}
	
	public abstract View bindView(int position, View convertView, ViewGroup parent);
	
	/**
	 * Integer : view的id
	 */
	public Map<Integer, onInternalClickListener> canClickItem;
	
	public interface onInternalClickListener{
		public void OnClickListener(View parentV, View v, Integer position,
				Object values);
	}
	
	public void setOnInternalClickListener(Integer key, onInternalClickListener listener){
		if (canClickItem==null) {
			canClickItem=new HashMap<Integer, BaseListAdapter.onInternalClickListener>();
		}
		canClickItem.put(key, listener);
	}
	
	/**
	 * 给item里面的view添加点击监听
	 * @param itemV 母体view
	 * @param position 位置
	 * @param valuesMap list内容
	 */
	private  void addInternalClickListener(final View itemV, final Integer position,final Object valuesMap){
		if (canClickItem!=null) {
			for (Integer key : canClickItem.keySet()) {
				View inView=itemV.findViewById(key);
				final onInternalClickListener inviewListener=canClickItem.get(key);
				if (inView!=null&&inviewListener!=null) {
					inView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							inviewListener.OnClickListener(itemV, v, position, valuesMap);
						}
					});
				}
			}
		}
	} 
	
	Toast mToast;
	public void showToast(final String text){
		if (!TextUtils.isEmpty(text)) {
			((Activity)mContext).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if (mToast==null) {
						mToast=Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
					}else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});
		}
	}
}
