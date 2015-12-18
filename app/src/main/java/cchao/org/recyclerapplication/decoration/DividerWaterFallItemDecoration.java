package cchao.org.recyclerapplication.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by chenchao on 15/12/18.
 */
public class DividerWaterFallItemDecoration extends RecyclerView.ItemDecoration{

    private Paint paintBorder, mPaint;
    private int offset;

    public DividerWaterFallItemDecoration(Context c){
        offset = 10;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);

        paintBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBorder.setColor(Color.TRANSPARENT);
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(10);

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(offset, offset, offset, offset);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        c.drawRect(
                0,
                0,
                c.getWidth(),
                c.getHeight(),
                paintBorder);

        for(int i=0; i<parent.getChildCount(); i++){
            final View child = parent.getChildAt(i);
            c.drawRect(
                    layoutManager.getDecoratedLeft(child),
                    layoutManager.getDecoratedTop(child),
                    layoutManager.getDecoratedRight(child),
                    layoutManager.getDecoratedBottom(child),
                    mPaint);
            c.drawRect(
                    layoutManager.getDecoratedLeft(child) + offset,
                    layoutManager.getDecoratedTop(child) + offset,
                    layoutManager.getDecoratedRight(child) - offset,
                    layoutManager.getDecoratedBottom(child) - offset,
                    mPaint);
        }

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }
}
