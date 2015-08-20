package com.example.im_zzc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检测注册信息是否符合规范
 * @author wudizhangzhi
 *
 */
public class CheckRegisterInfo {
	public static final int USERNAME_TOOLONG=0;
	public static final int USERNAME_TOOSHORT=1;
	public static final int USERNAME_CONTAINSPACE=2;
	public static final int USERNAME_OK=3;
	/**
	 * 用户名逻辑：
	 * 汉字算2个字符，一共不能超过12个字符,不少于4个字符
	 * 不能含有空格
	 * @param name
	 * @return
	 */
	public static int checkUserName(String name){
		//判断是否含有空格
		Pattern p1=Pattern.compile("[\\s]+");
		Matcher m1=p1.matcher(name);
		while (m1.find()) {
			return USERNAME_CONTAINSPACE;
		}
		//判断字符数量
		int length=0;
		Pattern p2=Pattern.compile("[\u4e00-\u9fa5]+");
		Matcher m2=p2.matcher(name);
		while (m2.find()) {
			length=+m2.group().length();
		}
		length=name.length()+length;
		if (length>12) {
			return USERNAME_TOOLONG;
		}
		if (length<4) {
			return	USERNAME_TOOSHORT;
		}
		return USERNAME_OK;
	}
//	
//	/**
//	 * 密码6-16位
//	 * @param password
//	 */
//	public static void checkPassWord(String password){
//		
//	}
}
