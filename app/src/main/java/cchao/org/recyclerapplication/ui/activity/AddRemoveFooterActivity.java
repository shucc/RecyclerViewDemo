package cchao.org.recyclerapplication.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.listener.OnItemClickListener;
import cchao.org.recyclerapplication.ui.adapter.AddRemoveFooterAdapter;

/**
 * Created by chenchao on 16/9/6.
 * cc@cchao.org
 */
public class AddRemoveFooterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button addBtn;
    private Button removeBtn;

    private List<String> mData;

    private AddRemoveFooterAdapter mAdapter;

    private View mFooterView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_footer);

        bindView();
        initData();
        bindEvent();
    }

    private void bindView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        addBtn = (Button) findViewById(R.id.add_footView);
        removeBtn = (Button) findViewById(R.id.remove_footView);
    }

    private void initData() {
        mData = new ArrayList<>();
        mFooterView = getLayoutInflater().inflate(R.layout.footer_view, null);
        for (int i = 0; i < 40; i++) {
            mData.add("Add Footer" + i);
        }
        showData();
    }

    private void bindEvent() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter != null) {
                    mAdapter.addFooterView(mFooterView);
                }
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter != null) {
                    mAdapter.removeFooterView();
                }
            }
        });
    }

    private void showData() {
        if (mAdapter == null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            mAdapter = new AddRemoveFooterAdapter(mData);
            recyclerView.setAdapter(mAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(AddRemoveFooterActivity.this, "我是点击君" + mData.get(position), Toast.LENGTH_SHORT).show();
                }
            });
        }
        mAdapter.reset();
    }
}
