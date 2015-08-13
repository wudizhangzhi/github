package com.example.im_zzc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;

import com.example.im_zzc.util.CollectionUtils;
import com.example.im_zzc.util.SharePreferenceUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class CustomApplication extends Application {
	public static CustomApplication mInstance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 是否开启debug模式
		BmobChat.DEBUG_MODE = true;
		mInstance = this;
		init();
	}
	
	public static CustomApplication getInstance(){
		return mInstance;
	}
	
	private void init() {
		//TODO获取经纬度
		//initBaidu()
		// 为什么这里用的this而不是getApplication
		mMediaPlayer = MediaPlayer.create(this, R.raw.notify);

		initImageLoader(getApplicationContext());
		// TODO 模糊
		 mNotificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		 if (BmobUserManager.getInstance(getApplicationContext())
					.getCurrentUser() != null) {
				contactList = CollectionUtils.list2map(BmobDB.create(getApplicationContext()).getContactList());
			}
	}
	
	
	MediaPlayer mMediaPlayer;
	//单线程防止被重复赋值
	public synchronized MediaPlayer getMediaPlayer(){
		if (mMediaPlayer==null) {
			mMediaPlayer=MediaPlayer.create(this, R.raw.notify);
		}
		return mMediaPlayer;
	}
	
	
	
	private void initImageLoader(Context context) {
		// TODO 待熟悉
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"bmobim/Cache");// 获取到缓存的目录地址
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// 线程池内加载的数量
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);
	}
	
	NotificationManager mNotificationManager;
	
	public NotificationManager getNotificationManager(){
		if (mNotificationManager==null) {
			mNotificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		return mNotificationManager;
	}
	
	SharePreferenceUtil mSpUtil;
	private static final String PREFERENCE_NAME="_sharedinfo";
	
	public synchronized SharePreferenceUtil getSpUtil(){
		if (mSpUtil==null) {
			String currentId=BmobUserManager.getInstance(getApplicationContext()).getCurrentUserObjectId();
			String sharedName=currentId+PREFERENCE_NAME;
			mSpUtil=new SharePreferenceUtil(getApplicationContext(), sharedName);
		}
		return mSpUtil;
	}
	
	
	private Map<String , BmobChatUser> contactList=new HashMap<String, BmobChatUser>();
	//从内存中获取好友信息
	public Map<String , BmobChatUser> getContactList(){
		return contactList;
	}
	
	public void setContactList(Map<String , BmobChatUser> contactlist ){
		if (this.contactList!=null) {
			this.contactList.clear();
		}
		this.contactList=contactlist;
	}
	
	//登出
	public void Logout(){
		BmobUserManager.getInstance(getApplicationContext()).logout();
		setContactList(null);
		//TODO 清除经纬度
	}
	
}
