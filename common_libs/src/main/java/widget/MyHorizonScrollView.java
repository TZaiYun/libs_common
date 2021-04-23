package widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import androidx.annotation.RequiresApi;

/**
 * @author : cathy
 * @package : com.guiying.module.common.widget
 * @time : 2018/06/07
 * @desc :
 * @version: 1.0
 */

public class MyHorizonScrollView extends HorizontalScrollView {
    private int lastX = 0;
    private int lastY = 0;

    public MyHorizonScrollView(Context context) {
        super(context);
    }

    public MyHorizonScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizonScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyHorizonScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        boolean intercept = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                intercept = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = x - lastX;
                int dy = y - lastY;
                if (Math.abs(dx) > Math.abs(dy)) {
                    intercept = true;
                } else {
                    intercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercept = false;
        }
        lastX = x;
        lastY = y;
        return intercept;
    }
}
