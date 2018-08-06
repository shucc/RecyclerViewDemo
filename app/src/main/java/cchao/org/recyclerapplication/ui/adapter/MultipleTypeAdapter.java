package cchao.org.recyclerapplication.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cchao.org.baseadapterlibrary.BaseAdapter;
import cchao.org.baseadapterlibrary.BaseHolder;
import cchao.org.recyclerapplication.R;

/**
 * Created by chenchao on 16/9/8.
 * cc@cchao.org
 */
public class MultipleTypeAdapter extends BaseAdapter<MultipleTypeAdapter.NormalHolder> {

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
    protected BaseHolder onCreateView(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multiple_type_one, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multiple_type_two, parent, false);
        }
        return new NormalHolder(view);
    }

    @Override
    protected void onBindView(NormalHolder holder, int position) {
         holder.textView.setText(data.get(position));
    }

    protected class NormalHolder extends BaseHolder {

        TextView textView;

        public NormalHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.recyclerview_text);
        }
    }
}
