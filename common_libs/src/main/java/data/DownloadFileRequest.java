package data;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author cathy
 * 创建日期
 * 描述
 */
public class DownloadFileRequest {
    private static int TIME_OUT = 30; // 30秒超时断开连接
    private static Activity activity;

    public DownloadFileRequest(Activity activity) {
        this.activity = activity;
    }

    public static void fileDownload(String downloadUrl, final String fileName, final OnDownloadListener listener) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD", "download failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                // 储存下载文件的目录
                String mSDCardPath = Environment.getExternalStorageDirectory().getPath() + "/";
                File routeFile = new File(mSDCardPath, fileName);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(routeFile);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        final int progress = (int) (sum * 1.0f / total * 100);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.onDownloading(progress);
                            }
                        });
                    }
                    fos.flush();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onDownloadSuccess();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onDownloadFailed();
                        }
                    });
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }
}
