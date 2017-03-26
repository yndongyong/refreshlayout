package com.yndongyong.widget.refreshlayout;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by dongzhiyong on 2017/3/26.
 */

public interface DYHeaderViewProvider {

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position);
}
