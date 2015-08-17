package com.example.im_zzc.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobUser;

import com.example.im_zzc.R;
import com.example.im_zzc.adapter.base.BaseListAdapter;
import com.example.im_zzc.adapter.base.ViewHolder;
import com.example.im_zzc.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BlackListAdapter extends BaseListAdapter<BmobChatUser> {

	public BlackListAdapter(Context context, List<BmobChatUser> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.item_blacklist, null);
		}
		ImageView head=ViewHolder.get(convertView, R.id.img_friend_avatar);
		TextView name=ViewHolder.get(convertView, R.id.tv_friend_name);
		BmobChatUser item=(BmobChatUser) getList().get(position);
		String avatar=item.getAvatar();
		if (!avatar.equals("")&&avatar!=null) {
			ImageLoader.getInstance().displayImage(avatar, head, ImageLoadOptions.getOptions());
		}else {
			head.setBackgroundResource(R.drawable.head);
		}
		name.setText(item.getUsername());
		return convertView;
	}
}
