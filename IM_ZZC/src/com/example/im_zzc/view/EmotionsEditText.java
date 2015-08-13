package com.example.im_zzc.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 聊天界面输入框
 */
public class EmotionsEditText extends EditText {

	public EmotionsEditText(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public EmotionsEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EmotionsEditText(Context context) {
		super(context);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(text)) {
			super.setText(replace(text.toString()), type);
		} else {
			super.setText(text, type);
		}
	}

	private CharSequence replace(String text) {
		try {
			SpannableString spannablestring = new SpannableString(text);
			Pattern pattern = Pattern.compile("////ue[a-z0-9]{3}",
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			int start=0;
			while (matcher.find()) {
				String facetext = matcher.group();
				String key = facetext.substring(1);
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeResource(
						getContext().getResources(),
						getContext().getResources().getIdentifier(key, "drawable",
								getContext().getPackageName()), options);
				ImageSpan imagespan = new ImageSpan(bitmap);
				int indexStart = text.indexOf(facetext,start);
				int indexEnd = indexStart + facetext.length();
				spannablestring.setSpan(imagespan, indexStart, indexEnd,
						spannablestring.SPAN_EXCLUSIVE_EXCLUSIVE);
				//TODO 测试不-1
				start=indexEnd;
			}
			return spannablestring;
		} catch (Exception e) {
			return text;
		}
	}
}
