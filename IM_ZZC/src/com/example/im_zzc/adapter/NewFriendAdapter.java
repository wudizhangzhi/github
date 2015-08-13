package com.example.im_zzc.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.R;
import com.example.im_zzc.adapter.base.BaseListAdapter;
import com.example.im_zzc.adapter.base.ViewHolder;
import com.example.im_zzc.util.CollectionUtils;
import com.example.im_zzc.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewFriendAdapter extends BaseListAdapter<BmobInvitation> {
//	private Context mContext;
//	private List<BmobInvitation> mList;
//	private LayoutInflater mInflater;

	public NewFriendAdapter( Context context,List<BmobInvitation> list) {
		super(context,list );
//		this.mContext = context;
//		this.mList = list;
//		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_add_friend, null);
		}
		ImageView iv_avatar = ViewHolder.get(convertView,
				R.id.addnewfriend_item_avatar);
		TextView tv_name = ViewHolder.get(convertView,
				R.id.addnewfriend_item_name);
		final Button bt_agree = ViewHolder.get(convertView,
				R.id.addnewfriend_item_btn_add);

		final BmobInvitation invitation = getList().get(position);
		String avatar = invitation.getAvatar();
		if (!TextUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.default_head);
		}
		tv_name.setText(invitation.getFromname());

		int status = invitation.getStatus();
		if (status == BmobConfig.INVITE_ADD_NO_VALI_RECEIVED
				|| status == BmobConfig.INVITE_ADD_NO_VALIDATION) {
			//请求收到未确认||请求未确认
			bt_agree.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					agreeAdd(bt_agree,invitation);
				}
			});
		}else if(status==BmobConfig.INVITE_ADD_AGREE){
			bt_agree.setText("已同意");
			bt_agree.setEnabled(false);
			bt_agree.setBackgroundDrawable(null);
			bt_agree.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
		}
		return convertView;
	}

	
	private void agreeAdd(final Button v, BmobInvitation invitation) {
		// TODO Auto-generated method stub
		final ProgressDialog progress=new ProgressDialog(mContext);
		progress.setMessage("正在添加...");
		progress.show();
		try {
			BmobUserManager.getInstance(mContext).agreeAddContact(invitation, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					progress.dismiss();
					CustomApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(mContext).getContactList()));
					v.setText("已同意");
					v.setEnabled(false);
					v.setBackgroundDrawable(null);
					v.setTextColor(mContext.getResources().getColor(R.color.base_color_text_black));
				}
				
				@Override
				public void onFailure(int arg0, String arg1) {
					showToast("添加失败:"+arg1);
				}
			});
		} catch (Exception e) {
			showToast("添加失败:"+e.getMessage());
			progress.dismiss();
		}
		
	}
}
