package cchao.org.recyclerapplication.listener;

import android.view.View;

/**
 * Created by chenchao on 15/12/18.
 */
public interface OnItemClickListener {

    void OnItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
