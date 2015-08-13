package com.example.im_zzc.config;

import android.os.Environment;

public class BmobConstants {
	public static final String ACTION_REGISTER_SUCCESS_FINISH = "register.success.finish";// 注册成功之后登陆页面退出
	/**
	 * 相机保存路径
	 */
	public static final String BMOB_PICTURE_PATH=Environment.getExternalStorageDirectory()+"/BmobChat/Image/";
	/**
	 * 头像保存路径
	 */
	public static final String BMOB_AVATAR_PATH=Environment.getExternalStorageDirectory()+"/BmobChat/Avatar/";
	/**
	 * 拍照回调
	 */
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//拍照修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//本地相册修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//系统裁剪头像
	
	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//拍照
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//本地图片
	public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;//位置
}
