package com.example.im_zzc.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.v3.listener.PushListener;

import com.example.im_zzc.R;
import com.example.im_zzc.adapter.base.BaseListAdapter;
import com.example.im_zzc.adapter.base.ViewHolder;
import com.example.im_zzc.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AddFriendAdapter extends BaseListAdapter<BmobChatUser> {
	
	
	
	public AddFriendAdapter( Context context,List<BmobChatUser> list) {
		super( context,list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView==null) {
			convertView=mInflater.inflate(R.layout.item_add_friend, null);
		}
		final BmobChatUser contact=getList().get(position);
		TextView tv_name=ViewHolder.get(convertView,R.id.addnewfriend_item_name);
		Button btn_add=ViewHolder.get(convertView,R.id.addnewfriend_item_btn_add);
		ImageView iv_avatar=ViewHolder.get(convertView,R.id.addnewfriend_item_avatar);
		
		String avatar=contact.getAvatar();
		if (avatar!=null&&avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.default_head);
		}
		tv_name.setText(contact.getUsername());
		btn_add.setText("添加");
		btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final ProgressDialog progress=new ProgressDialog(mContext); 
				progress.setMessage("添加中...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				BmobChatManager.getInstance(mContext).sendTagMessage(MsgTag.ADD_CONTACT, contact.getObjectId(), new PushListener() {
					
					@Override
					public void onSuccess() {
						showToast("发送请求成功");
						progress.dismiss();
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						showToast("发送请求失败");
						progress.dismiss();
					}
				});
			}
		});
		return convertView;
	}

}
