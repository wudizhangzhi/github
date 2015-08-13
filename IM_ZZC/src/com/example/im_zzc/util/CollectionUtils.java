package com.example.im_zzc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.bmob.im.bean.BmobChatUser;

public class CollectionUtils {
	
	public static Map<String , BmobChatUser> list2map(List<BmobChatUser> list){
		Map<String , BmobChatUser> friends=new HashMap<String, BmobChatUser>();
		for (BmobChatUser user : list) {
			friends.put(user.getUsername(), user);
		}
		return friends;
	}
	
	public static List<BmobChatUser> map2list(Map<String , BmobChatUser> map){
		ArrayList<BmobChatUser> list=new ArrayList<BmobChatUser>();
		Iterator<Entry<String, BmobChatUser>> iterator=map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, BmobChatUser> entry=iterator.next();
			list.add(entry.getValue());
		}
		return list;
	}
	
	public static boolean isNotNull(Collection<?> collection){
		if (collection!=null&&collection.size()>0) {
			return true;
		}
		return false;
	}
}
