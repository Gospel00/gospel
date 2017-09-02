/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.app.ActionBar;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.MeasureData;
import com.dxc.mycollector.model.TaskDetails;

public class CeLiangActivity extends AppCompatActivity {

    private TextView etcllc;//测量里程
    private TextView etcld;//测量点
    private TextView etclr;//测量人
    private TextView etclsj;//测量时间
    private TextView etgc;//高程
    private TextView etsl;//收敛

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ce_liang_detail);
        etcllc = (TextView) findViewById(R.id.cllc);
        etcld = (TextView) findViewById(R.id.cld);
        etclr = (TextView) findViewById(R.id.clr);
        etclsj = (TextView) findViewById(R.id.clsj);
        etgc = (TextView) findViewById(R.id.gc);
        etsl = (TextView) findViewById(R.id.sl);
        Intent intent = getIntent();
        try {
            MeasureData measureData = (MeasureData) intent.getSerializableExtra("measureData");
            if (measureData == null) {
                TaskDetails detailDatas = (TaskDetails) intent.getSerializableExtra("detailDatas");
                etcllc.setText(detailDatas.getMileageLabel());
                etcld.setText(detailDatas.getPointLabel());
                etclr.setText(detailDatas.getSection());
                etclsj.setText(detailDatas.getDateTime());
                etgc.setText(detailDatas.getInitialValue());
                etsl.setText(detailDatas.getInitialValue());
            } else {
                etcllc.setText(measureData.getCllicheng());
                etcld.setText(measureData.getCldian());
                etclr.setText(measureData.getClren());
                etclsj.setText(measureData.getCltime());
                etgc.setText(measureData.getGaocheng());
                etsl.setText(measureData.getShoulian());
            }
        } catch (Exception d) {
            Logger l = new Logger();
            l.e("showCeLiang", String.valueOf(d));

        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);

        //必须加2句
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示

        //使用setText的方法对textview动态赋值
        ((TextView) findViewById(R.id.title_name)).setText("测量数据列表");
    }


}
