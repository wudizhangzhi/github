package com.example.im_zzc.util;

import java.util.Comparator;

import com.example.im_zzc.bean.User;

public class PinyinComparator implements Comparator<User> {

	@Override
	public int compare(User user1, User user2) {
		// TODO Auto-generated method stub
		if (user1.getSortLetters().equals("@")||user2.getSortLetters().equals("#")) {
			return -1;
		}else if (user1.getSortLetters().equals("#")||user2.getSortLetters().equals("@")) {
			return -1;
		}else {
			return user1.getSortLetters().compareTo(user2.getSortLetters());
		}
	}

}
