package cchao.org.recyclerapplication.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cchao.org.baseadapterlibrary.BaseAdapter;
import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.ui.adapter.StaggeredGridAdapter;
import cchao.org.recyclerapplication.widget.MyPtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class StaggeredGridActivity extends Activity {

    private int pageNum = 1;

    private MyPtrClassicFrameLayout ptrLayout;

    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggerManager;
    private StaggeredGridAdapter adapter;

    private List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        bindView();
        initData();
        bindEvent();
    }

    private void bindView() {
        recyclerView = findViewById(R.id.recyclerview);
        ptrLayout = findViewById(R.id.ptrclassicframe);
    }

    private void initData() {
        data = new ArrayList<String>();
        showData();
    }

    private void bindEvent() {
        ptrLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                pageNum = 1;
                getData();
            }
        });
        ptrLayout.setLastUpdateTimeRelateObject(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ptrLayout.autoRefresh();
        }
    }

    private void getData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pageNum == 1) {
                    data.clear();
                    if (adapter != null) {
                        adapter.clearHeight();
                        adapter.setLoadAll(false);
                    }
                }
                int size = 0;
                if (data != null) {
                    size = data.size();
                }
                for (int i = size; i < size + 20; i++) {
                    data.add("Text" + i);
                    if (adapter != null) {
                        adapter.addHeight();
                    }
                }
                showData();
            }
        }, 2000);
    }

    private void showData() {
        if (adapter == null) {

            staggerManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(staggerManager);
            adapter = new StaggeredGridAdapter(data);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            View loadView = getLayoutInflater().inflate(R.layout.load_more_default, null);
            adapter.addLoadingView(loadView);
            adapter.setOnLoadMoreListener(new BaseAdapter.OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    pageNum++;
                    getData();
                }
            });
            adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(StaggeredGridActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
        adapter.reset();
        if (ptrLayout.isShown()) {
            ptrLayout.refreshComplete();
        }
    }
}
