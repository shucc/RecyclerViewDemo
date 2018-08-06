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
import cchao.org.recyclerapplication.ui.adapter.AddRemoveFooterAdapter;

/**
 * Created by chenchao on 16/9/6.
 * cc@cchao.org
 */
public class AddRemoveFooterActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button addBtn;
    private Button removeBtn;

    private List<String> data;

    private AddRemoveFooterAdapter adapter;

    private View footerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_footer);

        bindView();
        initData();
        bindEvent();
    }

    private void bindView() {
        recyclerView =  findViewById(R.id.recyclerview);
        addBtn = findViewById(R.id.add_footView);
        removeBtn = findViewById(R.id.remove_footView);
    }

    private void initData() {
        data = new ArrayList<>();
        footerView = getLayoutInflater().inflate(R.layout.footer_view, null);
        for (int i = 0; i < 10; i++) {
            data.add("Add Footer" + i);
        }
        showData();
    }

    private void bindEvent() {
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter != null) {
                    adapter.addFooterView(footerView);
                }
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter != null) {
                    adapter.removeFooterView();
                }
            }
        });
    }

    private void showData() {
        if (adapter == null) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new AddRemoveFooterAdapter(data);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Toast.makeText(AddRemoveFooterActivity.this, "我是点击君" + data.get(position), Toast.LENGTH_SHORT).show();
                }
            });
        }
        adapter.reset();
    }
}
