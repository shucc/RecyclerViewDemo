package cchao.org.recyclerapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cchao.org.baseadapterlibrary.BaseAdapter;
import cchao.org.baseadapterlibrary.BaseHolder;
import cchao.org.recyclerapplication.R;

/**
 * Created by chenchao on 16/2/3.
 */
public class GridAdapter extends BaseAdapter<GridAdapter.NormalViewHolder> {

    private List<String> data;

    public GridAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        if (data == null)
            return 0;
        return data.size();
    }

    @Override
    public BaseHolder onCreateView(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_default, parent, false);
        return new NormalViewHolder(view);
    }

    @Override
    public void onBindView(NormalViewHolder holder, int position) {
        holder.mTextView.setText(data.get(position));
    }

    protected class NormalViewHolder extends BaseHolder {

        public TextView mTextView;

        public NormalViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.recyclerview_text);
        }
    }
}
