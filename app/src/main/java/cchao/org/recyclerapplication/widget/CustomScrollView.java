package cchao.org.recyclerapplication.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.widget.Scroller;

/**
 * Created by shucc on 17/8/30.
 * cc@cchao.org
 */
public class CustomScrollView extends NestedScrollView {

    private Scroller scroller;

    public CustomScrollView(Context context) {
        this(context, null);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }

    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        scroller.startScroll(startX, startY, dx, dy, duration);
        invalidate();
    }
}
