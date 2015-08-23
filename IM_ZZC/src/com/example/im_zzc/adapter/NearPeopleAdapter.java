package com.example.im_zzc.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.R;
import com.example.im_zzc.adapter.base.BaseListAdapter;
import com.example.im_zzc.adapter.base.ViewHolder;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NearPeopleAdapter extends BaseListAdapter<User> {

	public NearPeopleAdapter(Context context, List<User> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.item_near_people,null);
		}
		User user=getList().get(position);
		TextView tv_name = ViewHolder.get(convertView, R.id.tv_name);
		TextView tv_distance = ViewHolder.get(convertView, R.id.tv_distance);
		TextView tv_logintime = ViewHolder.get(convertView, R.id.tv_logintime);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.item_chat_iv_avatar);
		String avatar=user.getAvatar();
		if (avatar != null &&!avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions());
		}else {
			iv_avatar.setBackgroundResource(R.drawable.head);
		}
		BmobGeoPoint location = user.getLocation();
		String currentLat = CustomApplication.getInstance().getLatitude();
		String currentLong = CustomApplication.getInstance().getLongitude();
		if (location!=null && !currentLat.equals("") && !currentLong.equals("")) {
			double distance = DistanceOfTwoPoints(Double.parseDouble(currentLat),Double.parseDouble(currentLong),location.getLatitude(), 
					location.getLongitude());
			tv_distance.setText(String.valueOf(distance)+"米");
		}else {
			tv_distance.setText("未知");
		}
		tv_name.setText(user.getUsername());
		tv_logintime.setText("最近登陆时间："+user.getUpdatedAt());
		return convertView;
	}
	//TODO 理解计算过程
	private static final double EARTH_RADIUS = 6378137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 根据两点间经纬度坐标（double值），计算两点间距离，
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return 单位是米
	 */
	public static double DistanceOfTwoPoints(double lat1, double lng1,
			double lat2, double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
	
}
