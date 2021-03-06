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

import cchao.org.baseadapterlibrary.BaseAdapter;
import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.ui.adapter.LinearAdapter;
import cchao.org.recyclerapplication.widget.MyPtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class LinearActivity extends Activity {

    private int pageNum = 1;

    private MyPtrClassicFrameLayout ptrLayout;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearManager;
    private LinearAdapter adapter;

    private List<String> data;

    private View loadMoreComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);

        bindView();
        initData();
        bindEvent();
    }

    private void bindView() {
        recyclerView =  findViewById(R.id.recyclerview);
        ptrLayout =  findViewById(R.id.ptrclassicframe);
    }

    private void initData() {
        data = new ArrayList<String>();
        showData();
        View headView = getLayoutInflater().inflate(R.layout.header_view, null);
        adapter.addHeaderView(headView);
        View loadView = getLayoutInflater().inflate(R.layout.load_more_custom, null);
        adapter.addLoadingView(loadView);
        loadMoreComplete = getLayoutInflater().inflate(R.layout.load_more_complete, null);
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
                if (adapter != null) {
                    adapter.setLoadAll(false);
                }
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
        //全部加载完成
        if (pageNum == 4) {
            adapter.setLoadAll(true);
            adapter.addFooterView(loadMoreComplete);
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pageNum == 1) {
                    data.clear();
                }
                int size = 0;
                if (data != null) {
                    size = data.size();
                }
                for (int i = size; i < size + 20; i++) {
                    data.add("Text" + i);
                }
                showData();
            }
        }, 1000);
    }

    private void showData() {
        if (adapter == null) {
            linearManager = new LinearLayoutManager(LinearActivity.this);
            linearManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearManager);
            adapter = new LinearAdapter(data);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

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
                    Toast.makeText(LinearActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();

                }
            });
        }
        adapter.reset();
        if (ptrLayout.isShown()) {
            ptrLayout.refreshComplete();
        }
    }
}
