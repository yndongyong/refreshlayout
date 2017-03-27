package com.yndongyong.widget.refreshlayout;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongzhiyong on 2017/3/24.
 */

public abstract class DYRecylerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mData = new ArrayList<>();
    protected Context mContext;
    protected LayoutInflater mInflater;


    //head view
    private DYHeaderViewProvider mHeaderView;

    //adapter的几种类型
    public static final int MODEL_NEIGTHER = -1;
    public static final int MODEL_HEADER = 0;
    public static final int MODEL_FOOTER = 1;
    public static final int MODEL_HEADER_FOOTER = 2;

    //item view 类型
    public static final int VIEW_TYPE_HEADER = 3;
    public static final int VIEW_TYPE_FOOTER = 4;
    public static final int VIEW_TYPE_NORMAL = 5;

    //footer view 的几种状态
    public static final int STATUS_HIDE = -1;
    public static final int STATUS_LOADING = 0;
    public static final int STATUS_LOADING_ERROR = 1;
    public static final int STATUS_NO_MORE = 2;
    public static final int STATUS_INVALID_NETWORK = 3;
    public static final int STATUS_LOAD_MORE = 4;


    //adapter 当前的类型 控制是是否有header或者footer;
    private int mModel;
    //footer view 当前的类型
    private int mStatus;


    public DYRecylerAdapter(Context _context, int model) {
        this.mContext = _context;
        this.mModel = model;
        this.mStatus = STATUS_HIDE;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public int getStatus() {
        return mStatus;
    }

    public T getItem(int position) {
        return mData.get(getIndex(position));
    }

    /**
     * 设置headerview
     *
     * @param view
     */
    public void setHeaderView(DYHeaderViewProvider view) {
        this.mHeaderView = view;
    }

    /**
     * 设置adapter是否含有header 或者footer;
     *
     * @param model
     */
    public void setModel(int model) {
        this.mModel = model;
    }

    /**
     * 设置footer 的样式
     *
     * @param status
     * @param isUpdate 是否要更新最后一个项目的
     */
    public void setStatus(int status, boolean isUpdate) {
        this.mStatus = status;
        if (isUpdate) {

            //Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout
            // pass where you cannot change theRecyclerView data.
            // Any method call that might change the structureof the RecyclerView or
            // the adapter contents should be postponed tothe next frame.
            // TODO: 2017/3/27  如果不注释 下面的代码会报如上的错误信息
//            int position = getItemCount() - 1;
//            notifyItemChanged(position);
        }

    }

    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, T item, int position);


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                if (mHeaderView == null) {
                    throw new IllegalArgumentException("headerview can not be empty where if status type is View_type_header");
                }
                return mHeaderView.onCreateViewHolder(parent, viewType);
            case VIEW_TYPE_FOOTER:
                return new FooterViewHolder(mInflater.inflate(R.layout.recylcerview_footer_layout, parent, false));
            default:
                return onCreateViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
                mHeaderView.onBindViewHolder(holder, position);
                break;
            case VIEW_TYPE_FOOTER:
                FooterViewHolder footerHolder = (FooterViewHolder) holder;
                footerHolder.itemView.setVisibility(View.VISIBLE);
                switch (mStatus) {
                    case STATUS_HIDE:
                        footerHolder.itemView.setVisibility(View.GONE);
                        break;
                    case STATUS_INVALID_NETWORK:
                        footerHolder.pbProgress.setVisibility(View.GONE);
                        footerHolder.tvTips.setText("网络错误");
                        break;
                    case STATUS_NO_MORE:
                        footerHolder.pbProgress.setVisibility(View.GONE);
                        footerHolder.tvTips.setText("没有更多数据");
                        break;
                    case STATUS_LOADING:
                        footerHolder.pbProgress.setVisibility(View.VISIBLE);
                        footerHolder.tvTips.setText("正在加载...");
                        break;
                    case STATUS_LOAD_MORE:
                        footerHolder.pbProgress.setVisibility(View.VISIBLE);
                        footerHolder.tvTips.setText("正在加载更多");
                        break;
                    case STATUS_LOADING_ERROR:
                        footerHolder.pbProgress.setVisibility(View.GONE);
                        footerHolder.tvTips.setText("加载失败");
                        break;
                }
                break;
            default:
                onBindViewHolder(holder, mData.get(getIndex(position)), position);
                break;
        }

    }

    /**
     * 当添加到recycleView 布局管理器为gridelayout 的时候修正header和footer的显示一整行
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == VIEW_TYPE_HEADER ||
                            getItemViewType(position) == VIEW_TYPE_FOOTER ? gridLayoutManager.getSpanCount() : 1;
                }
            });

        }
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * 修正staggeredgridlayoutmanager 时，显示header和footer为一整行
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            if (mModel == MODEL_HEADER) {
                p.setFullSpan(holder.getAdapterPosition() == 0);
            } else if (mModel == MODEL_FOOTER) {
                p.setFullSpan(mData.size() + 1 == holder.getAdapterPosition());
            } else if (mModel == MODEL_HEADER_FOOTER) {
                if (holder.getAdapterPosition() == 0 || holder.getAdapterPosition() == mData.size() + 1) {
                    p.setFullSpan(true);
                }
            }
        }
    }

    public int getIndex(int position) {
        return mModel == MODEL_HEADER || mModel == MODEL_HEADER_FOOTER ? position - 1 : position;
    }

    public int getCount() {
        return mData.size();
    }

    public List<?> getmData() {
        return mData;
    }

    public void addItem(T item) {
        if (item != null) {
            mData.add(item);
            notifyItemInserted(mData.size());
        }
    }

    public void addItem(int position, T item) {
        if (position >= 0 && position < mData.size() && item != null) {
            mData.add(getIndex(position), item);
            notifyItemInserted(position);
        }
    }

    public void updateItem(int position, T item) {
        if (position >= 0 && position < mData.size() && item != null) {
            mData.add(getIndex(position), item);
            notifyItemInserted(position);
        }
    }

    public void updateItem(int postion) {
        if (postion < getItemCount()) {
            notifyItemChanged(postion);
        }
    }

    public void removeItem(int position, T item) {
        if (position >= 0 && position < mData.size() && item != null) {
            mData.remove(getIndex(position));
            notifyItemRemoved(position);
        }
    }

    public void addNewData(List<T> list) {
        if (list != null && list.size() > 0) {
            mData.clear();
            mData.addAll(list);
//            notifyItemRangeChanged(mData.size(), list.size());
            notifyDataSetChanged();
        }
    }

    public void addMoreData(List<T> list) {
        if (list != null && list.size() > 0) {
            int offset = mData.size();
            mData.addAll(list);
            notifyItemRangeInserted(offset, list.size());
        }
    }

    public void clear() {
        mData.clear();

    }

    public void clear(boolean isUpdate) {
        mData.clear();
        if (isUpdate)
            notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mModel == MODEL_HEADER_FOOTER) return mData.size() + 2;
        if (mModel == MODEL_HEADER || mModel == MODEL_FOOTER)
            return mData.size() + 1;
        return mData != null ? mData.size() : 0;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && (mModel == MODEL_HEADER || mModel == MODEL_HEADER_FOOTER))
            return VIEW_TYPE_HEADER;
        if (position + 1 == getItemCount() && (mModel == MODEL_FOOTER || mModel == MODEL_HEADER_FOOTER))
            return VIEW_TYPE_FOOTER;
        else return VIEW_TYPE_NORMAL;
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {

        ProgressBar pbProgress;
        TextView tvTips;

        public FooterViewHolder(View itemView) {
            super(itemView);
            pbProgress = (ProgressBar) itemView.findViewById(R.id.pv_footer);
            tvTips = (TextView) itemView.findViewById(R.id.tv_footer);
        }
    }
}

