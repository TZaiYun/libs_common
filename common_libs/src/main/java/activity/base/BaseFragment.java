package activity.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nisco.common_libs.R;


public abstract class BaseFragment extends Fragment implements OnTouchListener, View.OnClickListener {

    public BaseActivity context;

    protected int layout;

    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = this.initUI();
        rootView = inflater.inflate(layout, container, false);
        context = (BaseActivity) getActivity();
        this.initViews();
        this.initActivity();

        return rootView;
    }

    protected abstract int initUI();

    protected abstract void initViews();

    protected abstract void initActivity();

    // onTouch事件 将上层的触摸事件拦截
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // 拦截触摸事件，防止泄露下去
        view.setOnTouchListener(this);
    }

    public void pageJumpResultActivity(Context packageContext, Class<?> cls,
                                       Bundle bundle) {
        Intent intent = new Intent(packageContext, cls);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
        ((Activity) packageContext).overridePendingTransition(
                R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onClick(View v) {

    }
}