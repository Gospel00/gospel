/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dxc.mycollector.BaseActivity;
import com.dxc.mycollector.R;
import com.dxc.mycollector.logs.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @desc 获取蓝牙列表，选择配对
 * Created by Gospel on 2017/9/4 21:01
 * DXC technology
 */

public class BlueToothListActivity extends Activity {
    static String TAG = BlueToothListActivity.class.getSimpleName();
    //该UUID表示串口服务
    //请参考文章<a href="http://wiley.iteye.com/blog/1179417">http://wiley.iteye.com/blog/1179417</a>
    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    Button btnSearch, btnDis, btnExit;
    ToggleButton tbtnSwitch;
    ListView lvBTDevices;
    ArrayAdapter<String> adtDevices;
    List<String> lstDevices = new ArrayList<String>();
    BluetoothAdapter btAdapt;
    public static BluetoothSocket btSocket;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_homepage_layout2);
        // Button 设置
//        btnSearch = (Button) this.findViewById(R.id.btnSearch);
//        btnSearch.setOnClickListener(new ClickEvent());
//        btnExit = (Button) this.findViewById(R.id.btnExit);
//        btnExit.setOnClickListener(new ClickEvent());
//        btnDis = (Button) this.findViewById(R.id.btnDis);
//        btnDis.setOnClickListener(new ClickEvent());
//
//        // ToogleButton设置
//        tbtnSwitch = (ToggleButton) this.findViewById(R.id.tbtnSwitch);
//        tbtnSwitch.setOnClickListener(new ClickEvent());

        // ListView及其数据源 适配器
        lvBTDevices = (ListView) this.findViewById(R.id.lvDevices);
        adtDevices = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, lstDevices);
        lvBTDevices.setAdapter(adtDevices);
        lvBTDevices.setOnItemClickListener(new ItemClickEvent());

        btAdapt = BluetoothAdapter.getDefaultAdapter();// 初始化本机蓝牙功能

        // ========================================================
        // modified by wiley
        /*
         * if (btAdapt.getState() == BluetoothAdapter.STATE_OFF)// 读取蓝牙状态并显示
         * tbtnSwitch.setChecked(false); else if (btAdapt.getState() ==
         * BluetoothAdapter.STATE_ON) tbtnSwitch.setChecked(true);
         */
//        if (btAdapt.isEnabled()) {
//            tbtnSwitch.setChecked(false);
//        } else {
//            tbtnSwitch.setChecked(true);
//        }
        Logger.i(TAG, "设备蓝牙状态：" + btAdapt.getState());
        if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启
            Logger.i(TAG, "申请开启蓝牙...");
            btAdapt.enable();
//            Intent discoverableIntent = new Intent(
//                    BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(
//                    BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            startActivity(discoverableIntent);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        Logger.i(TAG, "设备蓝牙状态：" + btAdapt.getState());
        if (btAdapt.getState() == BluetoothAdapter.STATE_ON) {
            Logger.i(TAG, "蓝牙已经开启.");
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
    }

    private BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            Object[] lstName = b.keySet().toArray();

            // 显示所有收到的消息及其细节
            for (int i = 0; i < lstName.length; i++) {
                String keyName = lstName[i].toString();
                Log.e(keyName, String.valueOf(b.get(keyName)));
            }
            BluetoothDevice device = null;
            // 搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    String str = "未配对|" + device.getName() + "|"
                            + device.getAddress();
                    if (lstDevices.indexOf(str) == -1)// 防止重复添加
                        lstDevices.add(str); // 获取设备名称和mac地址
                    adtDevices.notifyDataSetChanged();
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.d("BlueToothTestActivity", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d("BlueToothTestActivity", "完成配对");
                        connect(device);//连接设备
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d("BlueToothTestActivity", "取消配对");
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
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    class ItemClickEvent implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            if (btAdapt.isDiscovering()) btAdapt.cancelDiscovery();
            String str = lstDevices.get(arg2);
            String[] values = str.split("\\|");
            String address = values[2];
            Logger.i(TAG, "选择了设备" + str);
            BluetoothDevice btDev = btAdapt.getRemoteDevice(address);

//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            Intent intent = new Intent();

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

                    intent.putExtra(EXTRA_DEVICE_ADDRESS, str);
                    // Set result and finish this Activity
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
                    //r是1 连接失败...
                    int r = connect(btDev);
                    intent.putExtra(EXTRA_DEVICE_ADDRESS, str);
                    intent.putExtra("result", r);
                    // Set result and finish this Activity
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private int connect(BluetoothDevice btDev) {
        UUID uuid = UUID.fromString(SPP_UUID);
        int r = 0;
        try {
            btSocket.connect();
            Logger.i(TAG, "Connected");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            try {
                Logger.e(TAG, "trying fallback...");
                btSocket = (BluetoothSocket) btDev.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(btDev, 1);
                btSocket.connect();
                Logger.e(TAG, "Connected");
            } catch (Exception e2) {
                Logger.e(TAG, "Couldn't establish Bluetooth connection!");
            }
        }
//        try {
//            btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
//            Logger.i(TAG, "开始连接...");
//            btSocket.connect();
//        } catch (IOException e) {
//            r = 1;
//            Logger.i(TAG, "连接异常..." + e.getMessage());
//            e.printStackTrace();
//        }
        return r;
    }

    /**
     * 启动搜索蓝牙设备
     */
    public void initBluetooth() {
        if (btAdapt.isDiscovering())
            btAdapt.cancelDiscovery();
        lstDevices.clear();
        Object[] lstDevice = btAdapt.getBondedDevices().toArray();
        for (int i = 0; i < lstDevice.length; i++) {
            BluetoothDevice device = (BluetoothDevice) lstDevice[i];
            String str = "已配对|" + device.getName() + "|"
                    + device.getAddress();
            lstDevices.add(str); // 获取设备名称和mac地址
            adtDevices.notifyDataSetChanged();
        }
        setTitle("请选择要配对的设备");
        btAdapt.startDiscovery();
    }

    class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == btnSearch)// 搜索蓝牙设备，在BroadcastReceiver显示结果
            {
                if (btAdapt.getState() == BluetoothAdapter.STATE_OFF) {// 如果蓝牙还没开启
                    Toast.makeText(BlueToothListActivity.this, "请先打开蓝牙", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (btAdapt.isDiscovering())
                    btAdapt.cancelDiscovery();
                lstDevices.clear();
                Object[] lstDevice = btAdapt.getBondedDevices().toArray();
                for (int i = 0; i < lstDevice.length; i++) {
                    BluetoothDevice device = (BluetoothDevice) lstDevice[i];
                    String str = "已配对|" + device.getName() + "|"
                            + device.getAddress();
                    lstDevices.add(str); // 获取设备名称和mac地址
                    adtDevices.notifyDataSetChanged();
                }
                setTitle("本机蓝牙地址：" + btAdapt.getAddress());
                btAdapt.startDiscovery();
            } else if (v == tbtnSwitch) {// 本机蓝牙启动/关闭
                if (tbtnSwitch.isChecked() == false)
                    btAdapt.enable();

                else if (tbtnSwitch.isChecked() == true)
                    btAdapt.disable();
            } else if (v == btnDis)// 本机可以被搜索
            {
                Intent discoverableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(
                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
            } else if (v == btnExit) {
                try {
                    if (btSocket != null)
                        btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BlueToothListActivity.this.finish();
            }
        }
    }
}