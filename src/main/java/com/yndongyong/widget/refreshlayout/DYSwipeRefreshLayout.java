package com.yndongyong.widget.refreshlayout;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by dongzhiyong on 2017/3/23.
 */

public class DYSwipeRefreshLayout extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;

    private long mTouchSlop;
    private int mYDown;
    private int mLastY;


    private boolean mCanLoadMore = true;
    private boolean mCurrentIsLoading = false;

    private boolean mHasMore = true;

    DYSwipeRefreshLayoutListener listener;

    public DYSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public DYSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setOnRefreshListener(this);
        setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_light)
                , getResources().getColor(android.R.color.holo_green_dark));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mRecyclerView == null) {
            getRecyclerView();
            if (mRecyclerView == null) {
                throw new IllegalArgumentException("the direct child view can not be null");
            }
        }
    }

    public void getRecyclerView() {
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            if (!(child instanceof RecyclerView)) {
                child = findViewById(R.id.recyclerView);
            }
            if (child != null && child instanceof RecyclerView) {
                mRecyclerView = (RecyclerView) child;
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if (canLoad()) {
                            loadData();
                        }
                    }


                });
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mYDown = (int) ev.getRawY();
                if (isRefreshing()) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_HOVER_MOVE:
                mLastY = (int) ev.getRawY();
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean canLoad() {
        return hasLastItemVisible() && isPullUp() && mCanLoadMore && !isRefreshing() && !mCurrentIsLoading && mHasMore;
    }

    private boolean hasLastItemVisible() {
        return (mRecyclerView != null && mRecyclerView.getAdapter() != null) &&
                getLastItemPosition() == (mRecyclerView.getAdapter().getItemCount() - 1);
    }

    private boolean isPullUp() {
        return (mYDown - mLastY) >= mTouchSlop;
    }


    private int getLastItemPosition() {
        int position;
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (mRecyclerView.getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] positions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()]);
            position = getMaxPosition(positions);
        } else {
            position = layoutManager.getItemCount() - 1;
        }
        return position;
    }

    private int getMaxPosition(int[] positions) {
        int max = Integer.MIN_VALUE;
        for (int position : positions) {
            max = Math.max(max, position);
        }
        return max;
    }


    private void setOnLoading(boolean isloading) {
        mCurrentIsLoading = isloading;
        if (!mCurrentIsLoading) {
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * 对外暴露 是否可以加载更多
     *
     * @param enable true  :可以加载更多
     */
    public void setLoadMoreEnable(boolean enable) {
        mCanLoadMore = enable;

    }

    @Override
    public void onRefresh() {
        if (listener != null && !mCurrentIsLoading) {
            listener.onRefresh();
        }
    }

    private void loadData() {
        if (listener != null) {
            setOnLoading(true);
            listener.onLoadMore();
        }
    }

    /**
     * 刷新或者加载完时，调用,默认有更多数据
     */
    public void onComplete() {
        setOnLoading(false);
        setRefreshing(false);
        mHasMore = true;
    }

    /**
     * 刷新或者加载完时，调用 控制是否还有更多的数据
     */
    public void onComplete(boolean hasMore) {
        setOnLoading(false);
        setRefreshing(false);
        mHasMore = hasMore;
    }


    public void setDYSwipeRefreshLayoutListener(DYSwipeRefreshLayoutListener listener) {
        this.listener = listener;
    }

    public interface DYSwipeRefreshLayoutListener {

        void onRefresh();

        void onLoadMore();
    }
}
