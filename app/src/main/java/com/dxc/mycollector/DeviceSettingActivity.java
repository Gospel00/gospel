package com.dxc.mycollector;

import android.app.ActionBar;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DeviceSettingActivity extends AppCompatActivity {
private TextView total;
    private TextView surveyor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_setting);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);

        //必须加2句
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示

        //使用setText的方法对textview动态赋值
        ((TextView) findViewById(R.id.title_name)).setText("设置");
    }
}
