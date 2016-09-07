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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by chenchao on 16/2/3.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "LoadMoreAdapter";

    private final int LOAD_MORE_ITEM = -100;
    private final int FOOTER_VIEW_ITEM = -200;
    private final int HEADER_VIEW_ITEM = -300;

    private RecyclerView mRecyclerView;

    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    //是否显示加载中
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

    private View headerView = null;
    private View footerView = null;

    //加载更多
    private FootViewHolder footViewHolder;

    private LinearLayout footerLayout;
    private LinearLayout headerLayout;

    /**
     * 设置加载更多回调
     * @param onLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
        setLoadMoreListener();
    }

    /**
     * 设置点击事件
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置长按事件
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 添加底部
     * @param view
     */
    public void addFooterView(View view) {
        Log.i(TAG, "addFooterView");
        if (view == null) {
            throw new NullPointerException("FooterView is null!");
        }
        if (footerView != null) {
            return;
        }
        footerView = view;
        if (footerLayout == null && mRecyclerView != null) {
            footerLayout = new LinearLayout(mRecyclerView.getContext());
            footerLayout.setOrientation(LinearLayout.VERTICAL);
            footerLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        }
        footerLayout.removeAllViews();
        footerLayout.addView(view);
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * 移除底部View
     */
    public void removeFooterView() {
        if (footerView != null) {
            footerLayout.removeAllViews();
            footerView = null;
            notifyItemRemoved(getItemCount());
        }
    }

    /**
     * 添加头部
     * @param view
     */
    public void addHeaderView(View view) {
        headerView = view;
    }

    @Override
    public int getItemViewType(int position) {
        Log.i(TAG, "getItemViewType: " + position);
        if (position >= getCount() && loading) {
            return LOAD_MORE_ITEM;
        }
        if (position >= getCount() && footerView != null) {
            return FOOTER_VIEW_ITEM;
        }
        return super.getItemViewType(position);
    }

    @Override
    public final int getItemCount() {
        if (loading) {
            return getCount() + 1;
        }
        if (footerView != null) {
            return getCount() + 1;
        }
        return getCount();
    }

    public abstract int getCount();

    protected abstract RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType);

    protected abstract void onBindView(RecyclerView.ViewHolder holder, int position);

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: " + viewType);
        if (viewType == LOAD_MORE_ITEM) {
            if (footViewHolder == null) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view_load_more, parent, false);
                footViewHolder = new FootViewHolder(view);
            }
            return footViewHolder;
        } else if (viewType == FOOTER_VIEW_ITEM) {
            return new BaseHolder(footerLayout);
        } else {
            return onCreateView(parent, viewType);
        }
    }

    @Override
    public final void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (onItemClickListener != null && !(holder instanceof FootViewHolder)) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(view, pos);
                }
            });
        }
        if (onItemLongClickListener != null && !(holder instanceof FootViewHolder)) {
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
        if (!(holder instanceof BaseHolder)) {
            onBindView(holder, position);
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
            //必须主线程notify
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

    /**
     * 设置是否已经全部加载完成
     * @param loadAll
     */
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

    private class FootViewHolder extends BaseHolder {

        private LinearLayout mLLLoadNow;
        private TextView mTxtLoadMore;

        public FootViewHolder(View itemView) {
            super(itemView);

            mLLLoadNow = (LinearLayout) itemView.findViewById(R.id.footer_view_load_now);
            mTxtLoadMore = (TextView) itemView.findViewById(R.id.footer_view_load_all);
        }
    }

    private class BaseHolder extends RecyclerView.ViewHolder {

        public BaseHolder(View itemView) {
            super(itemView);
        }

    }
}