package com.example.im_zzc.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.example.im_zzc.R;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobUtils;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.rtp.AudioStream;
import android.os.Environment;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class NewRecordPlayClickListener implements OnClickListener {
	private BmobUserManager userManager;
	private Context mContext;
	public static NewRecordPlayClickListener currentPlayListener = null;
	public static boolean isPlaying = false;
	private AnimationDrawable anim = null;
	MediaPlayer mMediaPlayer = null;
	private ImageView iv_voice;

	static BmobMsg currentMsg = null;
	BmobMsg message;// 用于区分两个不同语音的播放
	private String currentUserObjectId;

	public NewRecordPlayClickListener(Context context, BmobMsg msg, ImageView iv) {
		super();
		this.mContext = context;
		iv_voice = iv;
		currentPlayListener = this;
		this.currentMsg = msg;
		this.message = msg;
		userManager = BmobUserManager.getInstance(context);
		currentUserObjectId = userManager.getCurrentUserObjectId();
	}

	@Override
	public void onClick(View v) {
		// 如果正在播放，停止
		if (isPlaying) {
			currentPlayListener.stopPlayRecord();
			// TODO currentMsg有什么用
			if (currentMsg != null
					&& currentMsg.hashCode() == message.hashCode()) {
				currentMsg = null;
				return;
			}
		}
		// 播放
		String localPath = "";
		if (currentUserObjectId.equals(message.getBelongId())) {
			// 播放本地
			localPath = message.getContent().split("&")[0];
			Log.i("本地", localPath);
		} else {
			// 下载后的文件地址
			localPath = getDownloadFilePath(message);
			Log.i("下载", localPath);
		}
		// 根据地址播放文件
		StartPlayRecord(localPath, true);
	}

	/**
	 * 播放音频
	 * 
	 * @param filePath
	 * @param isUseSpeaker
	 *            ：是否外放
	 */
	private void StartPlayRecord(final String filePath, boolean isUseSpeaker) {
		if (!(new File(filePath).exists())) {
			return;
		}
		AudioManager audioManager = (AudioManager) mContext
				.getSystemService(Context.AUDIO_SERVICE);

		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
		}

		if (isUseSpeaker) {
			audioManager.setSpeakerphoneOn(true);
			audioManager.setMode(AudioManager.MODE_NORMAL);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		} else {
			audioManager.setSpeakerphoneOn(false);
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
		}

		try {
			mMediaPlayer.reset();
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			// AssetFileDescriptor fileDescriptor = mContext.getAssets().openFd(
			// filePath);
			// mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),fileDescriptor.getStartOffset(),fileDescriptor.getLength());
			// mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDataSource(fis.getFD());
			mMediaPlayer.prepare();
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					currentMsg = message;
					isPlaying = true;
					mp.start();
					startRecordAnim();
				}
			});
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					stopPlayRecord();
				}
			});

			// // TODO 测试!!!!!!!!!!!!!
			// MediaPlayer media = new MediaPlayer();
			// media.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// final String path = Environment.getExternalStorageDirectory()
			// +
			// "/BmobChat/voice/f8cfb2244a3d519b35e2db35c406b141/63e4b08f8b/1438973201555.amr";
			// File file = new File(filePath);
			// FileInputStream fis = new FileInputStream(file);
			// media.setDataSource(fis.getFD());
			// media.prepare();
			// media.start();
			// media.setOnCompletionListener(new OnCompletionListener() {
			//
			// @Override
			// public void onCompletion(MediaPlayer mp) {
			// Log.i("测试播放@", "结束!");
			// Log.i("测试播放@", filePath);
			// }
			// });
			// //结束!！！！！！！！！！！！！！！

			currentPlayListener = this;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopPlayRecord() {
		stopRecordAnim();
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
		isPlaying = false;
	}

	private void startRecordAnim() {
		if (message.getBelongId().equals(currentUserObjectId)) {
			iv_voice.setImageResource(R.anim.anim_chat_voice_right);
		} else {
			iv_voice.setImageResource(R.anim.anim_chat_voice_left);
		}
		anim = (AnimationDrawable) iv_voice.getDrawable();
		anim.start();
	}

	private void stopRecordAnim() {
		if (message.getBelongId().equals(currentUserObjectId)) {
			iv_voice.setImageResource(R.drawable.voice_left3);
		} else {
			iv_voice.setImageResource(R.drawable.voice_right3);
		}
		if (anim != null) {
			anim.stop();
		}
	}

	public String getDownloadFilePath(BmobMsg msg) {
		// 加密名字
		String accountDir = BmobUtils.string2MD5(userManager
				.getCurrentUserObjectId());
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/BmobChat/voice" + File.separator + accountDir
				+ File.separator + msg.getBelongId());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 文件
		File autoFile = new File(dir, msg.getMsgTime() + ".amr");
		try {
			if (!autoFile.exists()) {
				autoFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return autoFile.getAbsolutePath();
	}
}
