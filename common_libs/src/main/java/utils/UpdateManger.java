package utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.LogUtils;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.nisco.common_libs.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author tzy
 * 
 */
public class UpdateManger {
	private Context mContext;
	private String apkUrl = "http://jhjs.nisco.cn:81/appUpdate/download/";
	private static final String savePath = "/sdcard/updatedemo/";// 保存apk的文件夹
	private static final String saveFileName = savePath
			+ "UpdateRelease.apk";

	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private NumberProgressBar mProgress;
	private int progress = 0;// 当前进度
	private Thread downLoadThread; // 下载线程
	private boolean interceptFlag = false;// 用户取消下载
	private Dialog builder;

	private Handler mHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				installApk();
				break;
			}
			super.handleMessage(msg);
		}
	};

	public UpdateManger(Context context, String labelStr) {
		this.mContext = context;
		this.apkUrl = labelStr;
	}

	/**
	 * 获取当前的版本号
	 * @param context
	 * @return
	 */
	public static String getNowVersionName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			return packInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 显示更新程序对话框，供主程序调用
	public void UpdateInfo() {
		showDownloadDialog();
	}

	protected void showDownloadDialog() {
		builder = new Dialog(mContext, R.style.dialog);
		builder.setContentView(R.layout.my_update_dialog);
		builder.setCancelable(false);
		mProgress = (NumberProgressBar) builder.findViewById(R.id.my_progress);

		ImageView cancel_btn = (ImageView) builder.findViewById(R.id.dialog_cancle);
		cancel_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.dismiss();
				interceptFlag = true;
			}
		});
		builder.show();
		downloadApk();
	}

	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	public void installApk() {
		if (null != builder){
			builder.dismiss();
		}
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上
			Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", new File(saveFileName));
			Intent install = new Intent(Intent.ACTION_VIEW);
			install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
			install.setDataAndType(apkUri, "application/vnd.android.package-archive");
			mContext.startActivity(install);
		} else {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
					"application/vnd.android.package-archive");// File.toString()会返回路径信息
			mContext.startActivity(i);
		}
	}

	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			LogUtils.d("111", "下载地址：" + apkUrl);
			URL url;
			try {
				url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream ins = conn.getInputStream();

				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream outStream = new FileOutputStream(ApkFile);
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = ins.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 下载进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					outStream.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消停止下载
				outStream.close();
				ins.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

}