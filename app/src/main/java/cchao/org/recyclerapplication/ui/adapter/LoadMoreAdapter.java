package cchao.org.recyclerapplication.ui.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.listener.OnItemClickListener;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;

/**
 * Created by chenchao on 16/2/3.
 */
public abstract class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int LOAD_MORE_ITEM = -1;

    private RecyclerView mRecyclerView;

    private OnLoadMoreListener onLoadMoreListener;
    protected OnItemClickListener onItemClickListener;

    private boolean loading = true;

    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;

    //最后一个的位置
    private int[] lastPositions;
    //最后一个可见的item的位置
    private int lastVisibleItemPosition;

    //正在加载中item position
    private int loadMorePos = -1;

    //是否全部加载完成
    private boolean isLoadAll = false;

    private FootViewHolder footViewHolder;

    public LoadMoreAdapter(RecyclerView recyclerView, OnLoadMoreListener onloadMoreListener) {
        this.mRecyclerView = recyclerView;
        this.onLoadMoreListener = onloadMoreListener;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && dy > 0 && !isLoadAll) {
                        loading = true;
                        loadMorePos = getItemCount() - 1;
                        notifyItemInserted(getItemCount() - 1);
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }
            });
        } else if (layoutManager instanceof StaggeredGridLayoutManager){
            final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (lastPositions == null) {
                        lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                    }
                    staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                    lastVisibleItemPosition = findMax(lastPositions);
                    if (!loading && staggeredGridLayoutManager.getItemCount() <= lastVisibleItemPosition + 1 && dy > 0 && !isLoadAll) {
                        loading = true;
                        loadMorePos = getItemCount() - 1;
                        notifyItemInserted(getItemCount() - 1);
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public final void reset() {
        loading = false;
        if (loadMorePos != -1) {
            notifyItemRemoved(loadMorePos);
        }
    }

    public final void setLoadAll(boolean loadAll) {
        isLoadAll = loadAll;
        if (isLoadAll && footViewHolder != null) {
            footViewHolder.mLLLoadNow.setVisibility(View.GONE);
            footViewHolder.mTxtLoadMore.setVisibility(View.VISIBLE);
        } else if (footViewHolder != null) {
            footViewHolder.mLLLoadNow.setVisibility(View.VISIBLE);
            footViewHolder.mTxtLoadMore.setVisibility(View.GONE);
        }
        if (!isLoadAll) {
            loadMorePos = -1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= getDataSize()) {
            return LOAD_MORE_ITEM;
        }
        return super.getItemViewType(position);
    }

    @Override
    public final int getItemCount() {
        if (loading)
           return getDataSize() + 1;
        return getDataSize();
    }

    abstract int getDataSize();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view_load_more, parent, false);
        footViewHolder = new FootViewHolder(view);
        return footViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.getItemViewType() == LOAD_MORE_ITEM
                && mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager
                    .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(true);
            holder.itemView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (getItemViewType(position) == RecyclerView.INVALID_TYPE || getItemViewType(position) == RecyclerView.INVALID_TYPE - 1)
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    protected class FootViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLLLoadNow;
        private TextView mTxtLoadMore;

        public FootViewHolder(View itemView) {
            super(itemView);

            mLLLoadNow = (LinearLayout) itemView.findViewById(R.id.footer_view_load_now);
            mTxtLoadMore = (TextView) itemView.findViewById(R.id.footer_view_load_all);
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}