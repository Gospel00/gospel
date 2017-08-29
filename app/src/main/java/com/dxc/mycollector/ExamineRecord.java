package com.dxc.mycollector;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * Created by gospel on 2017/8/21.
 * About AddExamineRecord
 */

public class ExamineRecord extends BaseActivity {
    private Spinner examineItem = null;  //隧道施工安全检查项目
    ArrayAdapter<String> examineItemAdapter = null;  //隧道施工安全检查项目适配器

    //省级选项值
    private String[] item = new String[]{"---请选择---", "1、超前地质预报作业安全", "2、全断面法开挖作业安全"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.add_examine_record_main_layout);
        setSpinner();
    }

    /**
     * 隧道施工安全检查项目
     */
    private void setSpinner() {
        examineItem = (Spinner) findViewById(R.id.examine_item);

        //绑定适配器和值
        examineItemAdapter = new ArrayAdapter<String>(ExamineRecord.this,
                android.R.layout.simple_spinner_item, item);
        examineItem.setAdapter(examineItemAdapter);

        //选择项目
        examineItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // 表示选项被改变的时候触发此方法，主要实现办法：动态改变地级适配器的绑定值
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //position为当前省级选中的值的序号
                Toast.makeText(getApplicationContext(), item[position], Toast.LENGTH_LONG);
                Log.i("examine", "onItemSelected: " + item[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }
}
