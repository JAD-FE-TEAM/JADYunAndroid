package com.jd.ad.demo.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.jd.ad.demo.R;

import java.lang.reflect.Method;

public class BasePermissionActivity extends BaseActivity {
    /**
     * 读取设备信息权限
     */
    private static final String KEY_PHONE_STATE = "设备信息权限";
    /**
     * 读取定位权限
     */
    private static final String KEY_LOCATION = "定位权限";
    /**
     * 读写外部存储权限（SD卡）
     */
    private static final String KEY_STORAGE = "存储权限";
    /**
     * 读取应用列表权限
     */
    private static final String KEY_APP_LIST = "应用列表权限";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.permissions_menu, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.permissions_list) {
            return true;
        }
        // 明文提示用户申请权限
        switch (id) {
            case R.id.phone_state:
                // 申请权限：android.permission.READ_PHONE_STATE
                // 获取IMEI, 有助于转化
                showRequestPhoneStateDialog();
                break;
            case R.id.location:
                // 申请权限：android.permission.ACCESS_COARSE_LOCATION
                // 获取定位, 有助于精准投放
                showRequestLocationDialog(false);
                break;
            case R.id.location_gps:
                // 申请权限：android.permission.ACCESS_COARSE_LOCATION
                // 获取定位, 有助于精准投放
                showRequestLocationDialog(true);
                break;
            case R.id.storage:
                // 申请权限：android.permission.WRITE_EXTERNAL_STORAGE
                // 获取外部存储权限，用于广告的下载和缓存
                showRequestExternalStorageDialog();
                break;

//            case R.id.app_list:     // 轮播图+文字模版
//                showRequestAppListDialog();
//                break;
            default:
                // nop
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000:
                showTip(KEY_PHONE_STATE, grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
            case 1001:
                showTip(KEY_LOCATION, grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;
            case 1002:
                showTip(KEY_STORAGE, grantResults[0] == PackageManager.PERMISSION_GRANTED);
                break;

        }
    }


    private void showRequestPhoneStateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请求广告需要设备信息权限，是否允许?");
        builder.setPositiveButton("允许", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(BasePermissionActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                        showTip(KEY_PHONE_STATE, true);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1000);
                    }
                } else {
                    showTip(KEY_PHONE_STATE, true);
                }
            }
        });
        builder.setNegativeButton("禁止", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showTip(KEY_PHONE_STATE, false);
            }
        });
        builder.show();
    }

    private void showRequestLocationDialog(boolean isGps) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请求广告需要定位权限，是否允许?");
        builder.setPositiveButton("允许", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (isGps) {
                        if (checkSelfPermission(BasePermissionActivity.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showTip(KEY_LOCATION, true);
                        } else {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
                        }
                    } else {
                        if (checkSelfPermission(BasePermissionActivity.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showTip(KEY_LOCATION, true);
                        } else {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                        }
                    }
                } else {
                    showTip(KEY_LOCATION, true);
                }
            }
        });
        builder.setNegativeButton("禁止", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showTip(KEY_LOCATION, false);
            }
        });
        builder.show();
    }

    private void showRequestExternalStorageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请求广告需要外部存储权限，是否允许?");
        builder.setPositiveButton("允许", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Android Q（API 29）支持分区存储，无需单独申请外部存储权限
                if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 29) {
                    if (checkSelfPermission(BasePermissionActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        showTip(KEY_STORAGE, true);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1002);
                    }
                } else {
                    showTip(KEY_STORAGE, true);
                }
            }
        });
        builder.setNegativeButton("禁止", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showTip(KEY_STORAGE, false);
            }
        });
        builder.show();
    }

    private void showRequestAppListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请求广告需要读取应用列表，是否允许?");
        builder.setPositiveButton("允许", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showTip(KEY_APP_LIST, true);
            }
        });
        builder.setNegativeButton("禁止", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showTip(KEY_APP_LIST, false);
            }
        });
        builder.show();
    }


    private boolean checkSelfPermission(Context context, String permission) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                Method method = Context.class.getMethod("checkSelfPermission",
                        String.class);
                return (Integer) method.invoke(context, permission) == PackageManager.PERMISSION_GRANTED;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void showTip(String permission, boolean hasPermission) {
        String tip = permission + " 获取 " + (hasPermission ? "成功" : "失败");
        showToast(tip);
    }
}
