package cchao.org.recyclerapplication.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.decoration.DividerWaterFallItemDecoration;
import cchao.org.recyclerapplication.listener.OnItemClickListener;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;
import cchao.org.recyclerapplication.ui.adapter.GridAdapter;
import cchao.org.recyclerapplication.widget.MyPtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class CoordinatorActivity extends AppCompatActivity implements Handler.Callback {
    private int mPage = 1;

    private MyPtrClassicFrameLayout mPtrClassicFrame;

    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridManager;
    private GridAdapter mAdapter;

    private List<String> mData;

    private Handler mHandler;

    private AppBarLayout mAppBar;
    private int mOffNum;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                if (mAdapter == null) {
                    mGridManager = new GridLayoutManager(this, 3);
                    mGridManager.setOrientation(GridLayoutManager.VERTICAL);
                    mRecyclerView.setLayoutManager(mGridManager);

                    mAdapter = new GridAdapter(mData, mRecyclerView, new OnLoadMoreListener() {
                        @Override
                        public void onLoadMore() {
                            getData();
                        }
                    });
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.addItemDecoration(new DividerWaterFallItemDecoration(CoordinatorActivity.this));
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    mAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Toast.makeText(CoordinatorActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();

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
        setContentView(R.layout.activity_coordinator);

        setupToolbar();
        setupCollapsingToolbar();
        bindView();
        initData();
        bindEvent();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_coordinator_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CoorDinatorActivity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupCollapsingToolbar() {
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(
                R.id.activity_coordinator_collapsing);
        collapsingToolbar.setTitleEnabled(false);
    }

    private void bindView() {
        mAppBar = (AppBarLayout) findViewById(R.id.activity_coordinator_appbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mPtrClassicFrame = (MyPtrClassicFrameLayout) findViewById(R.id.ptrclassicframe);
    }

    private void initData() {
        mHandler = new Handler(this);
        mData = new ArrayList<String>();
    }

    private void bindEvent() {
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mOffNum = verticalOffset;
            }
        });
        mPtrClassicFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return mOffNum == 0 && PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
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
            if (mAdapter != null) {
                mAdapter.setLoadAll(false);
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20; i++) {
                    mData.add("Text" + i);
                }
                mHandler.sendEmptyMessage(1);
            }
        }, 3000);
    }
}
