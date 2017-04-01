package debug;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.yndongyong.widget.refreshlayout.DYOnRecyclerItemClickListener;
import com.yndongyong.widget.refreshlayout.DYRecyclerAdapter;
import com.yndongyong.widget.refreshlayout.DYSwipeRefreshLayout;
import com.yndongyong.widget.refreshlayout.R;

import java.util.ArrayList;
import java.util.List;

public class LinearLayoutTestActivity extends AppCompatActivity implements DYSwipeRefreshLayout.DYSwipeRefreshLayoutListener {

    DYSwipeRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    MyAdapter mAdapter;
    List<String> mData = new ArrayList<>();

    boolean loadMoreEnable = true;

    int offset = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshLayout = (DYSwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setLoadMoreEnable(loadMoreEnable);
//        refreshLayout.setLoadMoreEnable(false);当refreshlayout处于不可以加载更多的时候，
//        不能设置adapter包含footer，不然会显示一个空行
        mAdapter = new MyAdapter(this, DYRecyclerAdapter.MODEL_FOOTER);
        mData = fakeData();
        mAdapter.addNewData(mData);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setDYSwipeRefreshLayoutListener(this);

        recyclerView.addOnItemTouchListener(new DYOnRecyclerItemClickListener(recyclerView) {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(LinearLayoutTestActivity.this, "click position:" + position, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onItemLongClick(int position) {

            }
        });
    }

    private List<String> fakeData() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            list.add("I am " + i * offset);
        }
        return list;
    }

    @Override
    public void onRefresh() {
        refreshLayout.setLoadMoreEnable(loadMoreEnable);
        offset = 1;
        mAdapter.clear();
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LinearLayoutTestActivity.this, "刷新完成 offset:" + offset, Toast.LENGTH_LONG).show();
                refreshLayout.onComplete();
                mData = fakeData();
                mAdapter.setStatus(DYRecyclerAdapter.STATUS_LOAD_MORE, false);
                mAdapter.addNewData(mData);
                offset++;

            }
        }, 2000);
    }


    @Override
    public void onLoadMore() {
        mAdapter.setStatus(DYRecyclerAdapter.STATUS_LOADING, true);
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.onComplete();
                Toast.makeText(LinearLayoutTestActivity.this, "加载完成 offset:" + offset, Toast.LENGTH_LONG).show();
                List<String> list = fakeData();
                mData.addAll(list);
                if (offset == 3) {
                    mAdapter.setStatus(DYRecyclerAdapter.STATUS_NO_MORE, true);
                    refreshLayout.setLoadMoreEnable(false);
                }
                mAdapter.addMoreData(list);
                offset++;
            }
        }, 2000);
    }
}
