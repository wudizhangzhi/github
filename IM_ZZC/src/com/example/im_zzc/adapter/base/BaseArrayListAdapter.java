package com.example.im_zzc.adapter.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.im_zzc.bean.FaceText;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class BaseArrayListAdapter extends BaseAdapter {
	protected Context mContext;
	protected List<FaceText> mDatas=new ArrayList<FaceText>();
	protected LayoutInflater mInflater;
	
	public BaseArrayListAdapter(Context context, FaceText... datas) {
		super();
		this.mContext = context;
		mInflater=LayoutInflater.from(context);
		if (datas!=null&&datas.length>0) {
			mDatas=Arrays.asList(datas);
		}
	}
	
	public BaseArrayListAdapter(Context context, List<FaceText> datas) {
		super();
		this.mContext = context;
		mInflater=LayoutInflater.from(context);
		if (datas!=null&&datas.size()>0) {
			mDatas=datas;
		}
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}

}
