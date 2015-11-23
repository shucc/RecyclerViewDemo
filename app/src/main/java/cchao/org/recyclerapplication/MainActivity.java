package cchao.org.recyclerapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Handler.Callback{

    private RecyclerView mRecyclerView;

    private RecyclerAdapter mAdapter;

    private ArrayList<String> dataset;

    private LinearLayoutManager mLinearLayoutManager;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
         @Override
         public void onLoadMore() {
             dataset.add(null);
             mAdapter.notifyItemInserted(dataset.size() - 1);
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         Thread.sleep(3000);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                     mHandler.sendEmptyMessage(1);
                 }
             }).start();
         }
        });
    }

    public boolean handleMessage(Message msg) {
        if (msg.what == 1) {
            dataset.remove(dataset.size() - 1);
            mAdapter.notifyItemRemoved(dataset.size());
            int start = dataset.size();
            int end = start + 10;

            for (int i = start + 1; i <= end; i++) {
                dataset.add("Text" + String.valueOf(i - 1));
                mAdapter.notifyItemInserted(dataset.size());
            }
            mAdapter.setLoaded();
        }
        return false;
    }
}
