package com.example.im_zzc.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 检测版本是否最新并更新下载的方法
 * @author wudizhangzhi
 *
 */
public class CheckUpdateApp {
	
	private static CheckUpdateApp mInstance;
	public CheckUpdateApp(){
		
	}
	public static synchronized CheckUpdateApp getInstance(){
		if (mInstance==null) {
			mInstance=new CheckUpdateApp();
		}
		return mInstance;
	}
	
	static String versionNumber;
	static int versionCode;
	/**
	 * 检查本地版本
	 */
	public static void checkLocalVersion(Context context){
		try {
			PackageInfo info=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			setVersionCode(info.versionCode);
			setVersionNumber(info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void checkNewVersion(){
		
	}
	
	
	public static String getVersionNumber() {
		return versionNumber;
	}
	public static void setVersionNumber(String versionNumber) {
		CheckUpdateApp.versionNumber = versionNumber;
	}
	public static int getVersionCode() {
		return versionCode;
	}
	public static void setVersionCode(int versionCode) {
		CheckUpdateApp.versionCode = versionCode;
	};
	
}
