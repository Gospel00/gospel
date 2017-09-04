/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.bluetooth;

import android.annotation.SuppressLint;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dxc.mycollector.R;
import com.dxc.mycollector.logs.Logger;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This Activity appears as a dialog. It lists any paired devices and devices
 * detected in the area after discovery. When a device is chosen by the user,
 * the MAC address of the device is sent back to the parent Activity in the
 * result Intent.
 * <p>
 * Created by Gospel on 2017/9/4 14:47
 * DXC technology
 */
@SuppressLint("ShowToast")
public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;
    private ListView newDevicesListView;
    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    public Context mContext;

    //该UUID表示串口服务
    //请参考文章<a href="http://wiley.iteye.com/blog/1179417">http://wiley.iteye.com/blog/1179417</a>
    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    ArrayAdapter<String> adtDevices;
    List<String> lstDevices = new ArrayList<String>();
    BluetoothAdapter btAdapt;
    public static BluetoothSocket btSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);

        adtDevices = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, lstDevices);


        // Find and set up the ListView for newly discovered devices
        newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(adtDevices);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(
                BluetoothTools.ACTION_FOUND_DEVICE);
        this.registerReceiver(mReceiver, filter);
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter
                .getDefaultAdapter().getBondedDevices();
        mNewDevicesArrayAdapter.add("BondedDevices List("
                + pairedDevices.size() + "):");
        System.out.println("BondeDevices size:" + pairedDevices.size());
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            // final BluetoothDevice blueDev[] = new
            // BluetoothDevice[pairedDevices.size()];
            // String[] items = new String[blueDev.length];
            int i = 0;
            for (BluetoothDevice devicel : pairedDevices) {
                // blueDev[i] = devicel;
                // items[i] = blueDev[i].getName() + ": " +
                // blueDev[i].getAddress();
                // mArrayAdapter.add(device.getName() + "\n" +
                // device.getAddress());
                int deviceType1 = devicel.getBluetoothClass()
                        .getMajorDeviceClass();
                System.out.println("BondedDevicesType:" + deviceType1);
                // computer蓝牙为256、phone 蓝牙为512、打印机蓝牙为1536等等
                String type1 = "";
                if (deviceType1 == 256) {
                    type1 = " - PC";
                }
                if (deviceType1 == 512) {
                    type1 = " - MOBILE";
                }
                if (deviceType1 == 1536) {
                    type1 = " - PRINTER";
                }
                if (deviceType1 == 1024) {
                    type1 = " - SOUND";
                }
                mNewDevicesArrayAdapter.add(devicel.getName() + type1
                        + "\n" + devicel.getAddress());
                i++;
            }
        }
        mNewDevicesArrayAdapter.add("FoundDevices List:");
        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        Intent startSearchIntent = new Intent(
                BluetoothTools.ACTION_START_DISCOVERY);
        sendBroadcast(startSearchIntent);// 广播意图信息
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        newDevicesListView.setVisibility(View.VISIBLE);

        btAdapt = BluetoothAdapter.getDefaultAdapter();// 初始化本机蓝牙功能

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

    class ItemClickEvent implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            if (btAdapt.isDiscovering()) btAdapt.cancelDiscovery();
            String str = lstDevices.get(arg2);
            String[] values = str.split("\\|");
            String address = values[2];
            Log.e("address", values[2]);
            BluetoothDevice btDev = btAdapt.getRemoteDevice(address);
            try {
                Boolean returnValue = false;
                if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
                    //利用反射方法调用BluetoothDevice.createBond(BluetoothDevice remoteDevice);
                    Method createBondMethod = BluetoothDevice.class
                            .getMethod("createBond");
                    Log.d("BlueToothTestActivity", "开始配对");
                    returnValue = (Boolean) createBondMethod.invoke(btDev);

                } else if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
                    connect(btDev);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void connect(BluetoothDevice btDev) {
        UUID uuid = UUID.fromString(SPP_UUID);
        try {
            btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
            Log.d("BlueToothTestActivity", "开始连接...");
            btSocket.connect();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the
            // View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, info);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            // com.vise.common_base.utils.ToastUtil.showToast(mContext, info);
            Toast.makeText(mContext, info, Toast.LENGTH_LONG).show();
            System.out.println("your choose :" + info);
            System.out.println("your choose :" + address);
//            //选择了要连接的设备
//            Intent selectedIntent = new Intent(BluetoothTools.ACTION_SELECTED_DEVICE);
//            sendBroadcast(selectedIntent);
            finish();
        }
    };


    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothTools.ACTION_FOUND_DEVICE.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getExtras()
                        .get(BluetoothTools.DEVICE);
                // If it's already paired, skip it, because it's been listed
                // already
                int deviceType = device.getBluetoothClass()
                        .getMajorDeviceClass();
                // System.out.println("deviceType:"+deviceType);
                // computer蓝牙为256、phone 蓝牙为512、打印机蓝牙为1536等等
                String type = "";
                if (deviceType == 256) {
                    type = " - PC";
                }
                if (deviceType == 512) {
                    type = " - MOBILE";
                }
                if (deviceType == 1536) {
                    type = " - PRINTER";
                }
                // KLBGF2O2WC
                // AC:7B:A1:98:40:30-PC
                mNewDevicesArrayAdapter.add(device.getName() + type + "\n"
                        + device.getAddress());

                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(
                            R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
}