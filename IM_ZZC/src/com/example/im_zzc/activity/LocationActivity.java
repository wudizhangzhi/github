package com.example.im_zzc.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.BaseActivity;
import com.example.im_zzc.view.HeaderLayout.onRightImageButtonClickListener;

public class LocationActivity extends BaseActivity implements
		OnGetGeoCoderResultListener {
	
	MapView mMapView;
	BaiduMap mBaiduMap;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	BitmapDescriptor mCurrentMarker;

	GeoCoder mSearch = null; // 搜索模块，因为百度定位sdk能够得到经纬度，但是却无法得到具体的详细地址，因此需要采取反编码方式去搜索此经纬度代表的地址
	private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key
	static BDLocation lastLocation = null;

	private BitmapDescriptor bdgeo = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_geo); // 当前位置的显示图标

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_location);
		initBaiduMap();
	}

	private void initBaiduMap() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMaxAndMinZoomLevel(18, 13);

		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);

		Intent intent = getIntent();
		String type = intent.getStringExtra("type");
		if (type.equals("select")) {// 聊天选择位置
			initTopBarForBoth("我的位置",
					R.drawable.base_action_bar_true_bg_selector,
					new onRightImageButtonClickListener() {

						@Override
						public void onClick() {
							goToChatPage();
							Log.i("地图", "点击确定");
						}
					});
			// 获取位置后再设置为可点击
			mHeadLayout.getRightImageButton().setEnabled(false);
			initLocClient();
		} else {// 查看当前位置
			initTopBarForLeft("位置");
			Bundle b = intent.getExtras();
			LatLng latlng = new LatLng(b.getDouble("latitude"),
					b.getDouble("longitude"));
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
			// 显示当前位置图标
			OverlayOptions ooA = new MarkerOptions().position(latlng)
					.icon(bdgeo).zIndex(9);
			mBaiduMap.addOverlay(ooA);
		}
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
	}

	private void initLocClient() {
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
						com.baidu.mapapi.map.MyLocationConfigeration.LocationMode.NORMAL,
						true, null));
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setProdName("bmobim");// 设置产品线
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setOpenGps(true);
		option.setIsNeedAddress(true);
		option.setIgnoreKillProcess(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
		if (mLocClient != null && mLocClient.isStarted())
			mLocClient.requestLocation();

		if (lastLocation != null) {
			// 显示在地图上
			LatLng ll = new LatLng(lastLocation.getLatitude(),
					lastLocation.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
		}
	}

	private void goToChatPage() {
		if (lastLocation != null) {
			Intent intent = new Intent();
			intent.putExtra("y", lastLocation.getLongitude());// ����
			intent.putExtra("x", lastLocation.getLatitude());// ά��
			intent.putExtra("address", lastLocation.getAddrStr());
			setResult(RESULT_OK, intent);
			this.finish();
		} else {
			showToast("位置为空!");
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;

			if (lastLocation != null) {
				if (lastLocation.getLatitude() == location.getLatitude()
						&& lastLocation.getLongitude() == location
								.getLongitude()) {
					BmobLog.i("获取坐标相同");// 若两次请求获取到的地理位置坐标是相同的，则不再定位
					mLocClient.stop();
					return;
				}
			}
			lastLocation = location;
			//TODO 测试上传位置！
			CustomApplication.getInstance().mLastLocation=new BmobGeoPoint(location.getLongitude(),location.getLatitude());
			updateUserLocation();
			
			BmobLog.i("lontitude = " + location.getLongitude() + ",latitude = "
					+ location.getLatitude() + ",地址 = "
					+ lastLocation.getAddrStr());
			
//			MyLocationData locData = new MyLocationData.Builder()
//					.accuracy(location.getRadius())
//					// 此处设置开发者获取到的方向信息，顺时针0-360
//					.direction(100).latitude(location.getLatitude())
//					.longitude(location.getLongitude()).build();
//			mBaiduMap.setMyLocationData(locData);
//			LatLng ll = new LatLng(location.getLatitude(),
//					location.getLongitude());
//			String address = location.getAddrStr();
//			if (address != null && !address.equals("")) {
//				lastLocation.setAddrStr(address);
//			} else {
//				// 反Geo搜索
//				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
//			}
			// TODO 显示在地图上
//			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
//			mBaiduMap.animateMapStatus(u);
			// 设置按钮可点击
			mHeadLayout.getRightImageButton().setEnabled(true);
		}

	}

	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				showToast("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				showToast("当前网络连接不稳定，请检查您的网络设置!");
			}
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			showToast("抱歉，未能找到结果");
			return;
		}
		BmobLog.i("反编码得到的地址：" + result.getAddress());
		lastLocation.setAddrStr(result.getAddress());
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		lastLocation = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null && mLocClient.isStarted()) {
			// 退出时销毁定位
			mLocClient.stop();
		}
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
		super.onDestroy();
		// 回收 bitmap 资源
		bdgeo.recycle();
	}
}
