package com.dxc.mycollector;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.model.User;

import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * About UserListAcitvity
 */
public class UserListAcitvity extends BaseActivity {
    TextView textView;
    String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist_layout);
        textView = (TextView) findViewById(R.id.usershow1);
        findViewById(R.id.userbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<User> userList = SqliteUtils.getInstance(getApplicationContext()).loadUser();
                String rr = "";
                for (User user : userList) {
                    rr += user.getuName() + "   --------------  " + user.getuPwd() + "\n";
                }
                textView.setText(rr);
            }
        });

//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.setCustomView(R.layout.actionbar);
//
//        //必须加2句
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
//        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
//
//        //使用setText的方法对textview动态赋值
//        ((TextView) findViewById(R.id.title_name)).setText("用户列表");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//??????
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("用户列表");

        //以下代码用于去除阴影
        if(Build.VERSION.SDK_INT>=21){
            getSupportActionBar().setElevation(0);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

