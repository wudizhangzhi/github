package com.example.im_zzc.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

import com.example.im_zzc.R.layout;
import com.example.im_zzc.activity.ChatActivity;
import com.example.im_zzc.MyMessageReceiver;
import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.ActivityBase;
import com.example.im_zzc.adapter.EmoViewPagerAdapter;
import com.example.im_zzc.adapter.EmoteAdapter;
import com.example.im_zzc.adapter.MessageChatAdapter;
import com.example.im_zzc.adapter.base.BaseListAdapter;
import com.example.im_zzc.bean.FaceText;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.config.BmobConstants;
import com.example.im_zzc.util.CommonUtil;
import com.example.im_zzc.util.FaceTextUtil;
import com.example.im_zzc.view.EmotionsEditText;
import com.example.im_zzc.view.HeaderLayout;
import com.example.im_zzc.view.HeaderLayout.HeaderStyle;
import com.example.im_zzc.view.HeaderLayout.onLeftImageButtonClickListener;
import com.example.im_zzc.view.xlist.XListView;
import com.example.im_zzc.view.xlist.XListView.XListViewListener;

public class ChatActivity extends ActivityBase implements OnClickListener,
		EventListener, XListViewListener {

	private HeaderLayout mHeaderLayout;
	private XListView mXListView;

	// VoiceView
	private RelativeLayout layout_record;
	private ImageView iv_record;
	private TextView tv_record_tip;
	// BottomView
	private LinearLayout layout_more, layout_add, layout_emo;
	private Button bt_add, bt_emon, bt_speak, bt_voice, bt_keyboard, bt_send;
	private TextView tv_add_pic, tv_add_camera, tv_add_location;
	private EmotionsEditText edit_user_comment;
	private ViewPager pager_emo;

	private MessageChatAdapter mAdapter;
	private List<BmobMsg> mDatas;
	private BmobChatUser targetUser;
	private String TargetId = "";
	private static int MsgPagerNum;
	private BmobRecordManager recordManager;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_chat);

		MsgPagerNum = 0;
		targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
		TargetId = targetUser.getObjectId();
		Log.i("聊天界面", "TargetId:" + TargetId);
		
		initNewMessageBroadCast();
		initView();
	}

	private void initView() {
		initActionbar();
		initBottomView();
		initXListView();
		initVoiceView();
	}

	private void initActionbar() {
		mHeaderLayout = (HeaderLayout) findViewById(R.id.chat_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_LEFT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(targetUser.getUsername(),
				R.drawable.base_action_bar_back_bg_selector,
				new onLeftImageButtonClickListener() {

					@Override
					public void onClick() {
						startAnimActivity(MainActivity.class);
					}
				});
	}

	private void initBottomView() {
		// 左
		bt_add = (Button) findViewById(R.id.chat_btn_add);
		bt_emon = (Button) findViewById(R.id.chat_btn_emo);
		bt_add.setOnClickListener(this);
		bt_emon.setOnClickListener(this);
		// 右
		bt_keyboard = (Button) findViewById(R.id.chat_btn_keyboard);
		bt_send = (Button) findViewById(R.id.chat_btn_send);
		bt_voice = (Button) findViewById(R.id.chat_btn_voice);
		bt_keyboard.setOnClickListener(this);
		bt_send.setOnClickListener(this);
		bt_voice.setOnClickListener(this);
		// 下
		layout_add = (LinearLayout) findViewById(R.id.chat_layout_add);
		layout_emo = (LinearLayout) findViewById(R.id.chat_layout_emo);
		layout_more = (LinearLayout) findViewById(R.id.chat_layout_more);
		initAddView();
		initEmoView();
		// 中
		bt_speak = (Button) findViewById(R.id.chat_btn_speak);
		bt_speak.setOnClickListener(this);
		edit_user_comment = (EmotionsEditText) findViewById(R.id.chat_edit_comment);
		edit_user_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (!TextUtils.isEmpty(s)) {
					bt_send.setVisibility(View.VISIBLE);
					bt_voice.setVisibility(View.GONE);
					bt_keyboard.setVisibility(View.GONE);
				} else {
					if (bt_voice.getVisibility() != View.VISIBLE) {
						bt_send.setVisibility(View.GONE);
						bt_voice.setVisibility(View.VISIBLE);
						bt_keyboard.setVisibility(View.GONE);
					}
				}
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

	private void initVoiceView() {
		layout_record = (RelativeLayout) findViewById(R.id.chat_rl_record);
		iv_record = (ImageView) findViewById(R.id.chat_iv_record);
		tv_record_tip = (TextView) findViewById(R.id.chat_tv_redord_tip);
		// 初始化动画资源
		initVoiceAnimRes();
		// 初始化voice控件
		initRecordManager();
		bt_speak.setOnTouchListener(new VoiceTouchListen());
	}

	/**
	 * 录音按键的监听
	 */
	class VoiceTouchListen implements View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				if (!CommonUtil.checkSdCard()) {
					showToast("录音需要sd卡支持!");
					return false;
				}
				try {
					v.setPressed(true);
					layout_record.setVisibility(View.VISIBLE);
					tv_record_tip.setText("手指上滑,取消发送");
					iv_record.setImageDrawable(drawable_anim[0]);
					// 开始录音
					recordManager.startRecording(TargetId);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			case MotionEvent.ACTION_MOVE:
				if (event.getY() < 0) {// 在view上面
					tv_record_tip.setText("松开手指,取消发送");
					tv_record_tip.setTextColor(Color.RED);
				} else {
					tv_record_tip.setText("手指上滑,取消发送");
					tv_record_tip.setTextColor(Color.WHITE);
				}
				return true;
			case MotionEvent.ACTION_UP:
				bt_speak.setPressed(false);
				layout_record.setVisibility(View.INVISIBLE);
				if (event.getY() < 0) {// 手指在按钮上面
					recordManager.cancelRecording();
					Log.i("record", "cancel");
				} else {
					int recordTime = recordManager.stopRecording();
					if (recordTime > 1) {// 录音时间大于1秒
						sendVoiceMessage(
								recordManager.getRecordFilePath(TargetId),
								recordTime);
					} else {// 录音时间小于1秒
						// showToast("录音时间过短");
						recordManager.cancelRecording();
					}
				}
				return true;
			default:
				return false;
			}
		}
	}

	private Drawable[] drawable_anim;

	private void initVoiceAnimRes() {
		drawable_anim = new Drawable[] {
				getResources().getDrawable(R.drawable.chat_icon_voice1),
				getResources().getDrawable(R.drawable.chat_icon_voice2),
				getResources().getDrawable(R.drawable.chat_icon_voice3),
				getResources().getDrawable(R.drawable.chat_icon_voice4),
				getResources().getDrawable(R.drawable.chat_icon_voice5),
				getResources().getDrawable(R.drawable.chat_icon_voice6), };
	}

	private void initRecordManager() {
		recordManager = BmobRecordManager.getInstance(ChatActivity.this);
		recordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

			@Override
			public void onVolumnChanged(int level) {
				// 音量大小
				iv_record.setImageDrawable(drawable_anim[level]);
			}

			@Override
			public void onTimeChanged(int recordTime, String local) {
				if (recordTime >= BmobRecordManager.MAX_RECORD_TIME) {
					bt_speak.setPressed(false);
					bt_speak.setClickable(false);
					layout_record.setVisibility(View.INVISIBLE);
					sendVoiceMessage(local, recordTime);
					// 设置1秒后按键可按，防止误操作
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							bt_speak.setClickable(true);
						}
					}, 1000);
				} else {
					// TODO 设置显示时间
				}
			}
		});
	}

	List<FaceText> emos;

	private void initEmoView() {
		pager_emo = (ViewPager) findViewById(R.id.chat_pager_emo);
		emos = FaceTextUtil.faceTexts;
		ArrayList<View> views = new ArrayList<View>();
		for (int i = 0; i < 2; i++) {
			views.add(getGridView(i));
		}
		pager_emo.setAdapter(new EmoViewPagerAdapter(views));
	}

	private View getGridView(int num) {
		View view = View.inflate(this, R.layout.include_emo_gridview, null);
		GridView gridview = (GridView) view
				.findViewById(R.id.chat_emo_gridview);
		ArrayList<FaceText> list = new ArrayList<FaceText>();
		if (num == 0) {
			list.addAll(emos.subList(0, 21));
		} else if (num == 1) {
			list.addAll(emos.subList(21, emos.size()));
		}
		final EmoteAdapter adapter = new EmoteAdapter(this, list);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击表情,显示到edittext
				FaceText facetext = (FaceText) adapter.getItem(position);
				String key = facetext.text.toString();
				try {
					if (edit_user_comment != null && !TextUtils.isEmpty(key)) {
						int start = edit_user_comment.getSelectionStart();
						CharSequence content = edit_user_comment.getText()
								.insert(start, key);
						edit_user_comment.setText(FaceTextUtil
								.toSpannableString(ChatActivity.this,
										(String) content));
						// 定位光标
						CharSequence info = edit_user_comment.getText();
						if (info instanceof Spannable) {
							Spannable spann = (SpannableString) info;
							Selection.setSelection(spann, start + key.length());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return view;
	}

	private void initAddView() {
		tv_add_pic = (TextView) findViewById(R.id.chat_add_tv_pic);
		tv_add_camera = (TextView) findViewById(R.id.chat_add_tv_camera);
		tv_add_location = (TextView) findViewById(R.id.chat_add_tv_location);
		tv_add_pic.setOnClickListener(this);
		tv_add_camera.setOnClickListener(this);
		tv_add_location.setOnClickListener(this);
	}

	private void initXListView() {
		mXListView = (XListView) findViewById(R.id.chat_xlistview_message);
		mXListView.setPullLoadEnable(false);
		mXListView.setPullRefreshEnable(false);
		initOrRefresh();
		mXListView.setSelection(mAdapter.getCount() - 1);

		mXListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideSoftInputView();
				layout_more.setVisibility(View.GONE);
				layout_emo.setVisibility(View.GONE);
				layout_add.setVisibility(View.GONE);
				return false;
			}
		});
		mAdapter.setOnInternalClickListener(R.id.item_chat_iv_fail_resend,
				new MessageChatAdapter.onInternalClickListener() {

					@Override
					public void OnClickListener(View parentV, View v,
							Integer position, Object values) {
						resendMessage(values, parentV);
					}
				});
	}

	/**
	 * 重新发送消息
	 * 
	 * @param values
	 * @param parentV
	 */
	private void resendMessage(Object values, View parentV) {
		BmobMsg msg = (BmobMsg) values;
		if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {// 语音、图片
			resendFileMessage(parentV, values);
		} else {// 文字
			resendTextMessage(parentV, values);
		}
	}

	private void resendFileMessage(final View parentV, final Object values) {
		manager.resendFileMessage(targetUser, (BmobMsg) values,
				new UploadListener() {

					@Override
					public void onFailure(int arg0, String arg1) {
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_FAIL);
						parentV.findViewById(R.id.item_chat_progress_load)
								.setVisibility(View.GONE);
						parentV.findViewById(R.id.item_chat_iv_fail_resend)
								.setVisibility(View.VISIBLE);
						parentV.findViewById(R.id.item_chat_tv_voice_length)
								.setVisibility(View.GONE);
						parentV.findViewById(R.id.item_chat_tv_send_status)
								.setVisibility(View.GONE);
					}

					@Override
					public void onStart(BmobMsg arg0) {
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_START);
						parentV.findViewById(R.id.item_chat_progress_load)
								.setVisibility(View.VISIBLE);
						parentV.findViewById(R.id.item_chat_iv_fail_resend)
								.setVisibility(View.GONE);
						parentV.findViewById(R.id.item_chat_tv_voice_length)
								.setVisibility(View.GONE);
						parentV.findViewById(R.id.item_chat_tv_send_status)
								.setVisibility(View.GONE);
					}

					@Override
					public void onSuccess() {
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_SUCCESS);
						parentV.findViewById(R.id.item_chat_progress_load)
								.setVisibility(View.GONE);
						parentV.findViewById(R.id.item_chat_iv_fail_resend)
								.setVisibility(View.GONE);
						if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_VOICE) {// 声音
							parentV.findViewById(R.id.item_chat_tv_voice_length)
									.setVisibility(View.VISIBLE);
							// ((TextView)parentV.findViewById(R.id.item_chat_tv_voice_length)).setText();
						} else if (((BmobMsg) values).getMsgType() == BmobConfig.TYPE_IMAGE) {// 图片
							parentV.findViewById(R.id.item_chat_tv_send_status)
									.setVisibility(View.VISIBLE);
							((TextView) parentV
									.findViewById(R.id.item_chat_tv_send_status))
									.setText("已发送");
						}

					}

				});
	}

	private void resendTextMessage(final View parentV, final Object values) {
		manager.resendTextMessage(targetUser, (BmobMsg) values,
				new PushListener() {

					@Override
					public void onFailure(int arg0, String arg1) {
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_FAIL);
						parentV.findViewById(R.id.item_chat_progress_load)
								.setVisibility(View.GONE);
						parentV.findViewById(R.id.item_chat_iv_fail_resend)
								.setVisibility(View.VISIBLE);
						parentV.findViewById(R.id.item_chat_tv_send_status)
								.setVisibility(View.GONE);
					}

					@Override
					public void onSuccess() {
						((BmobMsg) values)
								.setStatus(BmobConfig.STATUS_SEND_SUCCESS);
						parentV.findViewById(R.id.item_chat_iv_fail_resend)
								.setVisibility(View.GONE);
						parentV.findViewById(R.id.item_chat_progress_load)
								.setVisibility(View.GONE);
						parentV.findViewById(R.id.item_chat_tv_send_status)
								.setVisibility(View.VISIBLE);
						((TextView) parentV
								.findViewById(R.id.item_chat_tv_send_status))
								.setText("已发送");
					}
				});
	}

	/**
	 * 根据页数获取聊天数量
	 * 
	 * @return
	 */
	private List<BmobMsg> initMsgData() {
		return BmobDB.create(ChatActivity.this).queryMessages(TargetId,
				MsgPagerNum);
	}

	private void initOrRefresh() {
		if (mAdapter != null) {
			if (MyMessageReceiver.mNewNum != 0) {// 用于更新当在聊天界面锁屏期间来了消息，这时再回到聊天页面的时候需要显示新来的消息
				int news = MyMessageReceiver.mNewNum;// 倒叙显示新消息
				int size = initMsgData().size();
				for (int i = size - news - 1; i < size; i++) {
					mAdapter.add(initMsgData().get(i));
				}
				mXListView.setSelection(mAdapter.getCount() - 1);
			} else {
				mAdapter.notifyDataSetChanged();
			}
		} else {
			mAdapter = new MessageChatAdapter(ChatActivity.this, initMsgData(),
					targetUser,currentUser);
			mXListView.setAdapter(mAdapter);
			
		}
	}

	private String localCameraPath = "";// 本地相机保存的路径

	/**
	 * 从相机选取图片
	 */
	private void selectImageFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(BmobConstants.BMOB_PICTURE_PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir, String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		localCameraPath = file.getAbsolutePath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent,
				BmobConstants.REQUESTCODE_TAKE_CAMERA);

	}

	/**
	 * 从本地相册选取图片
	 */
	private void selectImageFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, BmobConstants.REQUESTCODE_TAKE_LOCAL);
	}

	/**
	 * 根据请求处理ContentProvider数据
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case BmobConstants.REQUESTCODE_TAKE_LOCAL:// 本地图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(
								selectedImage, null, null, null, null);
						cursor.moveToFirst();
						int index = cursor.getColumnIndex("_data");
						String localSelectedPath = cursor.getString(index);
						if (localSelectedPath == null
								|| localSelectedPath.equals(null)) {
							showToast("找不到你想要的图片");
							return;
						}
						sendImageMessage(localSelectedPath);
					}
				}
				break;
			case BmobConstants.REQUESTCODE_TAKE_CAMERA:// 当取到值的时候才上传path路径下的图片到服务器
				sendImageMessage(localCameraPath);
				break;
			default:
				break;
			}

		}
	}

	public class NewMessageBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String from = intent.getStringExtra("fromId");
			String msgId = intent.getStringExtra("msgId");
			String msgTime = intent.getStringExtra("msgTime");
			BmobMsg msg = BmobChatManager.getInstance(ChatActivity.this)
					.getMessage(msgId, msgTime);
			if (from != TargetId) {// 如果不是当前对象的消息，退出
				return;
			}
			mAdapter.add(msg);
			mXListView.setSelection(mAdapter.getCount() - 1);
			// 取消当前对象未读消息的标识
			BmobDB.create(ChatActivity.this).resetUnread(TargetId);
			// 截断广播
			abortBroadcast();
		}
	}

	public static final int NEW_MESSAGE = 0x001;// 接收到新消息的标识
	NewMessageBroadcastReceiver mReceiver;

	public void initNewMessageBroadCast() {
		// 注册接收消息广播
		mReceiver = new NewMessageBroadcastReceiver();
		IntentFilter filter = new IntentFilter(BmobConfig.BROADCAST_NEW_MESSAGE);
		// 设置广播的优先级别大于Mainacitivity,这样如果消息来的时候正好在chat页面，直接显示消息，而不是提示消息未读
		filter.setPriority(5);
		registerReceiver(mReceiver, filter);
	}

	/**
	 * 刷新界面
	 * 
	 * @param msg
	 */
	private void refreshMessage(BmobMsg msg) {
		mAdapter.add(msg);
		mXListView.setSelection(mAdapter.getCount() - 1);
		edit_user_comment.setText("");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chat_edit_comment:
			if (layout_more.getVisibility() == View.VISIBLE) {
				layout_more.setVisibility(View.GONE);
				layout_emo.setVisibility(View.GONE);
				layout_add.setVisibility(View.GONE);
			}
			break;
		case R.id.chat_btn_voice:// 对讲
			layout_more.setVisibility(View.GONE);
			edit_user_comment.setVisibility(View.GONE);
			bt_speak.setVisibility(View.VISIBLE);
			bt_voice.setVisibility(View.GONE);
			bt_keyboard.setVisibility(View.VISIBLE);
			hideSoftInputView();
			break;
		case R.id.chat_btn_keyboard:// 键盘
			layout_more.setVisibility(View.GONE);
			edit_user_comment.setVisibility(View.VISIBLE);
			edit_user_comment.requestFocus();
			bt_speak.setVisibility(View.GONE);
			bt_voice.setVisibility(View.VISIBLE);
			bt_keyboard.setVisibility(View.GONE);
			showSolfInputView();
			break;
		case R.id.chat_btn_emo:// 表情
			// 显示edit_user_comment；隐藏bt_speak
			edit_user_comment.setVisibility(View.VISIBLE);
			bt_speak.setVisibility(View.GONE);
			// layout_emo显示则隐藏并隐藏layout_add，隐藏则显示并隐藏软键盘
			if (layout_emo.getVisibility() == View.VISIBLE) {
				layout_more.setVisibility(View.GONE);
				layout_emo.setVisibility(View.GONE);
				layout_add.setVisibility(View.GONE);
			} else {
				layout_more.setVisibility(View.VISIBLE);
				layout_emo.setVisibility(View.VISIBLE);
				layout_add.setVisibility(View.GONE);
				hideSoftInputView();
			}
			// onEmoOrAddClick(true);
			break;
		case R.id.chat_btn_add:
			// layout_add显示则隐藏，隐藏则显示并隐藏软键盘
			if (layout_add.getVisibility() == View.VISIBLE) {
				layout_more.setVisibility(View.GONE);
				layout_emo.setVisibility(View.GONE);
				layout_add.setVisibility(View.GONE);
			} else {
				layout_more.setVisibility(View.VISIBLE);
				layout_emo.setVisibility(View.GONE);
				layout_add.setVisibility(View.VISIBLE);
				hideSoftInputView();
			}
			// onEmoOrAddClick(false);
			break;
		case R.id.chat_btn_send:// 点击发送
			String text = edit_user_comment.getText().toString();
			if (TextUtils.isEmpty(text)) {
				showToast("不能发送空消息");
				return;
			}
			boolean isNetConnected = CommonUtil.isNetworkAvailable(this);
			if (!isNetConnected) {
				showToast("请检查网络连接");
			}
			BmobMsg msg = BmobMsg.createTextSendMsg(this, TargetId, text);
			// 发送消息
			manager.sendTextMessage(targetUser, msg);
			// 更新本地界面
			refreshMessage(msg);
			break;
		case R.id.chat_add_tv_camera:// 拍照
			selectImageFromCamera();
			break;
		case R.id.chat_add_tv_pic:// 图片
			selectImageFromLocal();
			break;
		case R.id.chat_add_tv_location:// 位置

			break;
		default:
			break;
		}
	}

	/**
	 * 发送语音消息
	 * 
	 * @param local
	 *            ：本地地址
	 * @param length
	 *            ：长度
	 */
	private void sendVoiceMessage(String local, int length) {
		manager.sendVoiceMessage(targetUser, local, length,
				new UploadListener() {

					@Override
					public void onSuccess() {
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onStart(BmobMsg msg) {
						refreshMessage(msg);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						mAdapter.notifyDataSetChanged();
					}
				});
	}

	/**
	 * 发送图片信息
	 * 
	 * @param local
	 */
	private void sendImageMessage(String local) {
		if (layout_more.getVisibility() == View.VISIBLE) {
			layout_more.setVisibility(View.GONE);
			layout_add.setVisibility(View.GONE);
			layout_emo.setVisibility(View.GONE);
		}
		manager.sendImageMessage(targetUser, local, new UploadListener() {

			@Override
			public void onSuccess() {
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onStart(BmobMsg msg) {
				refreshMessage(msg);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onAddUser(BmobInvitation arg0) {

	}

	@Override
	public void onMessage(BmobMsg msg) {
		Message handlerMessage = mHandler.obtainMessage(NEW_MESSAGE);
		handlerMessage.obj = msg;
		mHandler.sendMessage(handlerMessage);
	}

	@Override
	public void onNetChange(boolean isNetChange) {
		if (isNetChange) {
			showToast("当前网络不可用,请检查您的网络!");
		}
	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
		// TODO Auto-generated method stub
		if (conversionId.split("&")[1].equals(TargetId)) {
			for (BmobMsg msg : mAdapter.getList()) {
				if (msg.getConversationId().equals(conversionId)
						&& msg.getMsgTime().equals(msgTime)) {
					msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
				}
			}
			mAdapter.notifyDataSetChanged();
		}

	}

	/**
	 * 事件监听处理发送过来的message，更新ui
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == NEW_MESSAGE) {
				// TODO
				BmobMsg bmobmsg = (BmobMsg) msg.obj;
				String uId = bmobmsg.getBelongId();
				BmobMsg message = BmobChatManager
						.getInstance(ChatActivity.this).getMessage(
								bmobmsg.getConversationId(),
								bmobmsg.getMsgTime());
				if (!uId.equals(TargetId)) {
					return;
				}
				mAdapter.add(message);
				mXListView.setSelection(mAdapter.getCount() - 1);
				BmobDB.create(ChatActivity.this).resetUnread(TargetId);
			}
		}

	};

	/**
	 * 下拉刷新
	 */
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// 页数+1；如果总数量<=当前数量，则已经全部加载
				MsgPagerNum++;
				int total = BmobDB.create(ChatActivity.this)
						.queryChatTotalCount(TargetId);
				int current = mAdapter.getCount();
				if (current <= total) {
					showToast("已经全部加载");
				} else {
					mAdapter.setList(initMsgData());
					mXListView.setSelection(mAdapter.getCount() - 1);
				}
				mXListView.stopRefreshing();
			}
		}, 1000);
	}

	@Override
	public void onLoadMore() {

	}

	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);
		// TODO
		if (recordManager.isRecording()) {
			recordManager.cancelRecording();
			layout_record.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initOrRefresh();// 重新刷新界面
		MyMessageReceiver.ehList.add(this);// 加入监听
		BmobNotifyManager.getInstance(this).cancelNotify();
		BmobDB.create(this).resetUnread(TargetId);
		// 清空新消息数量
		MyMessageReceiver.mNewNum = 0;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(mReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /**
	// * 根据表情界面界面是否展开，决定输入框的显示模式
	// * @param isEmo
	// */
	// private void showEditState(boolean isEmo){
	// edit_user_comment.setVisibility(View.VISIBLE);
	// edit_user_comment.requestFocus();
	// bt_voice.setVisibility(View.VISIBLE);
	// bt_keyboard.setVisibility(View.GONE);
	// bt_speak.setVisibility(View.GONE);
	// if (isEmo) {//表情界面打开
	// layout_more.setVisibility(View.VISIBLE);
	// layout_emo.setVisibility(View.VISIBLE);
	// layout_add.setVisibility(View.GONE);
	// hideSoftInputView();
	// }else {
	// layout_more.setVisibility(View.GONE);
	// showSolfInputView();
	// }
	// }

	/**
	 * 显示软键盘
	 */
	public void showSolfInputView() {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null) {
				imm.showSoftInput(edit_user_comment, 0);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (layout_more.getVisibility() == View.VISIBLE) {
				layout_more.setVisibility(View.GONE);
				return false;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	// /**
	// * 显示emo或者add页面
	// * @param showEmo：true显示emo，false显示add
	// */
	// public void onEmoOrAddClick(boolean showEmo){
	// if (layout_more.getVisibility()==View.VISIBLE) {
	// boolean isEmoShowing=(layout_emo.getVisibility()==View.VISIBLE);
	// boolean isAddShowing=(layout_add.getVisibility()==View.VISIBLE);
	// if (showEmo&&isAddShowing) {//要显示emo且emo已经显示
	// layout_emo.setVisibility(View.VISIBLE);
	// layout_add.setVisibility(View.GONE);
	// }else if (!showEmo&&isEmoShowing) {
	// layout_add.setVisibility(View.VISIBLE);
	// layout_emo.setVisibility(View.GONE);
	// }else {
	// layout_more.setVisibility(View.GONE);
	// }
	// }else {//more未显示
	// layout_more.setVisibility(View.VISIBLE);
	// if (showEmo) {//显示表情页
	// layout_emo.setVisibility(View.VISIBLE);
	// layout_add.setVisibility(View.GONE);
	// }else {//显示add页面
	// layout_emo.setVisibility(View.GONE);
	// layout_add.setVisibility(View.VISIBLE);
	// }
	// }
	// }
}
