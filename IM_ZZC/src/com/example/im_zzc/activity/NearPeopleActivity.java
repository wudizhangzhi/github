package com.example.im_zzc.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.bmob.im.task.BRequest;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.ActivityBase;
import com.example.im_zzc.adapter.NearPeopleAdapter;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.util.CollectionUtils;
import com.example.im_zzc.view.xlist.XListView;
import com.example.im_zzc.view.xlist.XListView.XListViewListener;

public class NearPeopleActivity extends ActivityBase implements
		OnItemClickListener, XListViewListener {
	XListView mListView;
	NearPeopleAdapter adapter;

	List<User> nears = new ArrayList<User>();

	private double QUERY_KILOMETERS = 1000;// 默认查询1000公里范围内的人

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_near_friend);
		
		initView();
	}

	private void initView() {
		initTopBarForLeft("附近的人");
		initXListView();
	}

	private void initXListView() {
		mListView = (XListView) findViewById(R.id.list_near);
		mListView.setOnItemClickListener(this);
		// 允许下拉
		mListView.setPullRefreshEnable(true);
		// 不允许加载更多
		mListView.setPullLoadEnable(false);
		// 设置监听
		mListView.setXListViewListener(this);
		// 进入后立即刷新
		mListView.pullRefreshing();

		adapter = new NearPeopleAdapter(this, nears);
		mListView.setAdapter(adapter);

		initNearByList(false);
	}
	

	int curPage = 0;
	ProgressDialog progress;

	/**
	 * 初始化列表
	 * 
	 * @param isUpdate
	 */
	private void initNearByList(final boolean isUpdate) {
		if (!isUpdate) {// 不是刷新
			progress = new ProgressDialog(NearPeopleActivity.this);
			progress.setMessage("正在查询附近的人...");
			progress.setCanceledOnTouchOutside(true);
			progress.show();
		}
		if (!mApplication.getLatitude().equals("")
				&& !mApplication.getLongitude().equals("")) {
			double latitude = Double.parseDouble(mApplication.getLatitude());
			double longitude = Double.parseDouble(mApplication.getLongitude());
			// bmob封装的查询方法，当进入此页面时 isUpdate为false，当下拉刷新的时候设置为true就行。
			userManager.queryKiloMetersListByPage(isUpdate, 0, "location",
					longitude, latitude, true, QUERY_KILOMETERS, "sex", null,
					new FindListener<User>() {

						@Override
						public void onError(int arg0, String arg1) {
							showToast("查询失败：" + arg1);
							mListView.setPullLoadEnable(false);
							if (!isUpdate) {
								progress.dismiss();
							} else {
								refreshPull();
							}
						}

						@Override
						public void onSuccess(List<User> arg0) {
							Log.i("附近的人", "查询成功："+arg0.size());
							if (CollectionUtils.isNotNull(arg0)) {
								if (isUpdate) {
									nears.clear();
								}
								adapter.addAll(arg0);
								if (arg0.size() < BRequest.QUERY_LIMIT_COUNT) {
									mListView.setPullLoadEnable(false);
									showToast("附近的人搜索完成!");
								} else {
									mListView.setPullLoadEnable(true);
								}

							} else {
								showToast("暂无附近的人");
							}

							if (!isUpdate) {
								progress.dismiss();
							} else {
								refreshPull();
							}
						}

					});
		}else {
			showToast("没有您的位置");
		}
		if (!isUpdate && progress != null) {
			progress.dismiss();
		}
	}

	/**
	 * 查询更多
	 * 
	 * @param page
	 */
	private void queryMoreNearList(int page) {
		double latitude = Double.parseDouble(mApplication.getLatitude());
		double longtitude = Double.parseDouble(mApplication.getLongitude());
		// 查询10公里范围内的性别为女的用户列表
		userManager.queryKiloMetersListByPage(true, page, "location",
				longtitude, latitude, true, QUERY_KILOMETERS, "sex", null,
				new FindListener<User>() {
					// 查询全部地理位置信息且性别为女性的用户列表
					// userManager.queryNearByListByPage(true,page, "location",
					// longtitude, latitude, true,"sex",false,new
					// FindListener<User>() {

					@Override
					public void onSuccess(List<User> arg0) {
						if (CollectionUtils.isNotNull(arg0)) {
							adapter.addAll(arg0);
						}
						refreshLoad();
					}

					@Override
					public void onError(int arg0, String arg1) {
						Log.i("附近的人", "查询更多附近的人出错:" + arg1);
						mListView.setPullLoadEnable(false);
						refreshLoad();
					}

				});
	}

	private void refreshLoad() {
		if (mListView.getPullLoading()) {
			mListView.stopLoadMore();
		}
	}

	private void refreshPull() {
		if (mListView.getPullRefreshing()) {
			mListView.stopRefreshing();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		User user = (User) adapter.getItem(position);
		Intent intent = new Intent(this, SetMyInfoActivity.class);
		intent.putExtra("from", "add");
		intent.putExtra("username", user.getUsername());
		startAnimActivity(intent);
	}

	@Override
	public void onRefresh() {
		initNearByList(true);
	}

	@Override
	public void onLoadMore() {
		double latitude = Double.parseDouble(mApplication.getLatitude());
		double longtitude = Double.parseDouble(mApplication.getLongitude());
		// 这是查询10公里范围内的性别为女用户总数
		userManager.queryKiloMetersTotalCount(User.class, "location",
				longtitude, latitude, true, QUERY_KILOMETERS, "sex", null,
				new CountListener() {
			
					@Override
					public void onSuccess(int arg0) {
						if (arg0 > nears.size()) {
							curPage++;
							queryMoreNearList(curPage);
						} else {
							showToast("数据加载完成");
							mListView.setPullLoadEnable(false);
							refreshLoad();
						}
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						refreshLoad();
					}
				});
	}

}
