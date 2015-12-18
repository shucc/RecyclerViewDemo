package cchao.org.recyclerapplication.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cchao.org.recyclerapplication.R;
import cchao.org.recyclerapplication.listener.OnItemClickListener;
import cchao.org.recyclerapplication.listener.OnLoadMoreListener;

/**
 * Created by chenchao on 15/11/24.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOT = 2;

    //标示不同布局
    public static final int LINEAR_RECYCLER = 3;
    public static final int GRID_RECYCLER = 4;
    public static final int WATER_FALL_RECYCLER = 5;
    private int type;

    private RecyclerView mRecyclerView;

    //数据集
    private ArrayList<String> mDataset;

    //瀑布流的高度
    private List<Integer> mHeight;

    //底部itemNum
    private int footItemNum = 0;

    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;

    private boolean loading;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnItemClickListener mOnItemClickListener;

    //最后一个的位置
    private int[] lastPositions;
    //最后一个可见的item的位置
    private int lastVisibleItemPosition;

    public RecyclerAdapter(RecyclerView recyclerView, final ArrayList<String> mDataset, int type) {
        this.mDataset = mDataset;
        this.mRecyclerView = recyclerView;
        this.type = type;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (type == LINEAR_RECYCLER) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
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
        } else if (type == GRID_RECYCLER || type == WATER_FALL_RECYCLER) {
            if (type == WATER_FALL_RECYCLER) {
                //图片随机高度的初始化
                mHeight = new ArrayList<Integer>();
                for (int i = 0; i < mDataset.size(); i++) {
                    mHeight.add((int)(300 + Math.random() * 300));
                }
            }
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
                    if (!loading && staggeredGridLayoutManager.getItemCount() <= lastVisibleItemPosition + 1) {
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

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener ) {
        this.mOnLoadMoreListener = mOnLoadMoreListener ;
    }

    public void setLoaded() {
        mDataset.remove(footItemNum);
        notifyItemRemoved(footItemNum + 1);
        notifyDataSetChanged();
        loading = false;
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
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof NormalViewHolder) {

            if (type == WATER_FALL_RECYCLER) {
                ViewGroup.LayoutParams lp = ((NormalViewHolder) viewHolder).mTextView.getLayoutParams();
                lp.height = mHeight.get(i);
                ((NormalViewHolder) viewHolder).mTextView.setLayoutParams(lp);
            } else {
                ViewGroup.LayoutParams lp = ((NormalViewHolder) viewHolder).mTextView.getLayoutParams();
                //因为是和瀑布流共用了item到layout，所以在此设置高度，正常在item到layout文件设置
                lp.height = 400;
                ((NormalViewHolder) viewHolder).mTextView.setLayoutParams(lp);
            }

            ((NormalViewHolder) viewHolder).mTextView.setText(mDataset.get(i));
            if (mOnItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = viewHolder.getLayoutPosition();
                        mOnItemClickListener.OnItemClick(viewHolder.itemView, position);
                    }
                });
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int position = viewHolder.getLayoutPosition();
                        mOnItemClickListener.onItemLongClick(viewHolder.itemView, position);
                        return false;
                    }
                });
            }
        } else if (viewHolder instanceof FootViewHolder){
            ((FootViewHolder) viewHolder).loadText.setText("Loading...");
            ((FootViewHolder) viewHolder).progressBar.setVisibility(View.VISIBLE);
            if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.setFullSpan(true);
                viewHolder.itemView.setLayoutParams(layoutParams);
            }
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
