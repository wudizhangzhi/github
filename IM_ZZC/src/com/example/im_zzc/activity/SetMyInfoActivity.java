package com.example.im_zzc.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.session.PlaybackState.CustomAction;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.example.im_zzc.CustomApplication;
import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.ActivityBase;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.config.BmobConstants;
import com.example.im_zzc.util.CollectionUtils;
import com.example.im_zzc.util.ImageLoadOptions;
import com.example.im_zzc.util.PhotoUtil;
import com.example.im_zzc.view.HeaderLayout;
import com.example.im_zzc.view.HeaderLayout.onLeftImageButtonClickListener;
import com.example.im_zzc.view.dialog.DialogTip;
import com.example.im_zzc.view.dialog.DialogTip.onPositiveButtonClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class SetMyInfoActivity extends ActivityBase implements OnClickListener {
	private TextView tv_set_name, tv_set_nick, tv_set_gender;
	private ImageView iv_set_avatar, iv_arraw, iv_nickarraw;
	private LinearLayout layout_all;

	private Button btn_chat, btn_black, btn_add_friend;
	private RelativeLayout layout_head, layout_nick, layout_gender,
			layout_black_tips;
	//用户信息
	private String from;
	private String username;
	private User user;

	@Override
	protected void onCreate(Bundle arg0) {
		// 隐藏
		int currentapiVersion = Build.VERSION.SDK_INT;
		if (currentapiVersion >= 14) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		}
		super.onCreate(arg0);
		setContentView(R.layout.activity_set_info);
		Intent intent = getIntent();
		from = intent.getStringExtra("from");
		username = intent.getStringExtra("username");
		initView();
	}

	@Override
	protected void onResume() {
		Log.i("onResume", "onResume");
		super.onResume();
		if (from.equals("me")) {
			initMeData();
		}
	}

	private void initView() {
		layout_all = (LinearLayout) findViewById(R.id.setinfo_layout_all);
		iv_set_avatar = (ImageView) findViewById(R.id.setinfo_iv_set_avator);
		iv_arraw = (ImageView) findViewById(R.id.setinfo_iv_arraw);
		iv_nickarraw = (ImageView) findViewById(R.id.setinfo_iv_nickarraw);
		tv_set_name = (TextView) findViewById(R.id.setinfo_tv_set_name);
		tv_set_nick = (TextView) findViewById(R.id.setinfo_tv_set_nick);
		layout_head = (RelativeLayout) findViewById(R.id.setinfo_layout_head);
		layout_nick = (RelativeLayout) findViewById(R.id.setinfo_layout_nick);
		layout_gender = (RelativeLayout) findViewById(R.id.setinfo_layout_gender);

		layout_black_tips = (RelativeLayout) findViewById(R.id.setinfo_layout_black_tips);
		tv_set_gender = (TextView) findViewById(R.id.setinfo_tv_set_gender);
		btn_chat = (Button) findViewById(R.id.setinfo_btn_chat);
		btn_black = (Button) findViewById(R.id.setinfo_btn_black);
		btn_add_friend = (Button) findViewById(R.id.setinfo_btn_add_friend);
		btn_add_friend.setEnabled(false);
		btn_chat.setEnabled(false);
		btn_black.setEnabled(false);
		if (from.equals("me")) {// 个人信息页面
			initTopBarForLeft("个人资料");
			layout_head.setOnClickListener(this);
			layout_nick.setOnClickListener(this);
			layout_gender.setOnClickListener(this);
			iv_nickarraw.setVisibility(View.VISIBLE);
			iv_arraw.setVisibility(View.VISIBLE);
			btn_black.setVisibility(View.GONE);
			btn_chat.setVisibility(View.GONE);
			btn_add_friend.setVisibility(View.GONE);
		} else {
			initTopBarForLeft("详细资料");
			iv_nickarraw.setVisibility(View.INVISIBLE);
			iv_arraw.setVisibility(View.INVISIBLE);
			// 即使不是好友也能发送消息
			btn_chat.setVisibility(View.VISIBLE);
			btn_chat.setOnClickListener(this);
			if (from.equals("add")) {// 从添加好友请求进入
				// 从附近的人列表添加好友--因为获取附近的人的方法里面有是否显示好友的情况，因此在这里需要判断下这个用户是否是自己的好友
				if (mApplication.getContactList().containsKey("username")) {// 是自己的好友
					btn_black.setVisibility(View.VISIBLE);
					btn_black.setOnClickListener(this);
				} else {// 陌生人
					btn_add_friend.setVisibility(View.VISIBLE);
					btn_add_friend.setOnClickListener(this);
					btn_black.setVisibility(View.GONE);
					// btn_black.setOnClickListener(this);
				}
			} else {// 查看其他人
				btn_black.setVisibility(View.VISIBLE);
				btn_black.setOnClickListener(this);
				// btn_add_friend.setVisibility(View.GONE);
				// btn_add_friend.setOnClickListener(this);
			}
			initOtherData(username);
		}
	}

	private void initMeData() {
		Log.i("initMeData", "更新自己的信息");
		User user = userManager.getCurrentUser(User.class);
		initOtherData(user.getUsername());
	}

	private void initOtherData(String name) {
		Log.i("initOtherData", "initOtherData");
		userManager.queryUser(name, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {

			}

			@Override
			public void onSuccess(List<User> list) {
				if (list.size() > 0 && list != null) {
					user = list.get(0);
					Log.i("initOtherData", "userAvarar:" + user.getAvatar());
					btn_add_friend.setEnabled(true);
					btn_chat.setEnabled(true);
					btn_black.setEnabled(true);
					updateUser(user);
				}
			}
		});
	}

	private void updateUser(User user) {
		Log.i("updateUser",
				"用户名：" + user.getUsername() + "；头像" + user.getAvatar());
		refreshAvatar(user.getAvatar());
		tv_set_name.setText(user.getUsername());
		tv_set_nick.setText(user.getNick());
		tv_set_gender.setText(user.getSex() == true ? "男" : "女");
		if (from.equals("other")) {
			if (BmobDB.create(this).isBlackUser(username)) {// 在黑名单
				// TODO 取消黑名单的设置
				btn_black.setVisibility(View.GONE);
				layout_black_tips.setVisibility(View.VISIBLE);
			} else {
				btn_black.setVisibility(View.VISIBLE);
				layout_black_tips.setVisibility(View.GONE);
			}
		}
	}

	private void refreshAvatar(String avatar) {
		Log.i("刷新头像", user.getUsername() + "头像:" + user.getAvatar());
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_set_avatar,
					ImageLoadOptions.getOptions());
		} else {
			iv_set_avatar.setImageResource(R.drawable.head);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setinfo_layout_head:
			showAvatarPop();
			break;
		case R.id.setinfo_layout_nick:
			startAnimActivity(UpdateInfoActivity.class);
			break;
		case R.id.setinfo_layout_gender:
			showSexChooseDialog();
			break;
		case R.id.setinfo_btn_chat:
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("user", user);
			startAnimActivity(intent);
			finish();
			break;
		case R.id.setinfo_btn_add_friend:
			addFriend();
			break;
		case R.id.setinfo_btn_black:
			showBlackDialog(username);
			break;

		default:
			break;
		}
	}

	private void showBlackDialog(final String username) {
		DialogTip dialog = new DialogTip(this, "删除好友", "你确定要将好友加入黑名单吗？", "确定",
				"取消", true);
		dialog.setOnPositiveButtonClickListener(new onPositiveButtonClickListener() {
			@Override
			public void onClick() {
				userManager.addBlack(username, new UpdateListener() {

					@Override
					public void onSuccess() {
						showToast("加入黑名单成功!");
						btn_black.setVisibility(View.GONE);
						layout_black_tips.setVisibility(View.VISIBLE);
						CustomApplication.getInstance().setContactList(
								CollectionUtils.list2map(BmobDB.create(
										SetMyInfoActivity.this)
										.getContactList()));
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						showToast("加入黑名单失败:" + arg1);
					}
				});
			}
		});
		dialog.show();
		dialog = null;
	}

	private void addFriend() {
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setCanceledOnTouchOutside(false);
		progress.setMessage("发送好友请求中...");
		progress.show();
		BmobChatManager.getInstance(this).sendTagMessage(MsgTag.ADD_CONTACT,
				user.getObjectId(), new PushListener() {

					@Override
					public void onSuccess() {
						progress.dismiss();
						showToast("好友请求发送成功");

					}

					@Override
					public void onFailure(int arg0, String arg1) {
						progress.dismiss();
						showToast("好友请求发送失败：" + arg1);
					}
				});
	}

	String[] sexs = new String[] { "男", "女" };

	private void showSexChooseDialog() {
		new AlertDialog.Builder(this)
				.setTitle("性别")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setSingleChoiceItems(sexs, 0,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								updateInfo(which);
								dialog.dismiss();
							}
						}).setNegativeButton("取消", null).show();
	}

	/**
	 * 更新性别
	 * 
	 * @param which
	 */
	private void updateInfo(int which) {
		final User user = userManager.getCurrentUser(User.class);
		if (which == 0) {
			user.setSex(true);
		} else {
			user.setSex(false);
		}
		user.update(this, new UpdateListener() {

			@Override
			public void onSuccess() {
				showToast("设置成功");
				tv_set_gender.setText(user.getSex() == true ? "男" : "女");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showToast("onFailure:" + arg1);
			}
		});
	}

	RelativeLayout layout_choose;
	RelativeLayout layout_photo;
	PopupWindow avatarPop;
	String filePath = "";// 图片路径

	/**
	 * 显示头像页面
	 */
	private void showAvatarPop() {
		View view = LayoutInflater.from(this).inflate(R.layout.pop_showavatar,
				null);
		layout_choose = (RelativeLayout) view
				.findViewById(R.id.showavatar_layout_choose);
		layout_photo = (RelativeLayout) view
				.findViewById(R.id.showavatar_layout_photo);
		layout_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				layout_choose.setBackgroundColor(getResources().getColor(
						R.color.base_color_text_white));
				layout_photo.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.pop_bg_press));
				File dir = new File(BmobConstants.BMOB_AVATAR_PATH);
				if (!dir.exists()) {
					dir.mkdir();
				}
				// TODO 以后测试一下日期的格式大小写是否有问题
				String savename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date());
				File file = new File(dir, savename);
				filePath = file.getAbsolutePath();
				Uri uri = Uri.fromFile(file);
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			}
		});
		// 本地选取
		layout_choose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				layout_photo.setBackgroundColor(getResources().getColor(
						R.color.base_color_text_white));
				layout_choose.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.pop_bg_press));

				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
			}
		});
		avatarPop = new PopupWindow(view, mScreenHeight, 600);
		avatarPop.setTouchInterceptor(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					avatarPop.dismiss();
					return true;
				}
				return false;
			}
		});
		avatarPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		avatarPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		avatarPop.setTouchable(true);
		avatarPop.setFocusable(true);
		avatarPop.setOutsideTouchable(true);
		// TODO 什么功能
		avatarPop.setBackgroundDrawable(new BitmapDrawable());
		avatarPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
		avatarPop.showAtLocation(layout_all, Gravity.BOTTOM, 0, 0);
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		switch (requestCode) {
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					showToast("SD卡不可用");
					return;
				}
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				startImageAction(Uri.fromFile(file), 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);

			}
			Log.i("onActivityResult", "拍照");
			break;

		case BmobConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
			if (avatarPop != null) {
				avatarPop.dismiss();
			}
			Uri uri = null;
			if (intent == null) {
				return;
			}
			if (resultCode == RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					showToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = intent.getData();
				startImageAction(uri, 200, 200,
						BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			Log.i("onActivityResult", "本地");
			break;
		case BmobConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			if (avatarPop != null) {
				avatarPop.dismiss();
			}
			if (intent == null) {
				return;
			} else {
				saveCropAvator(intent);
			}
			filePath = "";
			uploadAvatar();
			break;
		default:
			break;
		}
	}

	private void uploadAvatar() {
		final BmobFile bmobfile = new BmobFile(new File(path));
		bmobfile.upload(this, new UploadFileListener() {

			@Override
			public void onSuccess() {
				String url = bmobfile.getFileUrl();
				Log.i("uploadAvatar", "上传文件成功:" + bmobfile.getFilename()
						+ "；地址：" + url);
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				showToast("上传头像失败：" + arg1);
			}
		});
	}

	/**
	 * 头像处理
	 * 
	 * @param uri
	 * @param outputX
	 *            ： 输出的宽度
	 * @param outputY
	 *            ：输出的长度
	 * @param requestCode
	 * @param isCrop
	 *            ：是否是裁剪
	 */
	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // 无人脸识别
		startActivityForResult(intent, requestCode);
	}

	/**
	 * 修改后的保存路径
	 */
	String path = "";

	private void saveCropAvator(Intent intent) {

		Bundle extras = intent.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			if (bitmap != null) {
				// 圆角图片
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					// 旋转图片
					bitmap = PhotoUtil.rotaingImageView(bitmap, degree);
				}

				iv_set_avatar.setImageBitmap(bitmap);

				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()) + ".png";
				// path=Environment.getExternalStorageDirectory()+"/BmobChat/Avatar/";
				path = BmobConstants.BMOB_AVATAR_PATH + filename;
				Log.i("saveCropAvator", "保存:" + path);

				PhotoUtil.saveBitMap(BmobConstants.BMOB_AVATAR_PATH, filename,
						bitmap, true);
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	private void updateUserAvatar(final String url) {
		Log.i("updateUserAvatar", "更新用户头像信息:" + url);
		User user = (User) userManager.getCurrentUser(User.class);
		user.setAvatar(url);
		user.update(this, new UpdateListener() {

			@Override
			public void onFailure(int arg0, String msg) {
				showToast("上传失败：" + msg);
			}

			@Override
			public void onSuccess() {
				showToast("上传成功");
				refreshAvatar(url);
				// initMeData();
			}
		});
	}

}
