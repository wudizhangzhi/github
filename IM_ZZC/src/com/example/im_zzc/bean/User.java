package com.example.im_zzc.bean;

import java.util.List;

import android.content.Context;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
/**
 * 在这里添加需要的用户资料
 * @author wudizhangzhi
 *
 */
public class User extends BmobChatUser {
	
	/**
	 * 性别 true-男
	 */
	private boolean sex;
	
	/**
	 * 显示数据拼音的首字母
	 */
	private String sorrtLetters;
	/**
	 * 地理位置
	 */
	private BmobGeoPoint location;
	
	
	public boolean getSex() {
		return sex;
	}
	public void setSex(boolean sex) {
		this.sex = sex;
	}
	public String getSortLetters() {
		return sorrtLetters;
	}
	public void setSortLetters(String sorrtLetters) {
		this.sorrtLetters = sorrtLetters;
	}
	public BmobGeoPoint getLocation() {
		return location;
	}
	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}
	
}
