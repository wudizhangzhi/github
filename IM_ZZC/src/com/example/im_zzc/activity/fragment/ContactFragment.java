package com.example.im_zzc.activity.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.R;
import com.example.im_zzc.activity.AddNewFriendActivity;
import com.example.im_zzc.activity.ChatActivity;
import com.example.im_zzc.activity.NearPeopleActivity;
import com.example.im_zzc.activity.NewFriendActivity;
import com.example.im_zzc.activity.SetMyInfoActivity;
import com.example.im_zzc.activity.base.FragmentBase;
import com.example.im_zzc.adapter.UserFriendAdapter;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.util.CharacterParser;
import com.example.im_zzc.util.CollectionUtils;
import com.example.im_zzc.util.PinyinComparator;
import com.example.im_zzc.view.ClearEditText;
import com.example.im_zzc.view.HeaderLayout.onRightImageButtonClickListener;
import com.example.im_zzc.view.MyLetterView;
import com.example.im_zzc.view.MyLetterView.OnTouchLetterChangeListener;
import com.example.im_zzc.view.dialog.DialogTip;
import com.example.im_zzc.view.dialog.DialogTip.onPositiveButtonClickListener;

public class ContactFragment extends FragmentBase implements
		OnItemClickListener, OnItemLongClickListener {
	private ClearEditText mEdt_search;
	private ListView mlv_friendlist;
	private MyLetterView mLetterView;
	private TextView mtv_Dialog;

	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator;
	private UserFriendAdapter userAdapter;
	private InputMethodManager inputMethodManager;
	private ArrayList<User> friends = new ArrayList<User>();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		inputMethodManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		init();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact, container, false);
	}

	private void init() {
		// TODO Auto-generated method stub
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		initListView();
		initActionBar();
		initEditText();
		initLetterView();
	}

	private void initLetterView() {
		// TODO Auto-generated method stub
		mLetterView = (MyLetterView) findViewById(R.id.contact_letterview);
		mtv_Dialog = (TextView) findViewById(R.id.contact_tv_dialog);
		mLetterView.setTextDialog(mtv_Dialog);
		mLetterView
				.setOnTouchLetterChangeListener(new OnTouchLetterChangeListener() {

					@Override
					public void onTouchLetterChangeListener(String letter) {
						//如果首次字母出现的位置存在，则跳转list的位置
						int position = userAdapter.getPositionForSection(letter
								.charAt(0));
						if (position != -1) {
							mlv_friendlist.setSelection(position);
						}
					}
				});
	}

	ImageView iv_include_tip;
	LinearLayout layout_include_new, layout_include_near;

	private void initListView() {
		mlv_friendlist = (ListView) findViewById(R.id.contact_listview);
		RelativeLayout headview = (RelativeLayout) mInflate.inflate(
				R.layout.include_new_friend, null);
		iv_include_tip = (ImageView) headview
				.findViewById(R.id.include_iv_msg_tips);
		layout_include_new = (LinearLayout) headview
				.findViewById(R.id.include_layout_new);
		layout_include_near = (LinearLayout) headview
				.findViewById(R.id.include_layout_near);

		layout_include_new.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						NewFriendActivity.class);
				intent.putExtra("from", "contact");
				startAnimActivity(intent);
			}
		});

		layout_include_near.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						NearPeopleActivity.class);
				startAnimActivity(intent);
			}
		});
		// 加入headview
		mlv_friendlist.addHeaderView(headview);
		userAdapter = new UserFriendAdapter(getActivity(), friends);
		mlv_friendlist.setAdapter(userAdapter);
		mlv_friendlist.setOnItemClickListener(this);
		mlv_friendlist.setOnItemLongClickListener(this);
		mlv_friendlist.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 隐藏软键盘
				if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN) {
					if (getActivity().getCurrentFocus() != null) {
						inputMethodManager.hideSoftInputFromWindow(
								getActivity().getCurrentFocus()
										.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					}
				}
				return false;
			}
		});
	}

	/**
	 * 初始化搜索框
	 */
	private void initEditText() {
		mEdt_search = (ClearEditText) findViewById(R.id.contact_edt_search);
		mEdt_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private void filterData(String filterStr) {
		ArrayList<User> filterData = new ArrayList<User>();
		// 空白的时候恢复
		if (TextUtils.isEmpty(filterStr)) {
			filterData = friends;
		} else {
			for (User filterUser : friends) {
				String name = filterUser.getUsername();
				if (name != null) {
					// 如果含有筛选关键字，或者开头拼音是筛选的
					if (name.indexOf(filterStr) != -1
							|| characterParser.getSpelling(name).startsWith(
									filterStr)) {
						filterData.add(filterUser);
					}
				}
			}
		}
		// 排序
		Collections.sort(filterData, pinyinComparator);
		userAdapter.updateListView(filterData);
	}

	/**
	 * 填入数据,将bmobchatuser的表转化为user表
	 * 
	 * @param list
	 */
	public void fillData(List<BmobChatUser> list) {
		friends.clear();
		for (int i = 0; i < list.size(); i++) {
			User sortModel = new User();
			BmobChatUser chatUser = list.get(i);
			sortModel.setAvatar(chatUser.getAvatar());
			sortModel.setUsername(chatUser.getUsername());
			sortModel.setNick(chatUser.getNick());
			sortModel.setObjectId(chatUser.getObjectId());
			sortModel.setContacts(chatUser.getContacts());
			// 设置拼音
			String name = sortModel.getUsername();
			if (name != null) {
				String pinyin = characterParser.getSpelling(name);
				String first = pinyin.substring(0, 1).toUpperCase();
				if (first.matches("[A-Z]")) {
					sortModel.setSortLetters(first);
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);
		}
		Collections.sort(friends, pinyinComparator);
	}

	private void querryMyFriend() {
		// 查看是否有好友邀请
		if (BmobDB.create(getActivity()).hasNewInvite()) {
			iv_include_tip.setVisibility(View.VISIBLE);
		} else {
			iv_include_tip.setVisibility(View.GONE);
		}
		// 获取并保存好友列表
		CustomApplication.getInstance().setContactList(
				CollectionUtils.list2map(BmobDB.create(getActivity())
						.getContactList()));
		Map<String, BmobChatUser> map = CustomApplication.getInstance()
				.getContactList();
		fillData(CollectionUtils.map2list(map));
		if (userAdapter == null) {
			userAdapter = new UserFriendAdapter(getActivity(), friends);
			mlv_friendlist.setAdapter(userAdapter);
		} else {
			userAdapter.updateListView(friends);
		}
	}

	private boolean isHidden;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.isHidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	/**
	 * 刷新
	 */
	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					querryMyFriend();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isHidden) {
			refresh();
		}
	}

	/**
	 * fragment显示的时候
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			refresh();
		}
	}

	/**
	 * 初始化actionbar
	 */
	private void initActionBar() {
		initTopbarForRight("联系人", R.drawable.base_action_bar_add_bg_selector,
				new onRightImageButtonClickListener() {

					@Override
					public void onClick() {
						startAnimActivity(AddNewFriendActivity.class);
					}
				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO 这里position为什么是要-1
		User user = (User) userAdapter.getItem(position-1);
		// 进入好友信息页面
//		Intent intent = new Intent(getActivity(), SetMyInfoActivity.class);
//		intent.putExtra("from", "other");
//		intent.putExtra("username", user.getUsername());
		//TODO 测试进入聊天界面
		Intent intent = new Intent(getActivity(), ChatActivity.class);
		BmobChatUser chatuser=new BmobChatUser();
		chatuser.setObjectId(user.getObjectId());
		chatuser.setUsername(user.getUsername());
		intent.putExtra("user", chatuser);
		startAnimActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		User user=(User) userAdapter.getItem(position);
		showDeleteDialog(user);
		return false;
	}

	public void showDeleteDialog(final User user){
		//TODO 测试按钮是否成功
		DialogTip dialog=new DialogTip(getActivity(), "删除联系人："+user.getUsername(), "确定", true);
		dialog.setOnPositiveButtonClickListener(new onPositiveButtonClickListener() {
			
			@Override
			public void onClick() {
				deleteContact(user);
			}
		});
		dialog.show();
	}
	
	/**
	 * 删除联系人
	 * @param user
	 */
	public void  deleteContact(User user){
		final ProgressDialog progress=new ProgressDialog(getActivity());
		progress.setMessage("正在删除");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		
		userManager.deleteContact(user.getObjectId(), new UpdateListener(){

			@Override
			public void onFailure(int arg0, String arg1) {
				showToast("删除失败");
				progress.dismiss();
			}

			@Override
			public void onSuccess() {
				// TODO 测试这种方法
				refresh();
				progress.dismiss();
			}
		});
	}
}
