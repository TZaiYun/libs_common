package com.nisco.mycommonlibsapp;

import androidx.annotation.NonNull;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.zzhoujay.richtext.RichText;

import java.io.IOException;

import activity.base.BaseActivity;
import bean.User;
import constant.CommonConstants;
import data.source.RemoteCommonDataSource;
import http.InfoCallback;
import utils.CommonUtils;
import utils.PermissionsChecker;
import utils.SharedPreferenceUtil;
import utils.TextUtil;

public class MainActivity extends BaseActivity {

    private RemoteCommonDataSource remoteCommonDataSource;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private PermissionsChecker mPermissionsChecker; // 权限检测器
    private static final int PERMISSION_REQUEST_CODE = 1;
    private TextView mToastTv;

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