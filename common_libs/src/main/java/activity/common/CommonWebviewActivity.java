package activity.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.nisco.common_libs.R;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import activity.base.BaseActivity;
import bean.User;
import constant.CommonConstants;
import utils.FileUtils;
import utils.PermissionsChecker;
import utils.SharedPreferenceUtil;
import widget.RotateTextView;
import widget.x5webview.X5WebView;

/**
 * 公共的Webview Activity
 */
public class CommonWebviewActivity extends BaseActivity {
    private X5WebView mWebView;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ValueCallback<Uri[]> mFilePathCallback = null;
    private ValueCallback<Uri> mFilePathCallbackLow = null;
    private int REQUEST_CODE_LOLIPOP = 1;  // 5.0以上版本
    private int VIDEO_REQUEST = 2;  // 5.0以上版本
    private String mCameraPhotoPath = "";  // 拍照的图片路径
    private String userNo = "";
    private ProgressBar pg1;
    private boolean isvideo = false; // 是否是录像
    private TextView mTitleTv;
    private RelativeLayout mMoreRl;

    private ValueCallback<Uri> uploadFile;//定义接受返回值
    private ValueCallback<Uri[]> uploadFiles;
    private LinearLayout closeLayout;
    private String loadUrl;
    private boolean isAdd;

    private User user;


    @Override
    protected void initUI() {
        setContentView(R.layout.activity_common_webview);
    }

    @Override
    protected void initViews() {
        closeLayout = findViewById(R.id.close_ll);
        closeLayout.setOnClickListener(this);
        pg1 = (ProgressBar) findViewById(R.id.progressBar1);
        mWebView = (X5WebView) findViewById(R.id.webView);
        mTitleTv = findViewById(R.id.title);
        mMoreRl = (RelativeLayout) findViewById(R.id.more_rl);

        mMoreRl.setOnClickListener(this);
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        user = (User) SharedPreferenceUtil.get(CommonConstants.USERINFO_FILE_NAME, CommonConstants.USERINFO_KEY_NAME);
        mPermissionsChecker = new PermissionsChecker(this);
        loadUrl = getIntent().getStringExtra("url") == null ? "" : getIntent().getStringExtra("url");
        showWebView();
    }

    @Override
    protected void onStart() {
        int watermark = getIntent().getIntExtra("watermark", 0);
        if (!isAdd) {
            ViewGroup rootView = getRootView(this);
            View framView = LayoutInflater.from(this).inflate(R.layout.watermark_webview_bg, null);
            RotateTextView mFragmentTag11 = (RotateTextView) framView.findViewById(R.id.fragment_tag11);
            RotateTextView mFragmentTag12 = (RotateTextView) framView.findViewById(R.id.fragment_tag12);
            RotateTextView mFragmentTag13 = (RotateTextView) framView.findViewById(R.id.fragment_tag13);
            String waterMarkStr = "";
            if (1 == watermark) { // 姓名公司
                if (!TextUtils.isEmpty(user.getName())) {
                    waterMarkStr = waterMarkStr + user.getName();
                }
                if (!TextUtils.isEmpty(user.getCompanyName())) {
                    waterMarkStr = waterMarkStr + user.getCompanyName();
                }
            } else if (2 == watermark) { // 姓名工号
                if (!TextUtils.isEmpty(user.getName())) {
                    waterMarkStr = waterMarkStr + user.getName();
                }
                if (!TextUtils.isEmpty(user.getUserNo())) {
                    waterMarkStr = waterMarkStr + user.getUserNo();
                }
                if (!TextUtils.isEmpty(user.getCompanyName())) {
                    waterMarkStr = waterMarkStr + user.getCompanyName();
                }
            } else if (3 == watermark) { // 姓名手机号
                if (!TextUtils.isEmpty(user.getName())) {
                    waterMarkStr = waterMarkStr + user.getName();
                }
                if (!TextUtils.isEmpty(user.getPhone())) {
                    waterMarkStr = waterMarkStr + user.getPhone();
                }
                if (!TextUtils.isEmpty(user.getCompanyName())) {
                    waterMarkStr = waterMarkStr + user.getCompanyName();
                }
            }
            if (0 != watermark) {
                mFragmentTag11.setText(waterMarkStr, TextView.BufferType.EDITABLE);
                mFragmentTag12.setText(waterMarkStr, TextView.BufferType.EDITABLE);
                mFragmentTag13.setText(waterMarkStr, TextView.BufferType.EDITABLE);
                rootView.addView(framView);
                isAdd = true;
            }
        }
        super.onStart();

    }

