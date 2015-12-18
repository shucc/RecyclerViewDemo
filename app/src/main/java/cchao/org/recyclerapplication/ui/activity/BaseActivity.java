package cchao.org.recyclerapplication.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.adapter.RecyclerAdapter;

/**
 * Created by chenchao on 15/12/18.
 */
public abstract class BaseActivity extends AppCompatActivity implements Handler.Callback, SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView mRecyclerView;

    public RecyclerAdapter mAdapter;

    public ArrayList<String> dataset;

    public Handler mHandler;

    public SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swiperefresh_one);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mHandler = new Handler(this);
        initData();
        initRecycler();
    }

    protected abstract void initRecycler();

    private void initData() {
        dataset = new ArrayList<String>();
        for (int i = 0; i < 50; i++){
            dataset.add(i, "Text" + i);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            mAdapter.setLoaded();
        } else if (msg.what == 2) {
            mAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        } else if (msg.what == 3) {
            mAdapter.setLoaded();
            Toast.makeText(this, "所有数据已加载完!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
