package com.nisco.mycommonlibsapp;

import androidx.annotation.NonNull;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.zzhoujay.richtext.RichText;

import activity.base.BaseActivity;
import bean.User;
import data.source.RemoteCommonDataSource;
import http.InfoCallback;
import utils.CommonUtils;
import utils.PermissionsChecker;
import utils.TextUtil;

public class MainActivity extends BaseActivity {

    private RemoteCommonDataSource remoteCommonDataSource;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int PERMISSION_REQUEST_CODE = 1;
    private TextView mToastTv;
    private String loadUrl = "http://jhjs.nisco.cn:81/efamily-web/#/cleangover?userNo=022190";
    private String richText = "<p>关于开展&ldquo;学党史、知党情、强党性&rdquo;线上<br />\\r\\n知识竞赛通知<br />\\r\\n&nbsp;<br />各党工委、直属党组织:<br />&nbsp; &nbsp; 为纪念中国共产党成立100周年，促进广大党员及员工对党的历史和理论知识的了解，经公司党委研究决定，在全公司开展&ldquo;学党史、知党情、强党性&rdquo;线上知识竞赛。现将竞赛事项通知如下:<br />一、竞赛时间:&nbsp;<br />2021年4月16日下午4：00至5月10日上午11：00。<br />二、参赛对象:公司在职党员及职工。<br />三、竞赛内容:党史及党的理论知识、南钢发展史等。<br />四、竞赛方式:登录南钢E家&mdash;&mdash;全部&mdash;&mdash;智慧党建&mdash;&mdash;党史知识竞赛。系统在党史题库中随机抽取30题进行答题，答题时间在10分钟以内，超过时间系统将自动结束；每人有1次参与机会，系统按答题正确率及答题所用时间进行排名。同时，除了竞赛外，党员及职工每日可通过系统中&ldquo;趣味答题&rdquo;进行党史知识学习。<br />\\r\\n五、设置奖项：<br />\\r\\n一等奖：前50名<br />\\r\\n二等奖：51-120名<br />\\r\\n三等奖：121-200名<br />\\r\\n单位组织奖：5名<br />\\r\\n&nbsp;<br />\\r\\n党委工作部<br />\\r\\n2021年4月16日</p>";
    private String markDownText = "~`~`7 您有会议`~`8 you have  Meeting`~`9 您有會議`~`~: 战运部特殊需求-日程功能集中开发   ~`~`7 会议时间`~`8 Meeting Times`~`9 會議時間`~`~:2021-04-01 08:00 ~`~`7 会议地点`~`8 Meeting Place`~`9 會議地點`~`~:金恒公司-产业园3号楼-2#小会议室（25座）";

    @Override
    protected void initUI() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        mPermissionsChecker = new PermissionsChecker(this);
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        } else {

        }



        remoteCommonDataSource = new RemoteCommonDataSource();
        remoteCommonDataSource.getVersionInfo("http://jhjs.nisco.cn:81", "/appUpdate/appInfo/efamily", new InfoCallback<String>() {
            @Override
            public void onSuccess(String info) {
                LogUtils.d("111", "相应的数据：" + info);
            }

            @Override
            public void onError(int code, String message) {
                LogUtils.d("111", "相应的失败：" + message);
            }
        });
        mToastTv = (TextView) findViewById(R.id.toast_tv);

        RichText.fromHtml(richText).into(mToastTv);

        mToastTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonUtils.showUpdateDialog(mContext, "升级提醒升级提醒升级升级提醒升级提醒升级升级提醒升级提醒升级", 1.18, "http://efamily.nisco.cn:80/group1/M00/71/C3/rB34dmB0GnKAPL2NAmh40vTOn40791.apk" );


//                Bundle bundle = new Bundle();
//                bundle.putString("url", loadUrl);
//                pageJumpResultActivity(mContext, CommonVideoPlayActivity.class, bundle);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
                break;
            default:
                break;
        }
    }
}