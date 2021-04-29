package activity.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.nisco.common_libs.R;

import utils.StatusBarUtil;
import utils.ViewManager;


public abstract class BaseActivity extends AppCompatActivity implements OnClickListener {

	protected BaseActivity mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		mContext = this;
		this.initUI();
		this.initViews();
		this.initActivity(savedInstanceState);
		ViewManager.getInstance().addActivity(this);
		setStatusBar(R.color.transparent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		ViewManager.getInstance().finishActivity(this);
		super.onDestroy();
	}

	/**
	 * 方法说明：初始化UI操作
	 *
	 * @return void
	 * @Exception 异常对象
	 * 
	 */
	protected abstract void initUI();

	/**
	 * 方法说明：初始化View操作
	 *
	 * @return void
	 * @Exception 异常对象
	 * 
	 */
	protected abstract void initViews();

	/**
	 * 方法说明：初始化Activity对象操作
	 * 
	 * @param
	 * @return void
	 * @Exception 异常对象
	 * 
	 */
	protected abstract void initActivity(Bundle savedInstanceState);

	@Override
	public void onClick(View v) {

	}

	/***
	 * 关闭activity
	 */
	public void finishAnim(Activity activity) {
		activity.finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	/**
	 * 切换界面带有返回状态
	 * 
	 * @param packageContext
	 * @param cls
	 * @param bundle
	 */
	public void pageJumpResultActivity(Context packageContext, Class<?> cls,
			Bundle bundle) {
		Intent intent = new Intent(packageContext, cls);
		if (bundle != null)
			intent.putExtras(bundle);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	/**
	 * 界面导航按钮的返回事件
	 * @param view
	 */
	public void back(View view) {
		finish();
	}


	public void setStatusBar(int colorId) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (isUseFullScreenMode()) {
//                StatusBarUtil.transparencyBar(this);
//            } else {
			StatusBarUtil.setStatusBarColor(this, colorId);
//            }

//            if (isUseBlackFontWithStatusBar()) {
			StatusBarUtil.setLightStatusBar(getWindow(), true, true);
//            }
		}
	}
}
