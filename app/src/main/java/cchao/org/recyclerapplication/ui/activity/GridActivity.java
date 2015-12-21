package cchao.org.recyclerapplication.ui.activity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import cchao.org.recyclerapplication.adapter.RecyclerAdapter;
import cchao.org.recyclerapplication.decoration.DividerGridItemDecoration;
import cchao.org.recyclerapplication.listener.OnItemClickListener;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;

public class GridActivity extends BaseActivity {

    @Override
    protected void initRecycler() {
        StaggeredGridLayoutManager mLinearLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new RecyclerAdapter(mRecyclerView, dataset, RecyclerAdapter.GRID_RECYCLER);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        mAdapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Toast.makeText(GridActivity.this, "［Grid布局］点击了" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(GridActivity.this, "[Grid布局］长按了" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            //上拉自动加载
            @Override
            public void onLoadMore() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            int start = dataset.size();
                            int end = start + 10;

                            for (int i = start + 1; i <= end; i++) {
                                dataset.add("Text" + String.valueOf(i - 2));
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }).start();
            }
        });
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    dataset.clear();
                    for (int i = 0; i < 50; i++){
                        dataset.add(i, "ChangeText" + i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(2);
            }
        }).start();
    }
}
