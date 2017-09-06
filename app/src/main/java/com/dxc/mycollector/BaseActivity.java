package com.dxc.mycollector;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowId;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.bluetooth.BluetoothTools;
import com.dxc.mycollector.logs.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunyi on 2017/8/25.
 */

public class BaseActivity extends AppCompatActivity {
    String TAG = BaseActivity.class.getSimpleName();
    //    protected String[] planetTitles;
    protected DrawerLayout drawerLayout;
    protected ListView drawerList;
    protected FrameLayout frameLayout;
    protected String[] planetTitles = null;//{"个人信息", "任务管理", "数据管理", "安全管理", "仪器设置", "系统升级", "关于系统"};
    protected int[] imagesId = {R.drawable.assignment, R.drawable.down,
            R.drawable.data, R.drawable.measure, R.drawable.update, R.drawable.system, R.drawable.safe};
    Context context;
    private Dialog mWeiboDialog;//对话框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    //定义加载等待页面方法
    public void waitingDialog() {
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(context, "加载中...");//加载对话框
        mHandler.sendEmptyMessageDelayed(1, 500);//处理消息
    }

    //消息处理线程
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //DialogThridUtils.closeDialog(mDialog);
                    WeiboDialogUtils.closeDialog(mWeiboDialog);
                    break;
            }
        }
    };

    /**
     * 重写setContentView，以便于在保留侧滑菜单的同时，让子Activity根据需要加载不同的界面布局
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.menu_activity_first, null);
        frameLayout = (FrameLayout) drawerLayout.findViewById(R.id.content_frame);
        // 将传入的layout加载到activity_base的content_frame里面
        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(drawerLayout);
        setUpNavigation();
    }

    /**
     * 初始化侧滑菜单
     */
    private void setUpNavigation() {
        planetTitles = getResources().getStringArray(R.array.planets_array);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View layout = View.inflate(getApplicationContext(), R.layout.menu_list_item, null);
                ImageView imgv = (ImageView) layout.findViewById(R.id.lgface);
                TextView name1 = (TextView) layout.findViewById(R.id.name1);
                LinearLayout lin = (LinearLayout) layout.findViewById(R.id.item_lin_1);
                ImageView face = (ImageView) layout.findViewById(R.id.lgicon);
                TextView name = (TextView) layout.findViewById(R.id.menu_name);
                TextView num = (TextView) layout.findViewById(R.id.num);
                LinearLayout logout = (LinearLayout) layout.findViewById(R.id.logout);
                Button btnexitall = (Button) layout.findViewById(R.id.exitbtn);
                btnexitall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context)
                                .setTitle("退出系统")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setMessage("您确定要从系统中退出？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DLApplication.userName = "";
                                        finish();
                                        startActivity(new Intent(BaseActivity.this, MainActivity.class));
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();

//                        new AlertDialog.Builder(getApplicationContext()).setTitle("确认退出吗？")
//                                .setIcon(android.R.drawable.ic_dialog_info)
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // 点击“确认”后的操作
//
//                                    }
//                                })
//                                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // 点击“返回”后的操作,这里不设置没有任何操作
//                                    }
//                                }).show();
                        // super.onBackPressed();


                    }
                });


                if (position > 0) {
                    imgv.setVisibility(View.GONE);
                    name1.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    layout.invalidate();
                }
                //是否显示任务数
                if (position != 1 && position != 2 && position != 3) {
                    num.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    layout.invalidate();
                }

                //admin
                if (DLApplication.userName != null && !DLApplication.userName.equals(DLApplication.amdin)) {
                    if (position != 6) {
                        face.setImageResource(imagesId[position]);
                        name.setText(planetTitles[position]);
                    } else {//==7
                        lin.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                        layout.invalidate();
                    }
                } else {
                    face.setImageResource(imagesId[position]);
                    name.setText(planetTitles[position]);
                }

                if (position != 6) {
                    logout.setVisibility(View.GONE);
                    btnexitall.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    layout.invalidate();
                }
                return layout;
            }


            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return planetTitles[position];
            }

            @Override
            public int getCount() {
                return planetTitles.length;
            }
        };
        drawerList.setAdapter(adapter);
//        drawerList.setAdapter(new ArrayAdapter<>(BaseActivity.this,
//                R.layout.menu_list_item, planetTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
//        Toast.makeText(BaseActivity.this, planetTitles[position], Toast.LENGTH_SHORT).show();
        switch (position) {
            case 0:
                startActivity(new Intent(this, PersonAcitvity.class));
                break;
            case 1:
                startActivity(new Intent(this, ShowTaskInfo.class));
                Logger.i(TAG, "click task download.");
                break;
            case 2:
                //startActivity(new Intent(this, BlueToothFolder.class));
                startActivity(new Intent(this, UploadBlueToothFolder.class));
                Logger.i(TAG, "click bluetooth folder  search.");
                break;
//            case 3:
//                startActivity(new Intent(this, ShowExamineRecord.class));
//                Logger.i(TAG, "click safety examine.");
//                break;
            case 3:
                startActivity(new Intent(this, DeviceSettingActivity.class));
                Logger.i(TAG, "click devices setting.");
                break;
            case 4:
                startActivity(new Intent(this, UpdateSystemActivity.class));
                Logger.i(TAG, "click update system.");
                break;
            case 5:
                startActivity(new Intent(this, AboutSystemActivity.class));
                Logger.i(TAG, "click about system.");
                break;
            case 6:
                startActivity(new Intent(this, UserListAcitvity.class));
                Logger.i(TAG, "click user list.This operation belongs to the administrator.");
                break;
        }
    }

    /**
     * @param list get All Html Key Vules
     */
    private void showDialog(List<String> list) {
        new AlertDialog.Builder(this)
                .setTitle("列表框")
                .setItems(new String[]{"列表项1", "列表项2", "列表项3"}, null)
                .setNegativeButton("确定", null)
                .show();
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
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
////            this.finish();
//            // 创建退出对话框
//            AlertDialog isExit = new AlertDialog.Builder(this).create();
//            // 设置对话框标题
//            isExit.setTitle("系统提示");
//            // 设置对话框消息
//            isExit.setMessage("确定要退出吗");
//            // 添加选择按钮并注册监听
//            isExit.setButton("确定", listener);
//            isExit.setButton2("取消", listener);
//            // 显示对话框
//            isExit.show();
////            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    finish();
//                    System.exit(0);
                    startActivity(new Intent(getApplication(), MainActivity.class));
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };
}