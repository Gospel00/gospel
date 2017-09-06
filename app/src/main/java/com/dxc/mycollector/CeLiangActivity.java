/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dxc.mycollector.bluetooth.BlueToothListActivity;
import com.dxc.mycollector.bluetooth.BluetoothManager;
import com.dxc.mycollector.bluetooth.BluetoothTools;
import com.dxc.mycollector.bluetooth.DeviceListActivity;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.MeasureData;
import com.dxc.mycollector.model.TaskDetails;
import com.dxc.mycollector.model.TaskInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.dxc.mycollector.bluetooth.BluetoothManager.turnOnBluetooth;

public class CeLiangActivity extends BaseActivity {
    String TAG = CeLiangActivity.class.getSimpleName();
    private TextView etcllc;//测量里程
    private TextView etcld;//测量点
    private TextView etclr;//测量人
    private TextView etclsj;//测量时间
    private TextView etgc;//高程
    private TextView etsl;//收敛
    private Button button;//连接设备
    private Button datalist;//获取蓝牙数据
    private Button buttonmaual;//连接设备
    private  String UserName="";//用户名
    TaskDetails detailDatas;

    /**
     * 自定义的打开 Bluetooth 的请求码，与 onActivityResult 中返回的 requestCode 匹配。
     */
    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;

    /**
     * Bluetooth 设备可见时间，单位：秒。
     */
    private static final int BLUETOOTH_DISCOVERABLE_DURATION = 250;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private StringBuffer mOutStringBuffer;
    private BluetoothDevice device;
    public static String message;
    Intent intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ce_liang_detail);


        button = (Button) findViewById(R.id.getbluedata);
       //ZRW datalist = (Button) findViewById(R.id.getbluedatalist);
        etcllc = (TextView) findViewById(R.id.cllc);
        etcld = (TextView) findViewById(R.id.cld);
        etclr = (TextView) findViewById(R.id.clr);
        etclsj = (TextView) findViewById(R.id.clsj);
        etgc = (TextView) findViewById(R.id.gc);
        etsl = (TextView) findViewById(R.id.sl);
        buttonmaual = (Button) findViewById(R.id.turntomanual);
        intent = getIntent();
        try {
            MeasureData measureData = (MeasureData) intent.getSerializableExtra("measureData");
            if (measureData == null) {
                TaskDetails detailDatas = (TaskDetails) intent.getSerializableExtra("detailDatas");

                etcllc.setText(detailDatas.getMileageLabel());
                etcld.setText(detailDatas.getPointLabel());
                etclr.setText(detailDatas.getSection());
                UserName=detailDatas.getSection();
                etclsj.setText(detailDatas.getDateTime());
                etgc.setText(detailDatas.getInitialValue());
                etsl.setText(detailDatas.getInitialValue());
            } else {
                etcllc.setText(measureData.getCllicheng());
                etcld.setText(measureData.getCldian());
                etclr.setText(measureData.getClren());
                UserName=measureData.getClren();
                etclsj.setText(measureData.getCltime());
                etgc.setText(measureData.getGaocheng());
                etsl.setText(measureData.getShoulian());
            }
        } catch (Exception d) {
            Logger l = new Logger();
            l.e("showCeLiang", String.valueOf(d));

        }

        buttonmaual.setOnClickListener(new View.OnClickListener() {//手动跳转
            @Override
            public void onClick(View v) {
                detailDatas = (TaskDetails) intent.getSerializableExtra("detailDatas");
                String taskId = (String) intent.getStringExtra("taskId");

                Bundle bundle = new Bundle();
                bundle.putSerializable("taskpass", detailDatas);
                bundle.putSerializable("taskId_operation", taskId);
                bundle.putSerializable("task_user",UserName );
                Intent intents  = new Intent(CeLiangActivity.this, CeliangManualOperation.class);
                intents.putExtras(bundle);
                startActivity(intents);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if ((BluetoothManager.isBluetoothSupported())
//                        && (!BluetoothManager.isBluetoothEnabled())) {
//                    turnOnBluetooth();
//                } else {
                //开始搜索
                Intent serverIntent = new Intent(CeLiangActivity.this, BlueToothListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//                }
            }
        });

        // 注册Receiver来获取蓝牙设备相关的结果
        IntentFilter intent1 = new IntentFilter();
        intent1.addAction(BluetoothDevice.ACTION_FOUND);// 用BroadcastReceiver来取得搜索结果
        intent1.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intent1.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intent1.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(searchDevices, intent1);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//??????
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("测量详情");

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

    /**
     * 弹出系统弹框提示用户打开 Bluetooth
     */
    public void turnOnBluetooth() {
        // 请求打开 Bluetooth
        Intent requestBluetoothOn = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);

        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
        requestBluetoothOn
                .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

        // 设置 Bluetooth 设备可见时间
        requestBluetoothOn.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                BLUETOOTH_DISCOVERABLE_DURATION);

        // 请求开启 Bluetooth
        this.startActivityForResult(requestBluetoothOn,
                REQUEST_CODE_BLUETOOTH_ON);
    }

    @Override
    protected void onStop() {
        //关闭后台Service
        Intent startService = new Intent(BluetoothTools.ACTION_STOP_SERVICE);
        sendBroadcast(startService);

//        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
//                    //显示在按钮上
                    button.setText(button.getText() + ":" + address);
                   //ZRW datalist.setVisibility(View.VISIBLE);
//                    // Get the BLuetoothDevice object
//                    device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
//                    Intent selectDeviceIntent = new Intent(BluetoothTools.ACTION_SELECTED_DEVICE);//选择动作
//                    selectDeviceIntent.putExtra(BluetoothTools.DEVICE, device);
//                    sendBroadcast(selectDeviceIntent);
                }
                break;
//            case REQUEST_CODE_BLUETOOTH_ON:
//                // When DeviceListActivity returns with a device to connect
//                if (resultCode == BLUETOOTH_DISCOVERABLE_DURATION) {
//                    ///开始搜索
//                    Intent serverIntent = new Intent(CeLiangActivity.this, DeviceListActivity.class);
//                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//                } else {
//                    Toast.makeText(this, "用户拒绝开启蓝牙", Toast.LENGTH_SHORT).show();
//                    Logger.i(TAG, "用户拒绝开启蓝牙");
//                }
//
//                break;
        }
    }

    private BroadcastReceiver searchDevices = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            Object[] lstName = b.keySet().toArray();

            // 显示所有收到的消息及其细节
            for (int i = 0; i < lstName.length; i++) {
                String keyName = lstName[i].toString();
                Logger.e(keyName, String.valueOf(b.get(keyName)));
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
//                    adtDevices.notifyDataSetChanged();
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
                    default:
                        break;
                }
            }

        }
    };

    private void connect(BluetoothDevice btDev) {
        UUID uuid = UUID.fromString(SPP_UUID);
        try {
            btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
            Logger.i(TAG, "开始连接...");
            btSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 选择行
     */
    private void selectItem() {
//        TaskInfo taskInfo = listtasks.get(position);
//        TaskDetails detailDatas = taskInfo.getTaskDetail();
        String[] strarr = {"蓝牙1", "蓝牙2"};
//        strarr[0] = detailDatas.getProName() + "-" + detailDatas.getMileageLabel() + "-" + detailDatas.getPointLabel();
        new AlertDialog.Builder(this)
                .setTitle("测量任务列表")
                .setItems(strarr, null)
                .setNegativeButton("确定", null)
                .show();
    }
}
