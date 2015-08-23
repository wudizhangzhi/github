package com.example.im_zzc.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;

import com.example.im_zzc.R;
import com.example.im_zzc.util.FaceTextUtil;
import com.example.im_zzc.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MessageRecentAdapter extends ArrayAdapter<BmobRecent> {

	private List<BmobRecent> mData;
	private Context mContext;
	private LayoutInflater mInflater;
	
	public MessageRecentAdapter(Context context, int resource,
			List<BmobRecent> data) {
		super(context, resource, data);
		
		mContext = context;
		mData = data;
		mInflater = LayoutInflater.from(mContext);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		BmobRecent recent = mData.get(position);
		recentHolder holder = null;
		if (convertView == null) {
			holder = new recentHolder();
			convertView = mInflater.inflate(R.layout.item_conversation, null);
			holder.avatar = (ImageView) convertView
					.findViewById(R.id.recent_item_iv_avatar);
			holder.name = (TextView) convertView
					.findViewById(R.id.recent_item_tv_name);
			holder.msg = (TextView) convertView
					.findViewById(R.id.recent_item_tv_msg);
			holder.time = (TextView) convertView
					.findViewById(R.id.recent_item_tv_time);
			holder.unread = (TextView) convertView
					.findViewById(R.id.recent_item_tv_unread);
			convertView.setTag(holder);
		} else {
			holder = (recentHolder) convertView.getTag();
		}
		String avatar = recent.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, holder.avatar,
					ImageLoadOptions.getOptions());
		} else {
			holder.avatar.setBackgroundResource(R.drawable.head);
		}

		holder.name.setText(recent.getUserName());
		// TODO
		// holder.time.setText(TextUtil.getCharTime(recent.getTime()));

		int type = recent.getType();
		switch (type) {
		case BmobConfig.TYPE_TEXT:
			holder.msg.setText(FaceTextUtil.toSpannableString(mContext,
					recent.getMessage()));
			break;
		case BmobConfig.TYPE_IMAGE:
			holder.msg.setText("[图片]");
			break;
		case BmobConfig.TYPE_LOCATION:
			String all = recent.getMessage();
			if (all != null && !all.equals("")) {
				String address = all.split("&")[0];
				holder.msg.setText("[位置]" + address);
			}
			break;

		case BmobConfig.TYPE_VOICE:
			holder.msg.setText("[声音]");
			break;

		default:
			break;
		}

		// 设置未读标记
		int num = BmobDB.create(mContext).getUnreadCount(recent.getTargetid());
		if (num > 0) {
			holder.unread.setVisibility(View.VISIBLE);
			holder.unread.setText(num + "");
		} else {
			holder.unread.setVisibility(View.GONE);
		}
		return convertView;
	}

	class recentHolder {
		ImageView avatar;
		TextView name, msg, time, unread;
	}
}
