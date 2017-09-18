package com.dxc.mycollector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.dxc.mycollector.model.User;

public class UserDetailActivity extends AppCompatActivity {
    private TextView username;//真实姓名
    private TextView userphone;//电话号
    private TextView useridcard;//身份证号
    private TextView useraddress;//地址
    private TextView usergongdian;//工点
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        username = (TextView) findViewById(R.id.uname1);
        userphone = (TextView) findViewById(R.id.uphone1);
        useridcard = (TextView) findViewById(R.id.uidcard1);
        useraddress = (TextView) findViewById(R.id.uaddress1);
        usergongdian = (TextView) findViewById(R.id.ugongdian1);

        intent=getIntent();
        User u=(User)getIntent().getSerializableExtra("UserDetailData");

        if (intent!=null){
            username.setText(u.geturealName());
            userphone.setText(u.getuPhone());
            useridcard.setText(u.getidCard());
            useraddress.setText(u.getuAddress());
            usergongdian.setText(u.getgongDian());
        }


        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//??????
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("用户详情");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
