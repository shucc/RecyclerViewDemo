package cchao.org.recyclerapplication.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;

/**
 * Created by chenchao on 16/2/3.
 */
public class LinearAdapter extends LoadMoreAdapter {

    private List<String> mData;

    public LinearAdapter(List<String> data, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        super(recyclerView, onLoadMoreListener);
        this.mData = data;
    }

    @Override
    int getDataSize() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOAD_MORE_ITEM) {
            return super.onCreateViewHolder(parent, viewType);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
            return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalViewHolder) {
            ((NormalViewHolder)holder).mTextView.setText(mData.get(position));
            if (onItemClickListener != null) {
                ((NormalViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        onItemClickListener.onItemClick(v, pos);
                    }
                });
            }
        }
    }

    private class NormalViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextView;

        public NormalViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.recyclerview_text);
        }
    }
}
