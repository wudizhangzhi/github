package com.example.im_zzc.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.im_zzc.R;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.util.ImageLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserFriendAdapter extends BaseAdapter implements SectionIndexer{
	private Context context;
	private List<User> data;
	private LayoutInflater inflater;
	
	
	public UserFriendAdapter(Context context, List<User> data) {
		super();
		this.context = context;
		this.data = data;
		inflater=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void updateListView(List< User> data){
		this.data=data;
		notifyDataSetChanged();
	}
	
	public void remove(User user){
		this.data.remove(user);
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if (convertView==null) {
			holder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_user_friend, null);
			holder.alpha=(TextView) convertView.findViewById(R.id.contact_item_tv_alpha);
			holder.avatar=(ImageView) convertView.findViewById(R.id.contact_item_iv_avatar);
			holder.name=(TextView) convertView.findViewById(R.id.contact_item_tv_name);
			convertView.setTag(holder);
		}else {
			holder=(ViewHolder) convertView.getTag();
		}
		User friend=data.get(position);
		String avatar=friend.getAvatar();
		String name=friend.getUsername();
		
		if (!TextUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, holder.avatar, ImageLoadOptions.getOptions());
		}else {
			holder.avatar.setImageDrawable(context.getResources().getDrawable(R.drawable.head));
		}
		holder.name.setText(name);
		int section=getSectionForPosition(position);
		if (getPositionForSection(section)==position) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(friend.getSortLetters());
		}else {
			holder.alpha.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 根据首字母ascii值获得listview第一次出现该首字母的位置
	 * @param sectionIndex
	 * @return
	 */
	@Override
	public int getPositionForSection(int sectionIndex) {
		// TODO Auto-generated method stub
		for (int i = 0; i < data.size(); i++) {
			int ascii=data.get(i).getSortLetters().charAt(0);
			if (ascii==sectionIndex) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * 根据位置获得首字母的char ascii值
	 * @param position
	 * @return
	 */
	@Override
	public int getSectionForPosition(int position) {
		return data.get(position).getSortLetters().charAt(0);
	}
	
	static class ViewHolder{
		TextView name,alpha;
		ImageView avatar;
	}
}
