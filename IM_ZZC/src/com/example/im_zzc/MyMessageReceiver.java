package com.example.im_zzc;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

import com.example.im_zzc.activity.ChatActivity;
import com.example.im_zzc.activity.MainActivity;
import com.example.im_zzc.activity.NewFriendActivity;
import com.example.im_zzc.util.CollectionUtils;
import com.example.im_zzc.util.CommonUtil;

public class MyMessageReceiver extends BroadcastReceiver {
	BmobUserManager userManager;
	BmobChatUser currentUser;
	// 事件清单
	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();
	public static int mNewNum = 0;

	@Override
	public void onReceive(Context context, Intent intent) {

		// 获取当前用户
		userManager = BmobUserManager.getInstance(context);
		currentUser = userManager.getCurrentUser();
		// 获得json
		String json = intent.getStringExtra("msg");
		// 判断网络，如果有：转换信息，如果没有：各个事件传递下去
		boolean isNetConnected = CommonUtil.isNetworkAvailable(context);
		if (isNetConnected) {
			parseMessage(context, json);
		} else {
			for (int i = 0; i < ehList.size(); i++) {
				ehList.get(i).onNetChange(isNetConnected);
			}
		}
		// 判断是否有tag
		// tag类型
	}

	private void parseMessage(final Context context, String json) {
		JSONObject jo;
		try {
			jo = new JSONObject(json);
			String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
			if (tag.equals(BmobConfig.TAG_OFFLINE)) {// 下线通知
				if (currentUser != null) {
					if (ehList.size() > 0) {
						// 如果事件不为空，传递下去
						for (int i = 0; i < ehList.size(); i++) {
							ehList.get(i).onOffline();
						}
					} else {
						// 清空数据
						CustomApplication.getInstance().Logout();
					}
				}
			} else {
				String fromId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TARGETID);
				final String toId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TOID);
				String msgTime = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_MSGTIME);
				if (fromId != null
						&& !BmobDB.create(context).isBlackUser(fromId)) {// 如果不在黑名单
					if (TextUtils.isEmpty(tag)) {// 如果没有标签,普通消息的话
						BmobChatManager.getInstance(context).createReceiveMsg(
								json, new OnReceiveListener() {

									@Override
									public void onFailure(int arg0, String arg1) {
										BmobLog.i("获取接收的消息失败：" + arg1);
									}

									@Override
									public void onSuccess(BmobMsg msg) {

										if (ehList.size() > 0) {// 如果还有事件传递下去，即在页面中
											for (int i = 0; i < ehList.size(); i++) {
												ehList.get(i).onMessage(msg);
											}
										} else {// 推送消息
											boolean isAllow = CustomApplication
													.getInstance().getSpUtil()
													.isAllowPushNotify();
											if (isAllow
													&& currentUser != null
													&& toId.equals(currentUser
															.getObjectId())) {
												mNewNum++;
												showMsgNotify(context, msg);
											}
										}
									}
								});
					} else {// 有标签
						if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {// 请求添加
							BmobInvitation invitation = BmobChatManager
									.getInstance(context).saveReceiveInvite(
											json, toId);
							if (currentUser != null
									&& toId.equals(currentUser.getObjectId())) {
								if (ehList.size() > 0) {
									for (int i = 0; i < ehList.size(); i++) {
										ehList.get(i).onAddUser(invitation);
									}
								} else {
									showOtherNotify(
											context,
											invitation.getFromname(),
											toId,
											invitation.getFromname() + "请求添加好友",
											NewFriendActivity.class);
								}
							}
						} else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {// 同意添加
							String username = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_KEY_TARGETUSERNAME);
							BmobUserManager.getInstance(context)
									.addContactAfterAgree(username,
											new FindListener<BmobChatUser>() {

												@Override
												public void onSuccess(
														List<BmobChatUser> arg0) {
													// 保存
													CustomApplication
															.getInstance()
															.setContactList(
																	CollectionUtils
																			.list2map(BmobDB
																					.create(context)
																					.getContactList()));
												}

												@Override
												public void onError(int arg0,
														String arg1) {

												}
											});
							// 显示通知
							showOtherNotify(context, username, toId, username
									+ "同意添加好友", MainActivity.class);
							// 创建一个临时会话--用于在会话界面形成初始会话
							BmobMsg.createAndSaveRecentAfterAgree(context, json);
						} else if (tag.equals(BmobConfig.TAG_READED)) {// 已读回执
							String conversionId = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_READED_CONVERSIONID);
							if (currentUser != null) {
								// 更改某条信息状态
								BmobChatManager.getInstance(context).updateMsgStatus(conversionId, msgTime);
								if (currentUser.getObjectId().equals(toId)) {
									if (ehList.size() > 0) {
										for (EventListener eventListener : ehList) {
											eventListener.onReaded(
													conversionId, msgTime);
										}
									}
								}
							}

						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送其他推动
	 * 
	 * @param context
	 * @param username
	 * @param toId
	 * @param ticker
	 *            内容
	 * @param cls
	 */
	private void showOtherNotify(Context context, String username, String toId,
			String ticker, Class<?> cls) {
		boolean isAllowVoice = CustomApplication.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil()
				.isAllowVibrate();
		boolean isAllowNotify = CustomApplication.getInstance().getSpUtil()
				.isAllowPushNotify();

		BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,
				isAllowVibrate, R.drawable.ic_launcher, ticker, username,
				ticker.toString(), NewFriendActivity.class);
	}

	/**
	 * 发送聊天消息推送
	 * 
	 * @param context
	 * @param msg
	 */
	private void showMsgNotify(Context context, BmobMsg msg) {
		int icon = R.drawable.ic_launcher;
		String trueMsg = "";
		if (msg.getMsgType().equals(BmobConfig.TYPE_TEXT)
				&& msg.getContent().contains("\\ue")) {
			trueMsg = "[表情]";
		} else if (msg.getMsgType().equals(BmobConfig.TYPE_IMAGE)) {
			trueMsg = "[图片]";
		} else if (msg.getMsgType().equals(BmobConfig.TYPE_LOCATION)) {
			trueMsg = "[位置]";
		} else if (msg.getMsgType().equals(BmobConfig.TYPE_VOICE)) {
			trueMsg = "[声音]";
		} else {
			trueMsg = msg.getContent();
		}
		String tickerText = msg.getBelongUsername() + ":" + trueMsg;
		String title = msg.getBelongUsername() + "(" + mNewNum + "条新消息)";

		Intent intent = new Intent(context, MainActivity.class);
		// 设置之后如果堆栈有这个activity则不会启动一个新的activity
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		boolean isAllowVoice = CustomApplication.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil()
				.isAllowVibrate();

		BmobNotifyManager.getInstance(context).showNotifyWithExtras(
				isAllowVoice, isAllowVibrate, icon, tickerText.toString(),
				title, tickerText.toString(), intent);
	}
}
