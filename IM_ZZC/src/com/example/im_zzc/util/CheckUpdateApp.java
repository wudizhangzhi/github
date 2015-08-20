package com.example.im_zzc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 检测版本是否最新并更新下载的方法
 * 
 * @author wudizhangzhi
 *
 */
public class CheckUpdateApp {
	public static final String CHECKUPDATE_URL = "";
	private static CheckUpdateApp mInstance;

	public CheckUpdateApp() {

	}

	public static synchronized CheckUpdateApp getInstance() {
		if (mInstance == null) {
			mInstance = new CheckUpdateApp();
		}
		return mInstance;
	}

	static String versionNumber;
	static int versionCode;

	/**
	 * 检查本地版本
	 */
	public static void checkLocalVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			setVersionCode(info.versionCode);
			setVersionNumber(info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断是否是最新版本
	 * @return
	 */
	public static boolean  checkNewVersion() {
		HttpURLConnection conn = null;
		InputStream is = null;
		int newVersonCode = -1;
		try {
			URL url = new URL(CHECKUPDATE_URL);
			conn = (HttpURLConnection) url.openConnection();
			is = conn.getInputStream();
			JSONObject jsobject = new JSONObject(readStream(is));
			newVersonCode = jsobject.getInt("versoncode");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				conn.disconnect();
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (newVersonCode>0&&newVersonCode>versionCode) {
			return true;
		}
		return false;
	}

	private static String readStream(InputStream is) {
		String result="";
		InputStreamReader isr=null;
		BufferedReader br=null;
		try {
			isr=new InputStreamReader(is,"gbk");
			br=new BufferedReader(isr);
			String line="";
			if ((line=br.readLine())!=null) {
				result+=line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				isr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
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
