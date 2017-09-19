package com.dxc.mycollector;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dxc.mycollector.adapter.Adapter;
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
    Button userbtn;
    ListView usershow1;
    Adapter adapter;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist_layout);
        usershow1 = (ListView) findViewById(R.id.usershow1);
        userList = SqliteUtils.getInstance(getApplicationContext()).loadUser();
        adapter = new Adapter(getApplication(), userList);
        usershow1.setAdapter(adapter);
        usershow1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User userInfo = userList.get(position);
                Intent intent = new Intent(UserListAcitvity.this, UserDetailActivity.class);
                intent.putExtra("UserDetailData",userInfo);
                startActivity(intent);
            }
        });


        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//??????
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("用户列表");

        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

