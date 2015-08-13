package com.example.im_zzc.activity;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.im_zzc.R;
import com.example.im_zzc.activity.base.ActivityBase;
import com.example.im_zzc.util.ImageLoadOptions;
import com.example.im_zzc.view.CustomViewPager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageBrowserActivity extends ActivityBase implements OnPageChangeListener {
	private CustomViewPager mViewPager;
	private ArrayList<String> mPhotos = new ArrayList<String>();
	private int mPosition;

	 private ImageBrowserAdpater mAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_showpicture);
		init();
		initView();
	}

	private void init() {
		mPhotos = getIntent().getStringArrayListExtra("photos");
		mPosition = getIntent().getIntExtra("position", 0);
	}

	private void initView() {
		mViewPager = (CustomViewPager) findViewById(R.id.showpicture_viewpager);
		mAdapter=new ImageBrowserAdpater(this);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(mPosition);
		mViewPager.setOnPageChangeListener(this);
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		mPosition=arg0;
	}

	class ImageBrowserAdpater extends PagerAdapter {
		private LayoutInflater inflater;
		
		public  ImageBrowserAdpater(Context context){
			inflater=LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return mPhotos.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			FrameLayout imageLayout=(FrameLayout) inflater.inflate(R.layout.item_show_picture, null);
			final ProgressBar progress=(ProgressBar) imageLayout.findViewById(R.id.item_progress);
			PhotoView photoView=(PhotoView) imageLayout.findViewById(R.id.item_photoview);
			String imageUrl=mPhotos.get(position);
			ImageLoader.getInstance().displayImage(imageUrl, photoView,ImageLoadOptions.getOptions(),new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					progress.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					progress.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					progress.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					progress.setVisibility(View.GONE);
				}
			});
			container.addView(imageLayout);
			return imageLayout;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			container.removeView((View) object);
		}
	}

	
}
