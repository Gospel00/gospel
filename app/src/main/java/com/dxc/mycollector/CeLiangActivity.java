package com.dxc.mycollector;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.MeasureData;

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

        Intent intent = getIntent();
        try {
            MeasureData measureData = (MeasureData) intent.getSerializableExtra("measureData");


            etcllc = (TextView) findViewById(R.id.cllc);
            etcllc.setText(measureData.getCllicheng());
            etcld = (TextView) findViewById(R.id.cld);
            etcld.setText(measureData.getCldian());
            etclr = (TextView) findViewById(R.id.clr);
            etclr.setText(measureData.getClren());
            etclsj = (TextView) findViewById(R.id.clsj);
            etclsj.setText(measureData.getCltime());
            etgc = (TextView) findViewById(R.id.gc);
            etgc.setText(measureData.getGaocheng());
            etsl = (TextView) findViewById(R.id.sl);
            etsl.setText(measureData.getShoulian());
        } catch (Exception d) {
            Logger l = new Logger();
            l.e("showCeLiang", String.valueOf(d));

        }
    }


}
