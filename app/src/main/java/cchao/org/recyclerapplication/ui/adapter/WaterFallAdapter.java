package cchao.org.recyclerapplication.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;

/**
 * Created by chenchao on 16/2/3.
 */
public class WaterFallAdapter extends LoadMoreAdapter<String> {

    //瀑布流的高度
    private List<Integer> mHeight;

    public WaterFallAdapter(List<String> data, RecyclerView recyclerView, OnLoadMoreListener onLoadMoreListener) {
        super(data, recyclerView, onLoadMoreListener);
        //随机高度的初始化
        mHeight = new ArrayList<Integer>();
        for (int i = 0; i < mData.size(); i++) {
            mHeight.add((int)(300 + Math.random() * 300));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LOAD_MORE_ITEM) {
            return super.onCreateViewHolder(parent, viewType);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waterfall_recyclerview_item, parent, false);
            return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalViewHolder) {

            ViewGroup.LayoutParams lp = ((NormalViewHolder) holder).mTextView.getLayoutParams();
            lp.height = mHeight.get(position);
            ((NormalViewHolder) holder).mTextView.setLayoutParams(lp);

            ((NormalViewHolder)holder).mTextView.setText(mData.get(position));
            if (onItemClickListener != null) {
                ((NormalViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        onItemClickListener.OnItemClick(v, pos);
                    }
                });
                ((NormalViewHolder)holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getLayoutPosition();
                        onItemClickListener.onItemLongClick(v, pos);
                        return false;
                    }
                });
            }
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    /**
     * 添加数据时同时添加一个随机高度
     */
    public void addHeight() {
        mHeight.add((int) (300 + Math.random() * 300));
    }

    /**
     * 删除数据时移除对应高度
     * @param location  移除的数据下标
     */
    public void deleteHeight(int location) {
        mHeight.remove(location);
    }

    /**
     * 数据全部刷新时clear高度
     */
    public void clearHeight() {
        if (!mHeight.isEmpty()) {
            mHeight.clear();
        }
    }

    private class NormalViewHolder extends RecyclerView.ViewHolder{

        public TextView mTextView;

        public NormalViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.waterfall_recyclerview_text);
        }
    }
}
