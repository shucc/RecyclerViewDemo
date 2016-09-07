package cchao.org.recyclerapplication.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cchao.org.recyclerapplication.R;

/**
 * Created by chenchao on 16/2/3.
 */
public class LinearAdapter extends BaseAdapter {

    private List<String> mData;

    public LinearAdapter(List<String> data) {
        this.mData = data;
    }

    @Override
    public int getCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new NormalViewHolder(view);
    }

    @Override
    public void onBindView(final RecyclerView.ViewHolder holder, int position) {
        ((NormalViewHolder)holder).mTextView.setText(mData.get(position));
    }

    private class NormalViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextView;

        public NormalViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.recyclerview_text);
        }
    }
}
