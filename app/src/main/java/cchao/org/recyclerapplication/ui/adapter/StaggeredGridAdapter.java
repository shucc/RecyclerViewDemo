package cchao.org.recyclerapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cchao.org.baseadapterlibrary.BaseAdapter;
import cchao.org.baseadapterlibrary.BaseHolder;
import cchao.org.recyclerapplication.R;

/**
 * Created by chenchao on 16/2/3.
 */
public class StaggeredGridAdapter extends BaseAdapter<StaggeredGridAdapter.NormalViewHolder> {

    //瀑布流的高度
    private List<Integer> heights;
    private List<String> data;

    public StaggeredGridAdapter(List<String> data) {
        this.data = data;
        //随机高度的初始化
        heights = new ArrayList<Integer>();
        for (int i = 0; i < data.size(); i++) {
            heights.add((int)(300 + Math.random() * 300));
        }
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
        ViewGroup.LayoutParams lp =  holder.mTextView.getLayoutParams();
        lp.height = heights.get(position);
        holder.mTextView.setLayoutParams(lp);
        holder.mTextView.setText(data.get(position));
    }

    /**
     * 添加数据时同时添加一个随机高度
     */
    public void addHeight() {
        heights.add((int) (300 + Math.random() * 300));
    }

    /**
     * 删除数据时移除对应高度
     * @param location  移除的数据下标
     */
    public void deleteHeight(int location) {
        heights.remove(location);
    }

    /**
     * 数据全部刷新时clear高度
     */
    public void clearHeight() {
        if (!heights.isEmpty()) {
            heights.clear();
        }
    }

    protected class NormalViewHolder extends BaseHolder {

        public TextView mTextView;

        public NormalViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.recyclerview_text);
        }
    }
}
