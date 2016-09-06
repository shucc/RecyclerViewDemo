package cchao.org.recyclerapplication.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cchao.org.recyclerapplication.R;

/**
 * Created by chenchao on 16/2/3.
 */
public class WaterFallAdapter extends BaseAdapter {

    //瀑布流的高度
    private List<Integer> mHeight;
    private List<String> mData;

    public WaterFallAdapter(List<String> data) {
        this.mData = data;
        //随机高度的初始化
        mHeight = new ArrayList<Integer>();
        for (int i = 0; i < mData.size(); i++) {
            mHeight.add((int)(300 + Math.random() * 300));
        }
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
        if (holder instanceof NormalViewHolder) {
            ViewGroup.LayoutParams lp = ((NormalViewHolder) holder).mTextView.getLayoutParams();
            lp.height = mHeight.get(position);
            ((NormalViewHolder) holder).mTextView.setLayoutParams(lp);

            ((NormalViewHolder)holder).mTextView.setText(mData.get(position));
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
            mTextView = (TextView) itemView.findViewById(R.id.recyclerview_text);
        }
    }
}
