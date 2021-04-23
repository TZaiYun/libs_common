package utils;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by cathy on 2016/11/8.
 */
public class DipHelper {


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 获取屏幕宽度
     */
    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService( Context.WINDOW_SERVICE);

        int width = wm.getDefaultDisplay().getWidth();
//        int height = wm.getDefaultDisplay().getHeight();
        return width;
    }


    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
