package com.example.im_zzc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.im_zzc.R;
import com.example.im_zzc.util.PixelUtil;

public class MyLetterView extends View {

	public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z", "#" };
	private int choose = -1;
	private TextView mTextDialog;
	private Paint paint=new Paint();

	public MyLetterView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public MyLetterView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLetterView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = getWidth();
		int height = getHeight();
		int singleHeight = (height / b.length);
		
		
		for (int i = 0; i < b.length; i++) {
//			paint.setColor(getResources().getColor(R.color.color_bottom_text_normal));
			paint.setColor(Color.parseColor("#9da0a4"));
			paint.setAntiAlias(true);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTextSize(PixelUtil.sp2px(12));
			if (i==choose) {
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			float xPos=width/2-paint.measureText(b[i])/2;
			float yPos=singleHeight*(i+1);
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int action=event.getAction();
		float y=event.getY();
		int current=(int) (y/getHeight()*b.length);
		switch (action) {
		case MotionEvent.ACTION_UP:
			setBackgroundDrawable(new ColorDrawable(0x00000000));
			choose=-1;
			invalidate();
			if (mTextDialog!=null) {
				mTextDialog.setVisibility(View.GONE);
			}
			break;

		default:
			setBackgroundResource(R.drawable.v2_sortlistview_sidebar_background);
			if (choose!=current) {
				if (current>0&&current<b.length) {
					//外部接口
					if (listener!=null) {
						listener.onTouchLetterChangeListener(b[current]);
					}
					if (mTextDialog!=null) {
						mTextDialog.setVisibility(View.VISIBLE);
						mTextDialog.setText(b[current]);
					}
				}
			}
			choose=current;
			invalidate();
			break;
		}
		
		return true;
	}
	/**
	 * 外部接口,监听按下的字母的改变
	 */
	OnTouchLetterChangeListener listener;
	public interface OnTouchLetterChangeListener{
		void onTouchLetterChangeListener(String letter);
	}
	public void setOnTouchLetterChangeListener(OnTouchLetterChangeListener listener){
		this.listener=listener;
	}

	public TextView getTextDialog() {
		return mTextDialog;
	}

	public void setTextDialog(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}
}
