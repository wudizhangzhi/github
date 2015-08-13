package com.example.im_zzc.view.xlist;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.example.im_zzc.R;

public class XListView extends ListView implements OnScrollListener {
	private static final float OFFSET_RADIO = 1.8f;
	private static final int SCROLL_DURATION = 400;// scroll back 的时间
	private static final int SCROLLBACK_HEADER = 0;// 返回header
	private static final int SCROLLBACK_FOOTER = 1;// 返回footer
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
	// at bottom, trigger
	// load more.

	private XListViewHeader mHeader;
	private XListViewFooter mFooter = null;
	private RelativeLayout mHeaderContainer;

	private Scroller mScroll;

	private int mHeaderViewHeight;
	private float mLastY;
	private boolean mEnablePullRefreshing = true;
	private boolean mRefreshing = false;
	private boolean mEnablePullLoad = false;
	private boolean mPullLoading = false;
	private int mScrollBack;
	private int mTotalItemCount;
	private OnScrollListener mScrollListener;
	private Context mContext;

	public XListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		mContext = context;
		mScroll = new Scroller(context, new DecelerateInterpolator());
		super.setOnScrollListener(this);

		mHeader = new XListViewHeader(context);
		mHeaderContainer = (RelativeLayout) mHeader
				.findViewById(R.id.xlistview_header_content);
		addHeaderView(mHeader);
		// TODO 为什么不直接getHight()
		mHeader.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderContainer.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	// TODO 继承的哪里
	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	/**
	 * 可以使用这个定制的ScrollListener
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * 判断你使用的是自制的监听还是原本的
	 */
	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	/**
	 * 是否刷新 按下手指：mLastY=getrawY； 移动手指： 手指下滑距离：distanceX=getrawY-mLastY；
	 * firstvisiableItemPosition
	 * ==0&&mHeaderView.getVisiableHeight>0&&distanceX>0 :
	 * distanceX>mHeaderViewHeight : header变为ready状态，header高度变大；
	 * distanceX<mHeaderViewHeight：普通状态 更新footer的情况 松开手指：
	 * firstvisiableItemPosition
	 * ==0&&mHeaderView.getVisiableHeight>0&&distanceX>0 :
	 * distanceX>mHeaderViewHeight : 变为refreshing状态，header高度还原；
	 * distanceX<mHeaderViewHeight：普通状态，高度还原，外部接口 更新footer的情况
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO 判断motionevent.action_up的方法是否正常
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			float distanceY = ev.getRawY() - mLastY;
			if (getFirstVisiblePosition() == 0
					&& mHeader.getVisiableHeight() > 0 && distanceY > 0) {
				// 更新header状态
				updateHeaderHeight(distanceY / OFFSET_RADIO);
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& mEnablePullLoad
					&& (mFooter.getBottomMargin() < PULL_LOAD_MORE_DELTA || distanceY < 0)) {// 显示最后一个，footer没有完全显示，向上拉中
				updateFooterMargin(-distanceY / OFFSET_RADIO);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (getFirstVisiblePosition() == 0) {
				if (mEnablePullRefreshing
						&& mHeader.getVisiableHeight() > mHeaderViewHeight) {
					mRefreshing = true;
					mHeader.setState(XListViewHeader.STATE_REFRESHLING);
					if (mXListViewListener != null) {
						mXListViewListener.onRefresh();
					}
					resetHeaderHeight();
				}
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				if (mEnablePullLoad) {
					if (mFooter.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
						startLoadMore();
					}
					resetFooterHeight();
				}
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * footer点击后的 开始加载更多
	 */
	private void startLoadMore() {
		mPullLoading = true;
		mFooter.setState(XListViewFooter.STATE_REFRESHING);
		if (mXListViewListener != null) {
			mXListViewListener.onLoadMore();
		}
	}

	/**
	 * 更新
	 * 
	 * @param distance
	 */
	private void updateFooterMargin(float distance) {
		int height = (int) (distance + mFooter.getBottomMargin());
		if (height > PULL_LOAD_MORE_DELTA) {
			mFooter.setState(XListViewFooter.STATE_READY);
		} else {
			mFooter.setState(XListViewFooter.STATE_NORMAL);
		}
		mFooter.setBottomMargin(height);
	}

	/**
	 * 更新header的高度
	 */
	private void updateHeaderHeight(float distance) {
		mHeader.setVisiableHeight((int) distance + mHeader.getVisiableHeight());
		if (mEnablePullRefreshing && !mRefreshing) {// 如果允许刷新且不在刷新中
			if (mHeader.getVisiableHeight() > mHeaderViewHeight) {
				mHeader.setState(XListViewHeader.STATE_READY);
			} else {
				mHeader.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0);// 每次回到顶端
	}

	/**
	 * 重置header高度
	 */
	private void resetHeaderHeight() {
		int height = mHeader.getVisiableHeight();
		if (height == 0) {
			return;
		}
		// 正在刷新且没有完全展示
		if (mRefreshing && height < mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0;
		if (mRefreshing && height < mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroll.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
		invalidate();
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooter.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroll.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	@Override
	public void computeScroll() {
		if (mScroll.computeScrollOffset()) {// 移动动画停止
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeader.setVisiableHeight(mScroll.getCurrY());
			} else {
				mFooter.setBottomMargin(mScroll.getCurrY());
			}
			// TODO 为什么不在UIthread上
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	private XListViewListener mXListViewListener;

	/**
	 * 外部接口,刷新和加载更多时候的监听
	 */
	public interface XListViewListener {
		void onRefresh();

		void onLoadMore();
	}

	public void setXListViewListener(XListViewListener listener) {
		this.mXListViewListener = listener;
	}

	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefreshing = enable;
		if (mEnablePullRefreshing) {
			mHeader.setVisibility(View.VISIBLE);
		} else {
			mHeader.setVisibility(View.INVISIBLE);
		}
	}

	public void setPullLoadEnable(boolean enable) {
		if (enable == mEnablePullLoad) {
			return;
		}
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			// 不允许加载，移除footer
			if (mFooter != null) {
				this.removeView(mFooter);
			}
		} else {
			if (mFooter == null) {
				mFooter = new XListViewFooter(mContext);
				mFooter.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						startLoadMore();
					}
				});
				addFooterView(mFooter);
				mFooter.setState(XListViewFooter.STATE_NORMAL);
			}
		}
	}

	public void pullRefreshing() {
		if (!mEnablePullRefreshing) {
			return;
		}
		mRefreshing = true;
		mHeader.setVisibility(View.VISIBLE);
		mHeader.setState(XListViewHeader.STATE_REFRESHLING);
	}

	public void stopRefreshing() {
		Time time = new Time();
		time.setToNow();
		mHeader.setRefreshTime(time.format("%Y-%m-%d %T"));
		if (mRefreshing == true) {
			mRefreshing = false;
			resetHeaderHeight();
		}
	}

	public void stopLoadMore() {
		if (mPullLoading) {
			mPullLoading = false;
			mFooter.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	public boolean getPullLoading() {
		return mPullLoading;
	}

	public boolean getPullRefreshing() {
		return mRefreshing;
	}
}
