package cchao.org.recyclerapplication.ui.activity;

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
import cchao.org.recyclerapplication.widget.CustomScrollView;

/**
 * Created by shucc on 17/8/30.
 * cc@cchao.org
 */
public class AutoScrollActivity extends Activity {

    private CustomScrollView nestedScrollView;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearManager;
    private LinearAdapter adapter;

    private List<String> data;

    private int pageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_scroll);

        bindView();
        initData();
        bindEvent();
    }

    private void bindView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        nestedScrollView = (CustomScrollView) findViewById(R.id.scrollView);
    }

    private void initData() {
        data = new ArrayList<>();
        showData();
        View headView = getLayoutInflater().inflate(R.layout.header_view, null);
        adapter.addHeaderView(headView);
        View loadView = getLayoutInflater().inflate(R.layout.load_more_custom, null);
        adapter.addLoadingView(loadView);
        getData();
    }

    private void bindEvent() {
        findViewById(R.id.auto_scroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nestedScrollView.startScroll(0, 0, 0, recyclerView.getMeasuredHeight(), 60 * 1000);
            }
        });
    }

    private void getData() {
        if (pageNum == 1) {
            data.clear();
        }
        //全部加载完成
        if (pageNum == 4) {
            adapter.setLoadAll(true);
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int size = 0;
                if (data != null) {
                    size = data.size();
                }
                for (int i = size; i < size + 30; i++) {
                    data.add("Text" + i);
                }
                showData();
            }
        }, 1000);
    }

    private void showData() {
        if (adapter == null) {

            linearManager = new LinearLayoutManager(AutoScrollActivity.this);
            linearManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearManager);
            recyclerView.setNestedScrollingEnabled(false);
            adapter = new LinearAdapter(data);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

//            adapter.setOnLoadMoreListener(new BaseAdapter.OnLoadMoreListener() {
//                @Override
//                public void onLoadMore() {
//                    pageNum++;
//                    getData();
//                }
//            });
            adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(AutoScrollActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();

                }
            });
        }
        adapter.reset();
    }
}
