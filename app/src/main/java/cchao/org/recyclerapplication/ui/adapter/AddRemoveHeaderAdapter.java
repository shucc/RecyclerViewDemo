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
 * Created by chenchao on 16/9/7.
 * cc@cchao.org
 */
public class AddRemoveHeaderAdapter extends BaseAdapter<AddRemoveHeaderAdapter.NormalViewHolder> {

    private List<String> data;

    public AddRemoveHeaderAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        if (data == null)
            return 0;
        return data.size();
    }

    @Override
    protected BaseHolder onCreateView(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_default, parent, false);
        return new NormalViewHolder(view);
    }

    @Override
    protected void onBindView(NormalViewHolder holder, int position) {
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
