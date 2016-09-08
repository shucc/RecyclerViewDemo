package cchao.org.recyclerapplication.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cchao.org.recyclerapplication.R;

/**
 * Created by chenchao on 16/9/8.
 * cc@cchao.org
 */
public class MultipleTypeAdapter extends BaseAdapter {

    private final int TYPE_ONE = 100;
    private final int TYPE_TWO = 200;

    private List<String> data;

    public MultipleTypeAdapter(List<String> data) {
        this.data = data;
    }

    @Override
    public int getItemType(int position) {
        if (position % 2 == 0) {
            return TYPE_ONE;
        } else {
            return TYPE_TWO;
        }
    }

    @Override
    protected int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    protected RecyclerView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multiple_type_one, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multiple_type_two, parent, false);
        }
        return new NormalHolder(view);
    }

    @Override
    protected void onBindView(RecyclerView.ViewHolder holder, int position) {
        ((NormalHolder) holder).textView.setText(data.get(position));
    }

    private class NormalHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public NormalHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.recyclerview_text);
        }
    }
}
