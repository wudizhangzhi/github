package com.example.im_zzc.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.ActivityBase;
import com.example.im_zzc.adapter.NewFriendAdapter;
import com.example.im_zzc.view.HeaderLayout;
import com.example.im_zzc.view.dialog.DialogTip;
import com.example.im_zzc.view.dialog.DialogTip.onPositiveButtonClickListener;
import com.example.im_zzc.view.xlist.XListView;

public class NewFriendActivity extends ActivityBase implements OnItemLongClickListener {
	private ListView mListView;
	private NewFriendAdapter mAdapter;
	private String from = "";

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.activity_new_friend);
		from=getIntent().getStringExtra("from");
		init();

	}

	private void init() {
		initTopBarForLeft("好友请求");
		mListView =  (ListView) findViewById(R.id.newfriend_list_newfriend);

		mAdapter = new NewFriendAdapter(this, BmobDB.create(this)
				.queryBmobInviteList());
		mListView.setOnItemLongClickListener(this);
		mListView.setAdapter(mAdapter);

		if (from == null) {//空：来自通知栏；跳转到最近一条
			mListView.setSelection(mAdapter.getCount());
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		showDeleteDialog(position, (BmobInvitation) mAdapter.getItem(position));
		return true;
	}
	
	private void showDeleteDialog(final int position,final BmobInvitation invitation){
		DialogTip dialog=new DialogTip(this, invitation.getFromname(), "删除好友请求", "确定", "取消", true);
		dialog.setOnPositiveButtonClickListener(new onPositiveButtonClickListener() {
			
			@Override
			public void onClick() {
				deleteInvitation(position,invitation);
			}
		});
		dialog.show();
		dialog=null;
	}

	private void deleteInvitation(int position,BmobInvitation invitation) {
		mAdapter.remove(position);
		BmobDB.create(this).deleteInviteMsg(invitation.getFromid(),Long.toString(invitation.getTime()));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (from==null) {
			startAnimActivity(MainActivity.class);
		}
	}
}
