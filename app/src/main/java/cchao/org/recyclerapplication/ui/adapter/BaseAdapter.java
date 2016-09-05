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

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.listener.OnItemClickListener;
import cchao.org.recyclerapplication.listener.OnItemLongClickListener;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;

/**
 * Created by chenchao on 16/2/3.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "LoadMoreAdapter";

    protected static final int LOAD_MORE_ITEM = -100;

    private RecyclerView mRecyclerView;

    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

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

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        setLoadMoreListener();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
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
        if (footViewHolder == null) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view_load_more, parent, false);
            footViewHolder = new FootViewHolder(view);
        }
        return footViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (onItemClickListener != null && !(holder instanceof FootViewHolder)) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(view, pos);
                }
            });
        }
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onItemLongClickListener.onItemLongClick(view, pos);
                    return false;
                }
            });
        }
        if (holder instanceof FootViewHolder && mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                    layoutParams.setFullSpan(true);
                    holder.itemView.setLayoutParams(layoutParams);
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (mRecyclerView == null) {
            mRecyclerView = recyclerView;
        }
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (getItemViewType(position) == LOAD_MORE_ITEM ? gridManager.getSpanCount() : 1);
                }
            });
        }
    }

    public final void reset() {
        if (mRecyclerView != null) {
            //TODO 不加post会出现bug
            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    loading = false;
                    if (loadMorePos != -1) {
                        notifyItemRemoved(loadMorePos);
                    } else {
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public final void setLoadAll(boolean loadAll) {
        if (footViewHolder == null && mRecyclerView != null) {
            View view = LayoutInflater.from(mRecyclerView.getContext()).inflate(R.layout.footer_view_load_more, null);
            footViewHolder = new FootViewHolder(view);
        }
        isLoadAll = loadAll;
        if (isLoadAll && footViewHolder != null) {
            footViewHolder.mLLLoadNow.setVisibility(View.INVISIBLE);
            footViewHolder.mTxtLoadMore.setVisibility(View.VISIBLE);
        } else if (footViewHolder != null) {
            footViewHolder.mLLLoadNow.setVisibility(View.VISIBLE);
            footViewHolder.mTxtLoadMore.setVisibility(View.INVISIBLE);
        }
        if (!isLoadAll) {
            loadMorePos = -1;
        }
    }

    /**
     * 设置加载更多监听
     */
    private void setLoadMoreListener() {
        if (onLoadMoreListener != null) {
            isLoadAll = false;
            loadMorePos = -1;
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && dy > 0 && !isLoadAll) {
                            loading = true;
                            loadMorePos = getItemCount() - 1;
                            mRecyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemInserted(getItemCount() - 1);
                                    onLoadMoreListener.onLoadMore();
                                }
                            });
                        }
                    }
                });
            } else if (layoutManager instanceof StaggeredGridLayoutManager){
                final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            mRecyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyItemInserted(getItemCount() - 1);
                                    onLoadMoreListener.onLoadMore();
                                }
                            });
                        }
                    }
                });
            }
        } else {
            Log.i(TAG, "LoadMoreListener is null!");
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