    //查找布局的底层
    protected static ViewGroup getRootView(Activity context) {
        return (ViewGroup) context.findViewById(android.R.id.content);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.close_ll) {
            finish();
        } else if (v.getId() == R.id.more_rl) {
            if (null != mWebView) {
                showMoreDialog();
            }
        }
    }

    private void showWebView() {
        LogUtils.d("111", "webview：" + loadUrl);
        Map<String, String> map = new HashMap<String, String>();
        map.put("token", null == user ? "" : user.getToken());
        mWebView.loadUrl(loadUrl, map);
        showCustomWebChromeClient();
        mWebView.addJavascriptInterface(new JavascriptCall(), "android");
    }

    // 更多按钮弹框
    private void showMoreDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentview = inflater.inflate(R.layout.popup_more_layout, null);
        TextView clearTv = contentview.findViewById(R.id.clear_tv);
        PopupWindow popupWindow = new PopupWindow(contentview, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);

        clearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QbSdk.clearAllWebViewCache(mContext, true);
                popupWindow.dismiss();
                finish();
            }
        });

        popupWindow.showAsDropDown(mMoreRl);
    }

    private class JavascriptCall {
        @JavascriptInterface //js接口
        public void goToSign() {
            ActivityCompat.requestPermissions(
                    mContext,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    CommonConstants.CAMERA_REQ_CODE);
        }

        @JavascriptInterface //js接口
        public void goToPlay(String msg) {
            try {
                JSONObject jsonObject = new JSONObject(msg);
                Log.e("TAG", jsonObject.toString());
//                Bundle bundle = new Bundle();
//                bundle.putString("videoPath", jsonObject.getString("videoUrl"));
//                bundle.putString("videoName", jsonObject.getString("videoName"));
//                bundle.putString("imageUrl", jsonObject.getString("videoPoster"));
//                pageJumpResultActivity(mContext, VideoPlayActivity.class, bundle);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface //js接口 下载附件
        public void openAndroidFile(String url, String fileName) {
            if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(fileName)) {
                FileUtils.downLoadOpenFile(mContext, url, fileName);
            }
        }

        @JavascriptInterface //关闭当前页
        public void finishCurrentPage() {
            finish();
        }

        @JavascriptInterface //横竖屏切换
        public void changeScreen() {
            changeScreenOrientation();
        }

    }

    /**
     * 横竖屏切换
     */
    public void changeScreenOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void showCustomWebChromeClient() {
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                mWebView.loadUrl(url);
//                LogUtils.d("111", "地址：" + url);
//                if (url.contains("fssc.nisco.cn") || url.contains("zhgh.nisco.cn") || url.contains("mcmp.nisco.cn:8443/spa") || url.contains("jhjs.nisco.cn:81/webroot/decision/view") || url.contains("jhjs.nisco.cn:81/dsBulletinBoard")) {
//
//                } else {
//                    Intent intent = new Intent();
//                    intent.setAction("android.intent.action.VIEW");
//                    Uri content_url = Uri.parse(url);
//                    intent.setData(content_url);
//                    startActivity(intent);
//                }
                return true;
            }
        });

        mWebView.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {

            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if (newProgress == 100) {
                    pg1.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg1.setProgress(newProgress);//设置进度值
                }

                String titleStr = webView.getTitle();
                if (titleStr.indexOf("jhjs.nisco.cn") == -1 && titleStr.indexOf("gw.nisco.cn") == -1 && titleStr.indexOf("mcmp.nisco.cn") == -1 && titleStr.indexOf("fssc.nisco.cn") == -1) {
                    mTitleTv.setText(titleStr);
                } else {
                    mTitleTv.setText("");
                }
            }

            // 5.0 +
            @Override
            public boolean onShowFileChooser(com.tencent.smtt.sdk.WebView webView, com.tencent.smtt.sdk.ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
                String[] acceptTypes = fileChooserParams.getAcceptTypes();
                for (int i = 0; i < acceptTypes.length; i++) {
                    if (acceptTypes[i].contains("video")) {
                        isvideo = true;
                    } else {
                        isvideo = false;
                    }
                }

                if (!isvideo) { // 非视频
                    if (mFilePathCallback != null) {
                        mFilePathCallback.onReceiveValue(null);
                    }
                    mFilePathCallback = valueCallback;
                } else { // 视频
                    if (uploadFiles != null) {
                        uploadFiles.onReceiveValue(null);
                    }
                    uploadFiles = valueCallback;
                }

                if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                    }
                } else {
                    if (!isvideo) { // 非视频
                        Intent intent = gotoChooseFile();  // 选择文件及拍照
                        startActivityForResult(intent, REQUEST_CODE_LOLIPOP);
                    } else { // 视频
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
                        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
                        startActivityForResult(intent, VIDEO_REQUEST);
                    }
                }
                return true;
            }

            // 4.0 +
            @Override
            public void openFileChooser(com.tencent.smtt.sdk.ValueCallback<Uri> valueCallback, String s, String s1) {
                if (s.contains("video")) {
                    if (uploadFile != null) {
                        uploadFile.onReceiveValue(null);
                    }
                    uploadFile = valueCallback;
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
                    startActivityForResult(intent, VIDEO_REQUEST);
                    return;
                }

                mFilePathCallbackLow = valueCallback;
                Intent intent = gotoChooseFile();  // 选择文件及拍照
                startActivityForResult(intent, REQUEST_CODE_LOLIPOP);
            }
        });
    }

    /**
     * 选择文件及拍照
     */
    private Intent gotoChooseFile() {
        String saveName = Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DCIM + "/Camera/";

        /**
         * 打开相机intent
         */
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            Uri imageUri = null;
            try {
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(mContext, getApplicationContext().getPackageName() + ".fileprovider", createImageFile());
                } else {
                    imageUri = Uri.fromFile(createImageFile());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //temp sd card file

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }

        Intent[] takeoutArray = null;
        if (takePictureIntent != null) {
            takeoutArray = new Intent[]{takePictureIntent};
        } else {
            takeoutArray = new Intent[0];
        }

        /**
         * 获取图片intent
         */
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        /**
         * 使用系统选择器
         */
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, takeoutArray);  // 额外的intent

        return chooserIntent;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/don_test/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCameraPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOLIPOP) {  // 选择文件返回 5.0+
            Uri[] results = null;
            if (null != mFilePathCallback) {
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        if (mCameraPhotoPath != null) {
//                            results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                            results = new Uri[]{Uri.fromFile(new File(mCameraPhotoPath))};
                        }
                    } else {
                        String dataString = data.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
                mFilePathCallback.onReceiveValue(results);  // 当获取要传图片的Uri，通过该方法回调通知
                mFilePathCallback = null;
            } else if (null != mFilePathCallbackLow) {
                if (resultCode == RESULT_OK) {
                    if (null == data) {
                        Uri result = getUri(data, mContext);
                        if (null != result) {
                            mFilePathCallbackLow.onReceiveValue(result);
                            mFilePathCallbackLow = null;
                        }
                    } else {
                        Uri result = data.getData();
                        mFilePathCallbackLow.onReceiveValue(result);
                        mFilePathCallbackLow = null;
                    }
                }
            }
        } else if (requestCode == VIDEO_REQUEST) {
            if (null != uploadFile) {
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                uploadFile.onReceiveValue(result);
                uploadFile = null;
            }
            if (null != uploadFiles) {
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
                uploadFiles.onReceiveValue(new Uri[]{result});
                uploadFiles = null;
            }
        } else if (requestCode == CommonConstants.CAMERA_REQ_CODE) {
            // 扫描二维码/条码回传
            if (data != null && resultCode == RESULT_OK) {
                HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
                String content = "";
                if (obj != null) {
                    content = obj.getShowResult();
                }
                String method = "javascript:scanCallBack('" + content + "')";
                mWebView.loadUrl(method);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();// 返回前一个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 解决小米手机上获取图片路径为null的情况
     *
     * @param intent
     * @return
     */
    public static Uri getUri(Intent intent, Context context) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (null != uri && null != type) {
            if (uri.getScheme().equals("file") && (type.contains("image/"))) {
                String path = uri.getEncodedPath();
                if (path != null) {
                    path = Uri.decode(path);
                    ContentResolver cr = context.getContentResolver();
                    StringBuffer buff = new StringBuffer();
                    buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                            .append("'" + path + "'").append(")");
                    Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Images.ImageColumns._ID},
                            buff.toString(), null, null);
                    int index = 0;
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                        index = cur.getInt(index);
                    }
                    if (index == 0) {
                        // do nothing
                    } else {
                        Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                        if (uri_temp != null) {
                            uri = uri_temp;
                        }
                    }
                }
            }
        }
        return uri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!isvideo) { // 非视频
                        Intent intent = gotoChooseFile();  // 选择文件及拍照
                        startActivityForResult(intent, REQUEST_CODE_LOLIPOP);
                    } else { // 视频
                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
                        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
                        startActivityForResult(intent, VIDEO_REQUEST);
                    }
                } else {
                    Toast.makeText(mContext, "应用没有提供相应的权限，无法使用该功能", Toast.LENGTH_SHORT).show();
                }
                break;
            case CommonConstants.CAMERA_REQ_CODE:
                if (permissions == null || grantResults == null) {
                    return;
                }
                if (grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                ScanUtil.startScan(this, CommonConstants.CAMERA_REQ_CODE, new HmsScanAnalyzerOptions.Creator().create());

                break;
            default:
                break;
        }
    }

    public void back(View view) {
        if (mWebView.canGoBack()) {
            mWebView.goBack(); //goBack()表示返回WebView的上一页面
        } else {
            finish();
            mWebView.clearCache(true);
        }
    }

    @Override
    protected void onDestroy() {
        if (null != mWebView) {
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        super.onDestroy();
    }
}