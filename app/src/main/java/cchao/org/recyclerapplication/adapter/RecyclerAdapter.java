package cchao.org.recyclerapplication.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;

/**
 * Created by chenchao on 15/11/24.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOT = 2;

    //数据集
    private ArrayList<String> mDataset;
    //底部itemNum
    private int footItemNum = 0;

    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener mOnLoadMoreListener;

    public RecyclerAdapter(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mDataset.add(null);
                            notifyItemInserted(mDataset.size() - 1);
                            footItemNum = mDataset.size() - 1;
                            mOnLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public void setLoaded() {
        mDataset.remove(footItemNum);
        notifyItemRemoved(footItemNum + 1);
        notifyDataSetChanged();
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener ) {
        this.mOnLoadMoreListener = mOnLoadMoreListener ;
    }

    public void setData(ArrayList<String> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? TYPE_ITEM : TYPE_FOOT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = null;
        if (i == TYPE_ITEM) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item, viewGroup, false);
            NormalViewHolder holder = new NormalViewHolder(view);
            return holder;
        } else if (i == TYPE_FOOT) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.footer_view_load_more, viewGroup, false);
            FootViewHolder footViewHolder = new FootViewHolder(view);
            return  footViewHolder;
        }
        throw new RuntimeException("Could not inflate layout!");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof NormalViewHolder) {
            ((NormalViewHolder) viewHolder).mTextView.setText(mDataset.get(i));
        } else if (viewHolder instanceof FootViewHolder){
            ((FootViewHolder) viewHolder).loadText.setText("Loading...");
            ((FootViewHolder) viewHolder).progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    //加载底部ViewHolder
    private class FootViewHolder extends RecyclerView.ViewHolder {

        public TextView loadText;

        public ProgressBar progressBar;

        public FootViewHolder(View itemView) {
            super(itemView);
            loadText = (TextView) itemView.findViewById(R.id.tv_text);
            progressBar = (ProgressBar) itemView.findViewById(R.id.pb_progressBar);
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
