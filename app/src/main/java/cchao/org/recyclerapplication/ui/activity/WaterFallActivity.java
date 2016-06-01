package cchao.org.recyclerapplication.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.decoration.DividerWaterFallItemDecoration;
import cchao.org.recyclerapplication.listener.OnItemClickListener;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;
import cchao.org.recyclerapplication.ui.adapter.WaterFallAdapter;
import cchao.org.recyclerapplication.widget.MyPtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class WaterFallActivity extends Activity implements Handler.Callback {

    private int mPage = 1;

    private MyPtrClassicFrameLayout mPtrClassicFrame;

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggerManager;
    private WaterFallAdapter mAdapter;

    private List<String> mData;

    private Handler mHandler;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                if (mAdapter == null) {
                    mStaggerManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(mStaggerManager);

                    mAdapter = new WaterFallAdapter(mData, mRecyclerView, new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            getData();
                        }
                    });
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.addItemDecoration(new DividerWaterFallItemDecoration(WaterFallActivity.this));
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    mAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Toast.makeText(WaterFallActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                mAdapter.reset();
                if (mPtrClassicFrame.isShown()) {
                    mPtrClassicFrame.refreshComplete();
                }
                mPage++;
                break;
            default:
                break;
        }
        return false;
    }

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
        mHandler = new Handler(this);
        mData = new ArrayList<String>();
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
            if (!mData.isEmpty()) {
                mAdapter.clearHeight();
            }
            if (mAdapter != null) {
                mAdapter.setLoadAll(false);
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    mData.add("Text" + i);
                    if (mAdapter != null) {
                        mAdapter.addHeight();
                    }
                }
                mHandler.sendEmptyMessage(1);
            }
        }, 3000);
    }

}
