package cchao.org.recyclerapplication.ui.activity;

/**
 * Created by chenchao on 15/11/24.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.adapter.RecyclerAdapter;
import cchao.org.recyclerapplication.decoration.DividerItemDecoration;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;
import cchao.org.recyclerapplication.listener.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity implements Handler.Callback, SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;

    private RecyclerAdapter mAdapter;

    private ArrayList<String> dataset;

    private LinearLayoutManager mLinearLayoutManager;

    private Handler mHandler;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    //添加底部item的id
    private int footItemNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swiperefresh_one);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mHandler = new Handler(this);
        initRecycler();
    }

    private void initRecycler() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        dataset = new ArrayList<String>();
        for (int i = 0; i < 50; i++){
            dataset.add(i, "Text" + i);
        }

        mAdapter = new RecyclerAdapter(mRecyclerView);
        mAdapter.setData(dataset);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this
                , mRecyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, "OnItem" + String.valueOf(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(MainActivity.this, "OnItemLong" + String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        }));
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            //上拉自动加载
            @Override
            public void onLoadMore() {
                dataset.add(null);
                mAdapter.notifyItemInserted(dataset.size() - 1);
                footItemNum = dataset.size() - 1;
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

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            dataset.remove(footItemNum);
            mAdapter.notifyItemRemoved(footItemNum + 1);

            mAdapter.notifyDataSetChanged();
            mAdapter.setLoaded();
        } else if (msg.what == 2) {
            mAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
        return false;
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}
