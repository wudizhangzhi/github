package com.example.im_zzc.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.ActivityBase;
import com.example.im_zzc.adapter.BlackListAdapter;
import com.example.im_zzc.view.dialog.DialogTip;
import com.example.im_zzc.view.dialog.DialogTip.onPositiveButtonClickListener;

/**
 * 黑名单界面
 * 
 * @author wudizhangzhi
 */
public class BlackListActivity extends ActivityBase implements
		OnItemClickListener {

	private ListView mListView;
	private BlackListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_blacklist);
		initView();
	}

	private void initView() {
		initTopBarForLeft("黑名单");
		mListView = (ListView) findViewById(R.id.list_blacklist);
		mAdapter = new BlackListAdapter(this, BmobDB.create(this)
				.getBlackList());
		ArrayList<BmobChatUser> list = new ArrayList<BmobChatUser>();
		list = (ArrayList<BmobChatUser>) BmobDB.create(this).getBlackList();
		for (int i = 0; i < list.size(); i++) {
			Log.i("测试黑名单", "一共："+ list.size()+";"+list.get(i).getUsername());
		}

		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		BmobChatUser user = (BmobChatUser) mAdapter.getItem(position);
		showRemoveBlackDialog(position, user);
	}

	private void showRemoveBlackDialog(final int position,
			final BmobChatUser user) {
		DialogTip dialog = new DialogTip(this, "移出黑名单", "你确定要将"
				+ user.getUsername() + "移出黑名单吗？", "确定", "取消", true);
		dialog.setOnPositiveButtonClickListener(new onPositiveButtonClickListener() {

			@Override
			public void onClick() {
				mAdapter.remove(position);
				userManager.removeBlack(user.getUsername(),
						new UpdateListener() {

							@Override
							public void onSuccess() {
								showToast("移出黑名单成功");
							}

							@Override
							public void onFailure(int arg0, String arg1) {
								showToast("移出黑名单失败：" + arg1);
							}
						});
			}
		});
		dialog.show();
		dialog = null;
	}
}
