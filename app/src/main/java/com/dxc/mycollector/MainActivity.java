package com.dxc.mycollector;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.taskDownload.DownLoadService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * About Login
 */
public class MainActivity extends Activity implements
        ActivityCompat.OnRequestPermissionsResultCallback {
    String TAG = MainActivity.class.getSimpleName();
    private Button button;//登录按钮
    private Button registerBtn;//注册按钮
    private EditText username;
    private EditText lgpwd;
    Context context;
    private Dialog mWeiboDialog;//对话框
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //DialogThridUtils.closeDialog(mDialog);
                    if (context != null && mWeiboDialog != null) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                    }
                    break;
            }
        }
    };

    //定义加载等待页面方法
    public void waitingDialog() {
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(context, "加载中...");//加载对话框
        mHandler.sendEmptyMessageDelayed(1, 500);//处理消息
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        if (DLApplication.userName != null && DLApplication.userName.length() > 0) {
            startActivity(new Intent(getApplicationContext(), PersonAcitvity.class));
            finish();
        }
        context = this;
        button = (Button) findViewById(R.id.login);
        registerBtn = (Button) findViewById(R.id.register);
        username = (EditText) findViewById(R.id.username);
        lgpwd = (EditText) findViewById(R.id.lgpwd);
        username.setText("gospel");
        lgpwd.setText("gospel5200");
        Drawable username_drawable = getResources().getDrawable(R.drawable.login);
        Drawable password_drawable = getResources().getDrawable(R.drawable.lock);
        //四个参数分别是设置图片的左、上、右、下的尺寸
        username_drawable.setBounds(0, 0, 60, 60);
        password_drawable.setBounds(0, 0, 60, 60);
        //这个是选择将图片绘制在EditText的位置，参数对应的是：左、上、右、下
        username.setCompoundDrawables(username_drawable, null, null, null);
        lgpwd.setCompoundDrawables(password_drawable, null, null, null);

        context = this;
        /**
         * 登录按钮的点击事件
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingDialog();//加载等待页面对话框方法
                if (username != null && username.length() > 0) {
                    //初始化文件夹
                    initFolder();
                    int isTure = SqliteUtils.getInstance(getApplicationContext()).Quer(lgpwd.getText().toString(), username.getText().toString());
                    if (isTure == 1) {
//                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        DLApplication.userName = username.getText().toString();
                        Logger.i(TAG, DLApplication.userName + " login success.");
                        context.startService(new Intent(context, DownLoadService.class));
                        startActivity(new Intent(context, PersonAcitvity.class));
                        finish();
                    } else if (isTure == 0) {
                        Toast.makeText(MainActivity.this, "用户不存在", Toast.LENGTH_LONG).show();
                        Logger.i(TAG, username.getText().toString() + " login,User name not found.login failed.");
                    } else {
                        Toast.makeText(MainActivity.this, "密码错误，请重新输入", Toast.LENGTH_LONG).show();
                        Logger.i(TAG, username.getText().toString() + " login,password error.login failed.");
                    }
                } else {
                    Toast.makeText(MainActivity.this, "用户名不能为空", Toast.LENGTH_LONG).show();
                }
            }
        });


        /**
         * 注册按钮的点击事件
         */
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterAcitvity.class));
            }
        });
    }

    public void initFolder() {
        String dbPath = Environment.getExternalStorageDirectory().getPath() + "/Tunnel/log";
        //创建日志存放目录
        File dbp = new File(dbPath);
        if (!dbp.exists()) {
            dbp.mkdirs();
            Logger.i(TAG, "日志存放目录已创建：" + dbPath);
        }
        //创建数据库存放目录
        dbPath = Environment.getExternalStorageDirectory().getPath() + "/Tunnel/database/";
        dbp = new File(dbPath);
        if (!dbp.exists()) {
            dbp.mkdirs();
            Logger.i(TAG, "数据库存放目录已创建：" + dbPath);
        }
        //创建蓝牙存放目录
        dbPath = Environment.getExternalStorageDirectory().getPath() + "/bluetooth/";
        dbp = new File(dbPath);
        if (!dbp.exists()) {
            dbp.mkdirs();
            Logger.i(TAG, "蓝牙存放目录已创建：" + dbPath);
        }
    }

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheck) {
            checkPermissions(needPermissions);
        }
    }

    /**
     * requestPermissions方法是请求某一权限，
     */
    private void checkPermissions(String... permissions) {
        List<String> needRequestPermissonList = findDeniedPermissions(permissions);
        if (null != needRequestPermissonList
                && needRequestPermissonList.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissonList.toArray(
                            new String[needRequestPermissonList.size()]),
                    PERMISSON_REQUESTCODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return checkSelfPermission方法是在用来判断是否app已经获取到某一个权限
     * shouldShowRequestPermissionRationale方法用来判断是否
     * 显示申请权限对话框，如果同意了或者不在询问则返回false
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissonList.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, perm)) {
                    needRequestPermissonList.add(perm);
                }
            }
        }
        return needRequestPermissonList;
    }

    /**
     * 检测是否所有的权限都已经授权
     *
     * @param grantResults
     * @return
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                Logger.i(TAG, " PERMISSION request success.");
            }
        }
        return true;
    }

    /**
     * 申请权限结果的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUESTCODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                //showMissingPermissionDialog();
                isNeedCheck = false;
            }
        }
    }

    /**
     * 显示提示信息
     */
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当前应用缺少必要权限。请点击\"设置\"-\"权限\"-打开所需权限。");

        // 拒绝, 退出应用
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton("设置",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();
                    }
                });

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
