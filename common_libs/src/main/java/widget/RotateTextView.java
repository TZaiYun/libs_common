package widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;

import com.nisco.common_libs.R;


public class RotateTextView extends androidx.appcompat.widget.AppCompatTextView {

    @Override
    public void setText(CharSequence text, BufferType type) {
        // TODO Auto-generated method stub
        super.setText(text, type);
    }

    private static final int DEFAULT_DEGREES = 0;
    //水印文字
    public static String waterMark = "田在运022190江苏金恒信息科技股份有限公司";

    private int mDegrees;

    public RotateTextView(Context context) {
        super(context, null);
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.textViewStyle);

        this.setGravity(Gravity.CENTER);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RotateTextView);

        mDegrees = a.getDimensionPixelSize(R.styleable.RotateTextView_degree,
                DEFAULT_DEGREES);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
        canvas.rotate(mDegrees, this.getWidth() / 2f, this.getHeight() / 2f);
        super.onDraw(canvas);
        canvas.restore();
    }

    public void setDegrees(int degrees) {
        mDegrees = degrees;
    }
}
