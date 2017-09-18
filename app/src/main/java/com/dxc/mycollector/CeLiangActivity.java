/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.base.DLApplication;
import com.dxc.mycollector.bluetooth.BlueToothListActivity;
import com.dxc.mycollector.bluetooth.DeviceListActivity;
import com.dxc.mycollector.model.TaskDetails;

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
    TaskDetails detailDatas;
    Intent intent;
    public static CeLiangActivity ceLiangActivityinstance;

    private static final int REQUEST_CONNECT_DEVICE = 1;
    public static String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ce_liang_detail);

        ceLiangActivityinstance = this;

        button = (Button) findViewById(R.id.getbluedata);
        etcllc = (TextView) findViewById(R.id.cllc);
        etcld = (TextView) findViewById(R.id.cld);
        etclr = (TextView) findViewById(R.id.clr);
        etclsj = (TextView) findViewById(R.id.clsj);
        etgc = (TextView) findViewById(R.id.gc);
        //手动录入
        buttonmaual = (Button) findViewById(R.id.turntomanual);

        intent = getIntent();

        //接收任务信息
        detailDatas = (TaskDetails) intent.getSerializableExtra("detailDatas");

        etcllc.setText(detailDatas.getMileageLabel());
        etcld.setText(detailDatas.getPointLabel());
        etclr.setText(DLApplication.userName != null ? DLApplication.userName : android.os.Build.MODEL);
        etclsj.setText(detailDatas.getDateTime());
        etgc.setText(detailDatas.getInitialValue());

        //手动录入测量数据
        buttonmaual.setOnClickListener(new View.OnClickListener() {//手动跳转
            @Override
            public void onClick(View v) {
                String taskId = (String) intent.getStringExtra("taskId");
                String typeNum = (String) intent.getStringExtra("tasktypes");
                String returnStr = String.valueOf(detailDatas.getPointLabel());
                Bundle bundle = new Bundle();
                bundle.putSerializable("taskpass", detailDatas);
                bundle.putSerializable("taskId_operation", taskId);
                bundle.putSerializable("task_user", DLApplication.userName != null ? DLApplication.userName : android.os.Build.MODEL);
                bundle.putSerializable("tasktypes", typeNum);
                Intent intents = new Intent(CeLiangActivity.this, CeliangManualOperation.class);
                intents.putExtras(bundle);
                startActivity(intents);
                finish();
            }
        });

        //蓝牙读取测量数据
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始搜索
                Intent serverIntent = new Intent(CeLiangActivity.this, BlueToothListActivity.class);
                Bundle bundle = new Bundle();
                String taskId = intent.getStringExtra("taskId");
                bundle.putSerializable("taskId_ly", taskId);
                bundle.putSerializable("taskDetails", detailDatas);
                serverIntent.putExtras(bundle);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//??????
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("量测详情");

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    String restr = data.getExtras()
                            .getString("result");
//                    if (restr != null && "取消配对".equals(restr))
//                        button.setText("蓝牙连接读数\n" + address);
                    if (restr != null) {
                        Toast.makeText(context, restr, Toast.LENGTH_LONG).show();
//                        Logger.i(TAG, restr);
                    }
                    if (address != null) {
                        button.setText("蓝牙连接读数\n" + address);
                    }
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
}
