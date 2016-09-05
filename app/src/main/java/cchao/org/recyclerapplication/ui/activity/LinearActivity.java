package cchao.org.recyclerapplication.ui.activity;

/**
 * Created by chenchao on 15/11/24.
 */

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.listener.OnItemClickListener;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;
import cchao.org.recyclerapplication.ui.adapter.LinearAdapter;
import cchao.org.recyclerapplication.widget.MyPtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class LinearActivity extends Activity {

    private int mPage = 1;

    private MyPtrClassicFrameLayout mPtrClassicFrame;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearManager;
    private LinearAdapter mAdapter;

    private List<String> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        bindView();
        initData();
        bindEvent();
    }

    private void bindView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mPtrClassicFrame = (MyPtrClassicFrameLayout) findViewById(R.id.ptrclassicframe);
    }

    private void initData() {
        mData = new ArrayList<String>();
        showData();
    }

    private void bindEvent() {
        mPtrClassicFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPage = 1;
                if (mAdapter != null) {
                    mAdapter.setLoadAll(false);
                }
                getData();
            }
        });
        mPtrClassicFrame.setLastUpdateTimeRelateObject(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mPtrClassicFrame.autoRefresh();
        }
    }

    private void getData() {
        if (mPage == 1) {
            mData.clear();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPage == 5 && mAdapter != null) {
                    mAdapter.setLoadAll(true);
                } else {
                    int size = 0;
                    if (mData != null) {
                        size = mData.size();
                    }
                    for (int i = size; i < size + 20; i++) {
                        mData.add("Text" + i);
                    }
                    showData();
                }
            }
        }, 1000);
    }

    private void showData() {
        if (mAdapter == null) {

            mLinearManager = new LinearLayoutManager(LinearActivity.this);
            mLinearManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLinearManager);
            mAdapter = new LinearAdapter(mData);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    mPage++;
                    getData();
                }
            });
            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(LinearActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();

                }
            });
        }
        mAdapter.reset();
        if (mPtrClassicFrame.isShown()) {
            mPtrClassicFrame.refreshComplete();
        }
    }
}
