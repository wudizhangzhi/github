package com.example.im_zzc.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.task.BRequest;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.ActivityBase;
import com.example.im_zzc.adapter.AddFriendAdapter;
import com.example.im_zzc.util.CollectionUtils;
import com.example.im_zzc.view.xlist.XListView;
import com.example.im_zzc.view.xlist.XListView.XListViewListener;
/**
 * 添加新好友的页面
 */
public class AddNewFriendActivity extends ActivityBase implements OnClickListener, XListViewListener, OnItemClickListener {
	private EditText edt_search_name;
	private Button bt_search;
	private XListView mListView;
	
	private AddFriendAdapter mAdapter;
	
	private List<BmobChatUser> users=new ArrayList<BmobChatUser>();
	private String searchName;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_add_newfriends);
		initView();
	}

	private void initView() {
		initTopBarForLeft("添加好友");
		edt_search_name=(EditText) findViewById(R.id.addnewfriend_et_find_name);
		bt_search=(Button) findViewById(R.id.addnewfriend_btn_search);
		bt_search.setOnClickListener(this);
		initListView();
	}

	private void initListView() {
		mListView=(XListView) findViewById(R.id.addnewfriend_list_search);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mAdapter=new AddFriendAdapter( this,users);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}
	
	/**
	 * 搜索按钮
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addnewfriend_btn_search:
			users.clear();
			searchName=edt_search_name.getText().toString();
			if (searchName!=null&&!searchName.equals("")) {
				initSearchList(false);
			}else {
				showToast("请输入用户名");
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		
	}

	@Override
	public void onLoadMore() {
		userManager.querySearchTotalCount(searchName, new CountListener() {
			
			@Override
			public void onSuccess(int arg0) {
				if (arg0>curPage) {
					curPage++;
					queryMoreSearchList(curPage);
				}else {
					mListView.setPullLoadEnable(false);
					showToast("没有更多了");
					initLoad();
				}
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				initLoad();
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO position为什么要-1
		BmobChatUser user=(BmobChatUser) mAdapter.getItem(position);
		Intent intent=new Intent(this,SetMyInfoActivity.class);
		intent.putExtra("from", "add");
		intent.putExtra("username", user.getUsername());
		startAnimActivity(intent);
	}
	
	
	private int curPage;
	ProgressDialog progress;
	/**
	 * 更新搜索列表
	 * @param isUpdate是否是更新，或者第一次
	 */
	public void initSearchList(final boolean isUpdate){
		if (!isUpdate) {
			progress=new ProgressDialog(this);
			progress.setMessage("搜索中...");
			progress.setCanceledOnTouchOutside(false);
			progress.show();
		}
		userManager.queryUserByPage(isUpdate, 0, searchName, new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				if (!isUpdate) {
					users.clear();
				}
				mListView.setPullLoadEnable(false);
				initLoad();
				showToast("用户不存在");
				//确保每次查询从头开始
				curPage=0;
				
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				Log.i("test", "size:"+arg0.size());
				if (CollectionUtils.isNotNull(arg0)) {
					if (!isUpdate) {
						users.clear();//不是加载更多的话清除数据
					}
//					users.addAll(arg0);
					mAdapter.addAll(arg0);
					if (arg0.size()<BRequest.QUERY_LIMIT_COUNT) {
						mListView.setPullLoadEnable(false);
						showToast("已经加载全部");
					}else {
						mListView.setPullLoadEnable(true);
					}
				}else {
					//返回是空
					if (!isUpdate) {
						users.clear();
					}
					showToast("用户不存在");
				}
				if (!isUpdate) {
					progress.dismiss();
				}else {
					//停止加载更多
					initLoad();
				}
				//TODO 确保每次查询从头开始？为什么不判断是否是加载更多
				curPage=0;
			}
		});
	}
	
	private void queryMoreSearchList(int page){
		userManager.queryUserByPage(true, page, searchName, new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				mListView.setPullLoadEnable(false);
				initLoad();
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				if (CollectionUtils.isNotNull(arg0)) {
					users.addAll(arg0);
				}
				initLoad();
			}
		});
	}
	
	private void initRefresh(){
		if (mListView.getPullRefreshing()) {
			mListView.stopRefreshing();
		}
	}
	
	private void initLoad(){
		if (mListView.getPullLoading()) {
			mListView.stopLoadMore();
		}
	}
}
