package cchao.org.recyclerapplication.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cchao.org.baseadapterlibrary.BaseAdapter;
import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.ui.adapter.AddRemoveHeaderAdapter;

/**
 * Created by chenchao on 16/9/7.
 * cc@cchao.org
 */
public class AddRemoveHeaderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button addBtn;
    private Button removeBtn;

    private List<String> data;

    private AddRemoveHeaderAdapter adapter;

    private View headerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_header);

        bindView();
        initData();
        bindEvent();
    }

    private void bindView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        addBtn = (Button) findViewById(R.id.add_headView);
        removeBtn = (Button) findViewById(R.id.remove_headView);
    }

    private void initData() {
        data = new ArrayList<>();
        headerView = getLayoutInflater().inflate(R.layout.header_view, null);
        for (int i = 0; i < 40; i++) {
            data.add("Test" + i);
        }
        showData();
    }

    private void bindEvent() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter != null) {
                    adapter.addHeaderView(headerView);
                }
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter != null) {
                    adapter.removeHeaderView();
                }
            }
        });
    }

    private void showData() {
        if (adapter == null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new AddRemoveHeaderAdapter(data);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(AddRemoveHeaderActivity.this, "我是点击君" + data.get(position), Toast.LENGTH_SHORT).show();
                }
            });
        }
        adapter.reset();
    }
}
