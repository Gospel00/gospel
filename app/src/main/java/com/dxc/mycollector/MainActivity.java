package com.dxc.mycollector;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.taskDownload.DownLoadService;
import com.dxc.mycollector.utils.DateConver;
import com.dxc.mycollector.utils.DeviceUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
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
    private LocationManager locationManager;// 位置管理类

    private String provider;// 位置提供器


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
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(context, "正在登录...");//加载对话框
        mHandler.sendEmptyMessageDelayed(1, 500);//处理消息
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
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    };

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        checkPermissions(needPermissions);
        //初始化文件夹
        initFolder();
        if (DLApplication.userName != null && DLApplication.userName.length() > 0) {
            startActivity(new Intent(getApplicationContext(), PersonAcitvity.class));
            finish();
        }
        context = this;
        button = (Button) findViewById(R.id.login);
        registerBtn = (Button) findViewById(R.id.register);
        username = (EditText) findViewById(R.id.username);
        lgpwd = (EditText) findViewById(R.id.lgpwd);
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
                    int isTure = SqliteUtils.getInstance(getApplicationContext()).Quer(lgpwd.getText().toString(), username.getText().toString());
                    if (isTure == 1) {
//                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        DLApplication.userName = username.getText().toString();
                        Logger.i(TAG, DLApplication.userName + " login success.");
//                        context.startService(new Intent(context, DownLoadService.class));
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

        String weiyibiaoshi = "唯一标识：" + DeviceUtil.getDeviceId(context);
        String pingpai = "设备品牌：" + DeviceUtil.getPhoneBrand();
        String xinghao = "设备型号：" + DeviceUtil.getPhoneModel();
        String api = "设备SDK：" + DeviceUtil.getBuildLevel() + "";
        String sdkhao = "设备系统版本：" + DeviceUtil.getBuildVersion();
        String sbhight = "设备尺寸高：" + DeviceUtil.deviceHeight(context) + "";
        String sbwidht = "设备尺寸宽：" + DeviceUtil.deviceWidth(context) + "";
        String kyncdx = "可运行内存：" + getAvailMemory();
//        String zxtncdx = "总系统内存：" + getTotalMemory();
        String zn = "总系统内存：" + getZm();
        String deviceInfo = weiyibiaoshi + "\n" + pingpai + "\n" + xinghao +
                "\n" + api + "\n" + sdkhao + "\n" + sbhight +
                "\n" + sbwidht + "\n" + kyncdx + "\n" + zn;
        Logger.i("DeviceInfo", deviceInfo);
        // 更新当前位置
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // 获得LocationManager的实例
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            //优先使用gps
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            // 没有可用的位置提供器
            Toast.makeText(MainActivity.this, "没有位置提供器可供使用", Toast.LENGTH_LONG)
                    .show();
//            Logger.i("DeviceInfo", "没有位置提供器可供使用");
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
//             显示当前设备的位置信息
//            Logger.i("DeviceInfo", "第一次请求位置信息");
            showLocation(location, "");
        } else {
            Logger.i("DeviceInfo", "无法获得当前位置");
        }
        locationManager.requestLocationUpdates(provider, 30 * 1000, 1,
                locationListener);


    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            // 设备位置发生改变时，执行这里的代码
            String changeInfo = "隔10秒刷新的提示：\n 时间：" + DateConver.getStringDate()
                    + ",\n当前的经度是：" + location.getLongitude() + ",\n 当前的纬度是："
                    + location.getLatitude();
            showLocation(location, changeInfo);
        }
    };

    public String getZm() {
        try {
            final String mem_path = "/proc/meminfo";// 系统内存信息文件，第一行为内存大小
            Reader reader = null;
            BufferedReader bufferedReader = null;
            reader = new FileReader(mem_path);
            bufferedReader = new BufferedReader(reader, 8192);
            long totalRAMSize = Long.parseLong(bufferedReader.readLine().split("\\s+")[1]) * 1024L;//这里*1024是转换为单位B（字节）
            return "" + Formatter.formatFileSize(getBaseContext(), totalRAMSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 显示当前设备的位置信息
     *
     * @param location
     */
    private String showLocation(Location location, String changeInfo) {
        // TODO Auto-generated method stub
        String currentLocation = "当前的经度是：" + location.getLongitude() + ",\n"
                + "当前的纬度是：" + location.getLatitude();
        Logger.i("DeviceInfo", currentLocation);
        return currentLocation;
    }

    // 获取android当前可用内存大小
    private String getAvailMemory() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存

        return Formatter.formatFileSize(getBaseContext(), mi.availMem);// 将获取的内存大小规格化
    }

    // 获得系统总内存
    private String getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
    }

    /**
     * 根据路径获取内存状态
     *
     * @param path
     * @return
     */
    private String getMemoryInfo(File path) {
        // 根据文件对象的路径获得一个磁盘状态对象
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSize();   // 获得一个扇区的大小

        long totalBlocks = stat.getBlockCount();    // 获得扇区的总数

        long availableBlocks = stat.getAvailableBlocks();   // 获得可用的扇区数量

        // 总空间
        String totalMemory = Formatter.formatFileSize(this, totalBlocks * blockSize);
        // 可用空间
        String availableMemory = Formatter.formatFileSize(this, availableBlocks * blockSize);

        return "总空间: " + totalMemory + "\n可用空间: " + availableMemory;
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
