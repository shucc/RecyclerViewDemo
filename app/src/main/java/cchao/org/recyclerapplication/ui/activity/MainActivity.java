package cchao.org.recyclerapplication.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cchao.org.recyclerapplication.R;

/**
 * Created by chenchao on 15/12/18.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setClick(R.id.activity_main_linear, LinearActivity.class);
        setClick(R.id.activity_main_grid, GridActivity.class);
        setClick(R.id.activity_main_waterfall, WaterFallActivity.class);
        setClick(R.id.activity_main_coordinator, CoordinatorActivity.class);
        setClick(R.id.activity_main_add_footerView, AddRemoveFooterActivity.class);
        setClick(R.id.activity_main_add_headerView, AddRemoveHeaderActivity.class);
    }

    private void setClick(int id, final Class<?> cls) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, cls));
            }
        });
    }

}
