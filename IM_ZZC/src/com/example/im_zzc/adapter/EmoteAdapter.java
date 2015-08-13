package com.example.im_zzc.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.im_zzc.R;
import com.example.im_zzc.adapter.base.BaseArrayListAdapter;
import com.example.im_zzc.bean.FaceText;

public class EmoteAdapter extends BaseArrayListAdapter {

	public EmoteAdapter(Context context, List<FaceText> datas) {
		super(context, datas);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_face_text, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView
					.findViewById(R.id.item_iv_facetext);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FaceText facetext = (FaceText) getItem(position);
		String key = facetext.text.substring(1);
		Drawable drawable = mContext.getResources().getDrawable(
				mContext.getResources().getIdentifier(key, "drawable",
						mContext.getPackageName()));
		holder.iv.setImageDrawable(drawable);
		return convertView;
	}

	class ViewHolder {
		ImageView iv;
	}
}
