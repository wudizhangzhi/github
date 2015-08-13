package com.example.im_zzc.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.sax.StartElementListener;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobDownloadManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.DownloadListener;
import cn.bmob.v3.listener.FindListener;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.ImageBrowserActivity;
import com.example.im_zzc.activity.SetMyInfoActivity;
import com.example.im_zzc.adapter.base.BaseListAdapter;
import com.example.im_zzc.adapter.base.ViewHolder;
import com.example.im_zzc.bean.User;
import com.example.im_zzc.util.FaceTextUtil;
import com.example.im_zzc.util.ImageLoadOptions;
import com.example.im_zzc.util.TimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class MessageChatAdapter extends BaseListAdapter<BmobMsg> {
	// 8种item的类型
	// 文本
	private final int TYPE_RECEICER_TEXT = 0;
	private final int TYPE_SEND_TEXT = 1;
	// 图片
	private final int TYPE_RECEICER_IMAGE = 2;
	private final int TYPE_SEND_IMAGE = 3;
	// 位置
	private final int TYPE_RECEICER_LOCATION = 4;
	private final int TYPE_SEND_LOCATION = 5;
	// 语音
	private final int TYPE_RECEICER_VOICE = 6;
	private final int TYPE_SEND_VOICE = 7;
	//
	private BmobChatUser currentUser;
	private String currentUserObjectId = "";
	
	DisplayImageOptions Options;
	private AnimateFirstDisplayListener animateFirstDisplayListener = new AnimateFirstDisplayListener();

	public MessageChatAdapter(Context context, List<BmobMsg> list,BmobChatUser target,BmobChatUser user) {
		super(context, list,target,user);
		// mContext = context;
		// this.list = list;
		// mInflater = LayoutInflater.from(context);
//		currentUser=BmobUserManager.getInstance(mContext).getCurrentUser(User.class);
		currentUser=user;
		currentUserObjectId = BmobUserManager.getInstance(mContext)
				.getCurrentUserObjectId();

		Options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheOnDisc(true)
				.resetViewBeforeLoading(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				// 比例刚好
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300))// 图片加载好后渐入动画时间
				.build();
		Log.i("消息适配器","adapter初始化适配器!!!!!target："+target.getAvatar());
		Log.i("消息适配器","adapter初始化适配器!!!!!userid："+currentUser.getObjectId()+";"+currentUser.getAvatar());
	}
	

	@Override
	public int getViewTypeCount() {
		return 8;
	}

	@Override
	public int getItemViewType(int position) {
		BmobMsg msg = getList().get(position);
		int type = msg.getMsgType();
		if (type == BmobConfig.TYPE_LOCATION) {
			return msg.getBelongId().equals(currentUserObjectId) ? TYPE_SEND_LOCATION
					: TYPE_RECEICER_LOCATION;
		} else if (type == BmobConfig.TYPE_IMAGE) {
			return msg.getBelongId().equals(currentUserObjectId) ? TYPE_SEND_IMAGE
					: TYPE_RECEICER_IMAGE;
		} else if (type == BmobConfig.TYPE_VOICE) {
			return msg.getBelongId().equals(currentUserObjectId) ? TYPE_SEND_VOICE
					: TYPE_RECEICER_VOICE;
		} else {// 其他默认为text
			return msg.getBelongId().equals(currentUserObjectId) ? TYPE_SEND_TEXT
					: TYPE_RECEICER_TEXT;
		}
	}

	@Override
	public View bindView(final int position, View convertView, ViewGroup parent) {
		final BmobMsg item = list.get(position);
		
		if (convertView == null) {
			convertView = creatViewByType(position, item);
		}
		// 文本类型
		TextView tv_msg = ViewHolder
				.get(convertView, R.id.item_chat_tv_message);
		TextView tv_time = ViewHolder.get(convertView, R.id.item_chat_tv_time);
		TextView tv_send_status = ViewHolder.get(convertView,
				R.id.item_chat_tv_send_status);
		// 图片
		final ImageView iv_avatar = ViewHolder.get(convertView,
				R.id.item_chat_iv_avatar);
		ImageView iv_fail_resend = ViewHolder.get(convertView,
				R.id.item_chat_iv_fail_resend);
		ImageView iv_picture = ViewHolder.get(convertView,
				R.id.item_chat_iv_picture);
		// 语音
		final ImageView iv_voice = ViewHolder.get(convertView,
				R.id.item_chat_iv_voice);
		final TextView tv_voice_length = ViewHolder.get(convertView,
				R.id.item_chat_tv_voice_length);
		// 位置
		TextView tv_location = ViewHolder.get(convertView,
				R.id.item_chat_tv_location);
		// 进度
		final ProgressBar progress_load = ViewHolder.get(convertView,
				R.id.item_chat_progress_load);

		// 设置头像
//		 final String avatar = item.getBelongAvatar();
		String avatar ="";
		if (mTargetUser!=null) {
			if (currentUserObjectId.equals(item.getBelongId())) {//自己发出的消息
				avatar= currentUser.getAvatar();
			}else {//对方
				avatar=mTargetUser.getAvatar();
			}
		}
		
		if ( avatar != null&&!avatar.equals("") ) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions(), animateFirstDisplayListener);
		} else {//头像为空时候的默认头像
			iv_avatar.setBackgroundResource(R.drawable.head);
		}
		iv_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击头像进入个人资料
				Intent intent = new Intent(mContext, SetMyInfoActivity.class);
				if (getItemViewType(position) == TYPE_RECEICER_IMAGE
						|| getItemViewType(position) == TYPE_RECEICER_LOCATION
						|| getItemViewType(position) == TYPE_RECEICER_TEXT
						|| getItemViewType(position) == TYPE_RECEICER_VOICE) {
					intent.putExtra("from", "other");
					intent.putExtra("username", item.getBelongUsername());
				} else {
					intent.putExtra("from", "me");
				}
				mContext.startActivity(intent);
			}
		});

		// 设置状态
		if (getItemViewType(position) == TYPE_SEND_LOCATION
				|| getItemViewType(position) == TYPE_SEND_TEXT
				|| getItemViewType(position) == TYPE_SEND_VOICE) {// 发送的内容
			if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// 发送成功
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
					tv_voice_length.setVisibility(View.VISIBLE);
					tv_send_status.setVisibility(View.GONE);
				} else {
					// 不是语音就显示发送状态
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("已发送");
				}
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) {// 发送失败
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
					tv_voice_length.setVisibility(View.GONE);
				}
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {// 对方收到
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
					tv_voice_length.setVisibility(View.VISIBLE);
					tv_send_status.setVisibility(View.INVISIBLE);
				} else {
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("已阅读");
				}
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_START) {// 开始发送
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if (item.getMsgType() == BmobConfig.TYPE_VOICE) {
					tv_voice_length.setVisibility(View.GONE);
				}
			}
		}
		// 显示时间
		tv_time.setText(TimeUtil.getChatTime(Long.parseLong(item.getMsgTime())));
		// 根据类型显示内容
		String content = item.getContent();
		switch (item.getMsgType()) {
		case BmobConfig.TYPE_TEXT:
			SpannableString spannableString = FaceTextUtil.toSpannableString(
					mContext, content);
			tv_msg.setText(spannableString);
			break;
		case BmobConfig.TYPE_IMAGE:
			if (content != "" && !content.equals("")) {
				dealWithImage(item, iv_picture, iv_fail_resend, tv_send_status,
						progress_load, position);
			}
			iv_picture.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO 传入数据并进入页面
					Intent intent = new Intent(mContext,
							ImageBrowserActivity.class);
					ArrayList<String> photos = new ArrayList<String>();
					photos.add(getImageUrl(item));
					intent.putExtra("photos", photos);
					mContext.startActivity(intent);
				}
			});
			break;
		case BmobConfig.TYPE_LOCATION:
			if (content != "" && !content.equals("")) {
				String adress = content.split("&")[0];
				String latitude = content.split("&")[1];// 纬度
				String longtitude = content.split("&")[2];// 经度
				tv_location.setText(adress);
				tv_location.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO 传入数据进入地图页

					}
				});
			}
			break;
		case BmobConfig.TYPE_VOICE:
			if (content != "" && !content.equals("")) {
				if (item.getBelongId().equals(currentUserObjectId)) {// 发送的消息
					if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED
							|| item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// 发送成功或者收到
						String length = content.split("&")[2];
						tv_voice_length.setText(length + "\''");
					}
				} else {// 收到的消息
					boolean isExists = BmobDownloadManager
							.checkTargetPathExist(currentUserObjectId, item);
					if (!isExists) {// 不存在，需要下载,因为文件比小，所以在这里下载
						// TODO 下载可以放在点击事件里面
						String netUrl = content.split("&")[0];
						Log.i("下载地址", netUrl);
						final String length = content.split("&")[1];
						BmobDownloadManager downloadTask = new BmobDownloadManager(
								mContext, item, new DownloadListener() {

									@Override
									public void onSuccess() {
										tv_voice_length
												.setVisibility(View.VISIBLE);
										tv_voice_length.setText(length + "\''");
										iv_voice.setVisibility(View.VISIBLE);// 只有完成才显示播放按钮
										progress_load
												.setVisibility(View.INVISIBLE);
									}

									@Override
									public void onStart() {
										tv_voice_length
												.setVisibility(View.GONE);
										iv_voice.setVisibility(View.INVISIBLE);
										progress_load
												.setVisibility(View.VISIBLE);
									}

									@Override
									public void onError(String arg0) {
										progress_load.setVisibility(View.GONE);
										tv_voice_length
												.setVisibility(View.GONE);
										iv_voice.setVisibility(View.INVISIBLE);
									}
								});
						downloadTask.execute(netUrl);
					} else {// 已经下载
						String length = content.split("&")[2];
						tv_voice_length.setText(length + "\''");
					}
				}
				// 播放语音文件监听
				iv_voice.setOnClickListener(new NewRecordPlayClickListener(
						mContext, item, iv_voice));
			}
			break;

		default:
			break;
		}

		return convertView;
	}

	/**
	 * 处理图像
	 * 
	 * @param position
	 * @param progress_load
	 * @param tv_send_status
	 * @param iv_fail_resend
	 * @param iv_picture
	 * @param item
	 */
	private void dealWithImage(BmobMsg item, ImageView iv_picture,
			ImageView iv_fail_resend, TextView tv_send_status,
			final ProgressBar progress_load, int position) {
		// 控件的显示
		if (getItemViewType(position) == TYPE_SEND_IMAGE) {// 发送的图片才显示状态
			if (item.getStatus() == BmobConfig.STATUS_SEND_SUCCESS) {// 成功
				progress_load.setVisibility(View.GONE);
				iv_fail_resend.setVisibility(View.GONE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("已发送");
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_RECEIVERED) {// 接收
				progress_load.setVisibility(View.GONE);
				iv_fail_resend.setVisibility(View.GONE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("已接收");
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_FAIL) {// 失败
				progress_load.setVisibility(View.GONE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.VISIBLE);
				tv_send_status.setText("发送失败");
			} else if (item.getStatus() == BmobConfig.STATUS_SEND_START) {// 开始
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.GONE);
				tv_send_status.setVisibility(View.GONE);
			}
			// 加载图像
			String path = getImageUrl(item);
			ImageLoader.getInstance().displayImage(path, iv_picture);
		} else {// 接收的
			ImageLoader.getInstance().displayImage(item.getContent(),
					iv_picture, Options, new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							progress_load.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
							progress_load.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							progress_load.setVisibility(View.INVISIBLE);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
							progress_load.setVisibility(View.INVISIBLE);
						}
					});
		}
	}

	/**
	 * 根据消息得到图片地址
	 * 
	 * @param item
	 * @return
	 */
	private String getImageUrl(BmobMsg item) {
		String showUrl = "";
		String text = item.getContent();
		if (item.getBelongId().equals(currentUserObjectId)) {// 发送的图片
			if (text.contains("&")) {
				showUrl = text.split("&")[0];
			} else {
				showUrl = text;
			}
		} else {// 接收的图片
			showUrl = text;
		}
		return showUrl;
	}

	/**
	 * 根据item位置获取不同类型的layout
	 */
	private View creatViewByType(int position, BmobMsg msg) {
		int type = msg.getMsgType();
		if (type == BmobConfig.TYPE_IMAGE) {
			return getItemViewType(position) == TYPE_RECEICER_IMAGE ? mInflater
					.inflate(R.layout.item_chat_received_image, null)
					: mInflater.inflate(R.layout.item_chat_sent_image, null);
		} else if (type == BmobConfig.TYPE_LOCATION) {
			return getItemViewType(position) == TYPE_RECEICER_LOCATION ? mInflater
					.inflate(R.layout.item_chat_received_location, null)
					: mInflater.inflate(R.layout.item_chat_sent_location, null);
		} else if (type == BmobConfig.TYPE_VOICE) {
			return getItemViewType(position) == TYPE_RECEICER_VOICE ? mInflater
					.inflate(R.layout.item_chat_received_voice, null)
					: mInflater.inflate(R.layout.item_chat_sent_voice, null);
		} else {// 其他默认都是文本
			return getItemViewType(position) == TYPE_RECEICER_TEXT ? mInflater
					.inflate(R.layout.item_chat_received_message, null)
					: mInflater.inflate(R.layout.item_chat_sent_message, null);
		}
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			ImageView imageView = (ImageView) view;
			boolean firstDisplay = !displayedImages.contains(imageUri);
			if (firstDisplay) {
				FadeInBitmapDisplayer.animate(imageView, 500);
				displayedImages.add(imageUri);
			}
		}
	}
}
