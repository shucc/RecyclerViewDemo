package cchao.org.recyclerapplication.ui.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by chenchao on 16/2/3.
 */
public abstract class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "LoadMoreAdapter";

    private final int TYPE_LOAD_MORE = -100;
    private final int TYPE_FOOTER = -200;
    private final int TYPE_HEADER = -300;
    private final int TYPE_DEFAULT = -400;

    private RecyclerView recyclerView;

    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    //1表示表示滑动至最后一个开始加在更多
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;

    //最后一个的位置
    private int[] lastPositions;
    //最后一个可见的item的位置
    private int lastVisibleItemPosition;

    //是否显示加载中
    private boolean loading = false;

    //是否全部加载完成
    private boolean isLoadAll = false;

    private View headerView = null;
    private View footerView = null;
    private View loadView = null;

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
     * 设置提前加载
     * @param num
     */
    public void setVisibleThreshold(int num) {
        visibleThreshold = num;
        if (recyclerView != null) {
            //网格或瀑布布局特殊处理
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                visibleThreshold = visibleThreshold * ((GridLayoutManager) manager).getSpanCount();
            }
            if (manager instanceof StaggeredGridLayoutManager) {
                visibleThreshold = visibleThreshold * ((StaggeredGridLayoutManager) manager).getSpanCount();
            }
        }
    }

    /**
     * 添加底部
     * @param view
     */
    public void addFooterView(View view) {
        if (view == null) {
            throw new NullPointerException("FooterView is null!");
        }
        if (footerView != null) {
            return;
        }
        footerView = view;
        footerView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * 移除底部View
     */
    public void removeFooterView() {
        if (footerView != null) {
            footerView = null;
            notifyItemRemoved(getItemCount());
        }
    }

    /**
     * 添加头部
     * @param view
     */
    public void addHeaderView(View view) {
        if (view == null) {
            throw new NullPointerException("HeadView is null");
        }
        if (headerView != null) {
            return;
        }
        headerView = view;
        headerView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        notifyDataSetChanged();
    }

    public void removeHeaderView() {
        if (headerView != null) {
            headerView = null;
            notifyDataSetChanged();
        }
    }

    public void addLoadingView(View view) {
        if (view == null) {
            throw new NullPointerException("LoadingView is null!");
        }
        if (loadView != null) {
            return;
        }
        loadView = view;
        loadView.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    private void removeLoadingView() {
        loading = false;
        notifyDataSetChanged();
    }

    @Override
    public final int getItemViewType(int position) {
        if (headerView != null && position == 0) {
            return TYPE_HEADER;
        }
        if (position >= (headerView == null ? getCount() : getCount() + 1) && loading && loadView != null) {
            return TYPE_LOAD_MORE;
        }
        if (footerView != null && position >= (headerView == null ? getCount() : getCount() + 1)) {
            return TYPE_FOOTER;
        }
        return getItemType(headerView == null ? position : position - 1);
    }

    public int getItemType(int position) {
        return TYPE_DEFAULT;
    }

    @Override
    public final int getItemCount() {
        int count = getCount();
        if (count == 0) {
            return 0;
        }
        if (loading && loadView != null) {
            count++;
        }
        if (footerView != null) {
            count++;
        }
        if (headerView != null) {
            count++;
        }
        return count;
    }

    public final void reset() {
        if (recyclerView != null) {
            //必须主线程notify
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    if (loading) {
                        removeLoadingView();
                    } else {
                        notifyDataSetChanged();
                    }
                    loading = false;
                }
            });
        }
    }

    /**
     * 设置是否已经全部加载完成
     * @param loadAll
     */
    public final void setLoadAll(boolean loadAll) {
        if (loading) {
            reset();
        } else if (!loadAll) {
            footerView = null;
        }
        isLoadAll = loadAll;
    }

    protected abstract int getCount();

    protected abstract RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType);

    protected abstract void onBindView(RecyclerView.ViewHolder holder, int position);

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOAD_MORE) {
            return new BaseHolder(loadView);
        } else if (viewType == TYPE_FOOTER) {
            return new BaseHolder(footerView);
        } else if (viewType == TYPE_HEADER) {
            return new BaseHolder(headerView);
        } else {
            final RecyclerView.ViewHolder viewHolder = onCreateView(parent, viewType);
            if (onItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = viewHolder.getAdapterPosition();
                        if (headerView != null) {
                            pos = pos - 1;
                        }
                        onItemClickListener.onItemClick(v, pos);
                    }
                });
            }
            if (onItemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = viewHolder.getAdapterPosition();
                        if (headerView != null) {
                            pos = pos - 1;
                        }
                        onItemLongClickListener.onItemLongClick(v, pos);
                        return false;
                    }
                });
            }
            return viewHolder;
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof BaseHolder)) {
            position = holder.getAdapterPosition();
            if (headerView != null) {
                position = position - 1;
            }
            onBindView(holder, position);
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder);
        }
    }

    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return true;
        }
        return false;
    }

    protected void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        int viewType = holder.getItemViewType();
        if (viewType == TYPE_LOAD_MORE || viewType == TYPE_HEADER || viewType == TYPE_FOOTER) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(true);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView1) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView == null) {
            recyclerView = recyclerView1;
        }
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = (GridLayoutManager) manager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int viewType = getItemViewType(position);
                    if (viewType == TYPE_LOAD_MORE || viewType == TYPE_HEADER
                            || viewType == TYPE_FOOTER) {
                        return gridManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    /**
     * 设置加载更多监听
     */
    private void setLoadMoreListener() {
        if (onLoadMoreListener != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)
                                && dy > 0 && !isLoadAll && getCount() > 0) {
                            loading = true;
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (loadView != null) {
                                        notifyItemInserted(getItemCount() - 1);
                                    }
                                    onLoadMoreListener.onLoadMore();
                                }
                            });
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
                        if (!loading && staggeredGridLayoutManager.getItemCount() <= lastVisibleItemPosition + 1
                                && dy > 0 && !isLoadAll && getCount() > 0) {
                            loading = true;
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (loadView != null) {
                                        notifyItemInserted(getItemCount() - 1);
                                    }
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

    private class BaseHolder extends RecyclerView.ViewHolder {

        public BaseHolder(View itemView) {
            super(itemView);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
}