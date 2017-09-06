/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.bluetooth;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dxc.mycollector.BaseActivity;
import com.dxc.mycollector.R;
import com.dxc.mycollector.WeiboDialogUtils;
import com.dxc.mycollector.logs.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc 获取蓝牙列表，选择配对
 * Created by Gospel on 2017/9/4 21:01
 * DXC technology
 */

public class BlueToothListActivity extends BaseActivity {
    static String TAG = BlueToothListActivity.class.getSimpleName();
    //该UUID表示串口服务
    //请参考文章<a href="http://wiley.iteye.com/blog/1179417">http://wiley.iteye.com/blog/1179417</a>
    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    ListView lvBTDevices;
    ArrayAdapter<String> adtDevices;
    List<String> lstDevices = new ArrayList<String>();
    BluetoothAdapter btAdapt;
    public static BluetoothSocket btSocket;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    Context context;
    private Dialog mWeiboDialog;//对话框
    int connect = 0;
    String getAddress = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_homepage_layout2);
        context = this;
        waitingConnect(3000);
        // ListView及其数据源 适配器
        lvBTDevices = (ListView) this.findViewById(R.id.lvDevices);
        adtDevices = new ArrayAdapter<String>(this,
                R.layout.person_homepage_layout2_item, lstDevices);
        lvBTDevices.setAdapter(adtDevices);
        lvBTDevices.setOnItemClickListener(new ItemClickEvent());

        btAdapt = BluetoothAdapter.getDefaultAdapter();// 初始化本机蓝牙功能
        Logger.i(TAG, "设备蓝牙状态：" + btAdapt.getState());

        if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启
            Logger.i(TAG, "申请开启蓝牙...");
            boolean iena = btAdapt.enable();
            if (iena) {
                Logger.i(TAG, "同意开启蓝牙功能.");
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initBluetooth();
            } else {
                Logger.i(TAG, "蓝牙开启请求被拒绝.");
                Intent intent = new Intent();
                // Set result and finish this Activity
                intent.putExtra("result", "蓝牙开启请求被拒绝");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        } else {
            Logger.i(TAG, "蓝牙功能已开启.");
            initBluetooth();
        }


        // ============================================================
        // 注册Receiver来获取蓝牙设备相关的结果
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(searchDevices, intent);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//??????
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("蓝牙列表");

        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //    定义加载等待页面方法
    public void waitingConnect(int speed) {
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(context, "正在连接...");//加载对话框
//        mHandler.sendEmptyMessageDelayed(1, speed);//处理消息
    }

    //    消息处理线程
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (context != null && mWeiboDialog != null) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                    }
                    break;
                case 2:
                    if (context != null && mWeiboDialog != null) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                    }
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_DEVICE_ADDRESS, getAddress);
//                    intent.putExtra("result", null);
                    // Set result and finish this Activity
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    break;
                case 3:
                    if (context != null && mWeiboDialog != null) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                    }
                    Intent intent1 = new Intent();
                    intent1.putExtra(EXTRA_DEVICE_ADDRESS, getAddress);
                    intent1.putExtra("result", "无法连接到该设备，请确保设备蓝牙已经打开.");
                    // Set result and finish this Activity
                    setResult(Activity.RESULT_OK, intent1);
                    finish();
                    break;
                case 4:
                    if (context != null && mWeiboDialog != null) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                    }
                    Intent intent2 = new Intent();
                    intent2.putExtra(EXTRA_DEVICE_ADDRESS, getAddress);
                    intent2.putExtra("result", "取消配对");
                    // Set result and finish this Activity
                    setResult(Activity.RESULT_OK, intent2);
                    finish();
                    break;
                case 5:
                    if (context != null && mWeiboDialog != null) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                    }
                    break;
            }
        }
    };

    private BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            Object[] lstName = b.keySet().toArray();

            // 显示所有收到的消息及其细节
            for (int i = 0; i < lstName.length; i++) {
                String keyName = lstName[i].toString();
                Logger.i(keyName, String.valueOf(b.get(keyName)));
            }
            BluetoothDevice device = null;
            // 搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    String str = " 未配对 |" + device.getName() + " | "
                            + device.getAddress();
                    if (lstDevices.indexOf(str) == -1)// 防止重复添加
                        lstDevices.add(str); // 获取设备名称和mac地址
                    adtDevices.notifyDataSetChanged();
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Logger.i(TAG, "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Logger.i(TAG, "完成配对");
                        connect(device);//连接设备
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Logger.i(TAG, "取消配对");
                        mHandler.sendEmptyMessageDelayed(4, 1000);//处理消息
                    default:
                        break;
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(searchDevices);
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    class ItemClickEvent implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            if (btAdapt.isDiscovering()) btAdapt.cancelDiscovery();
            String str = lstDevices.get(arg2);
            String[] values = str.split("\\|");
            String address = values[2].trim();
            Logger.i(TAG, "选择了设备" + str);
            BluetoothDevice btDev = btAdapt.getRemoteDevice(address);
            getAddress = btDev.getAddress();
            try {
                Boolean returnValue = false;
                if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
                    //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);
//                    Method autoBondMethod = BluetoothDevice.class.getMethod("setPin", new Class[]{byte[].class});
//                    Boolean result = (Boolean) autoBondMethod
//                            .invoke(btDev, new Object[]{"1234".getBytes()});
                    Method createBondMethod = BluetoothDevice.class
                            .getMethod("createBond");
                    returnValue = (Boolean) createBondMethod.invoke(btDev);
                    Logger.i(TAG, "开始配对..." + returnValue);
                    waitingConnect(3000);
                } else if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
//                    Toast.makeText(context, "正在连接...", Toast.LENGTH_SHORT);
                    waitingConnect(3000);
                    //r是1 连接失败...
                    int r = connect(btDev);
                    Log.i(TAG, "connect result::" + connect);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private int connect(final BluetoothDevice btDev) {
//        UUID uuid = UUID.fromString(SPP_UUID);
        final int[] r = {0};
        new Thread() {
            public void run() {
                if (r[0] == 0) {
                    try {
                        btSocket.connect();
                        r[0] = 1;
                        Logger.i(TAG, "Connected");
                    } catch (Exception e) {
                        Logger.e(TAG, "btSocket.connect() failed." + e.getMessage());
                        try {
                            Logger.e(TAG, "trying fallback...");
                            Logger.i(TAG, "调用发射机制连接设备...");
                            btSocket = (BluetoothSocket) btDev.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(btDev, 1);
                            btSocket.connect();
                            mHandler.sendEmptyMessageDelayed(2, 3000);//处理消息
                            r[0] = 1;
                            Logger.i(TAG, "连接成功.");
                        } catch (Exception e2) {
                            Logger.e(TAG, "Couldn't establish Bluetooth connection!");
                            mHandler.sendEmptyMessageDelayed(3, 2000);//处理消息
                            r[0] = 1;
                        }
                    }
                }
            }
        }.start();
        return r[0];
    }

    /**
     * 启动搜索蓝牙设备
     */
    public void initBluetooth() {
        final int[] r = {0};
        new Thread() {
            public void run() {
                if (btAdapt.isDiscovering())
                    btAdapt.cancelDiscovery();
                lstDevices.clear();
                //检查蓝牙状态，只有蓝牙已经开启才开始搜索蓝牙
                while (r[0] == 0) {
//                if (btAdapt.getState() == BluetoothAdapter.STATE_ON) {
                    Object[] lstDevice = btAdapt.getBondedDevices().toArray();
                    Logger.i(TAG, "list::" + lstDevice.length);
                    //组装搜索到的蓝牙信息
                    for (int i = 0; i < lstDevice.length; i++) {
                        BluetoothDevice device = (BluetoothDevice) lstDevice[i];
                        String str = " 已配对 | " + device.getName() + " | "
                                + device.getAddress();
                        lstDevices.add(str); // 获取设备名称和mac地址
                        adtDevices.notifyDataSetChanged();
                    }
                    btAdapt.startDiscovery();
                    //只有搜索到蓝牙才退出搜索
                    if (lstDevice.length > 0) {
                        r[0] = 1;
                        mHandler.sendEmptyMessageDelayed(5, 1000);//处理消息
                    } else {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
//                }
            }
        }.start();
    }
//    class ClickEvent implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            if (v == btnSearch)// 搜索蓝牙设备，在BroadcastReceiver显示结果
//            {
//                if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启
//                    Toast.makeText(BlueToothListActivity.this, "请先打开蓝牙", Toast.LENGTH_LONG)
//                            .show();
//                    return;
//                }
//                if (btAdapt.isDiscovering())
//                    btAdapt.cancelDiscovery();
//                lstDevices.clear();
//                Object[] lstDevice = btAdapt.getBondedDevices().toArray();
//                for (int i = 0; i < lstDevice.length; i++) {
//                    BluetoothDevice device = (BluetoothDevice) lstDevice[i];
//                    String str = "已配对|" + device.getName() + "|"
//                            + device.getAddress();
//                    lstDevices.add(str); // 获取设备名称和mac地址
//                    adtDevices.notifyDataSetChanged();
//                }
//                setTitle("本机蓝牙地址：" + btAdapt.getAddress());
//                btAdapt.startDiscovery();
//            } else if (v == tbtnSwitch) {// 本机蓝牙启动/关闭
//                if (tbtnSwitch.isChecked() == false)
//                    btAdapt.enable();
//
//                else if (tbtnSwitch.isChecked() == true)
//                    btAdapt.disable();
//            } else if (v == btnDis)// 本机可以被搜索
//            {
//                Intent discoverableIntent = new Intent(
//                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//                discoverableIntent.putExtra(
//                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//                startActivity(discoverableIntent);
//            } else if (v == btnExit) {
//                try {
//                    if (btSocket != null)
//                        btSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                BlueToothListActivity.this.finish();
//            }
//        }
//    }
}