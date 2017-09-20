package com.dxc.mycollector;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.adapter.Adapter;
import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.model.User;

import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * About UserListAcitvity
 */
public class UserListAcitvity extends BaseActivity implements AdapterView.OnItemLongClickListener {
    TextView textView;
    String result = null;
    ListView usershow1;
    Adapter adapter;
    List<User> userList;
    public int MID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist_layout);
        usershow1 = (ListView) findViewById(R.id.usershow1);
        userList = SqliteUtils.getInstance(getApplicationContext()).loadUser();
        String rr = "";
        for (User user : userList) {
            rr += user.getuName() + "   --------------  " + user.getuPwd() + "    " +
                    user.getuRePwd() + "  " + user.getuPhone() + "  " + user.geturealName() + " " + user.getidCard() + "  "
                    + user.getuAddress() + "   " + user.getgongDian();
        }
        //给listview设置一个适配器
        adapter = new Adapter(getApplication(), userList);
        usershow1.setAdapter(adapter);
        //点击item
        usershow1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User userInfo = userList.get(position);//获取每一个item的位置
                Intent intent = new Intent(UserListAcitvity.this, UserDetailActivity.class);
                intent.putExtra("UserDetailData", userInfo);//用intent进行传值
                startActivity(intent);
            }
        });
        usershow1.setOnItemLongClickListener(this);//listview 长按点击事件
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
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ItemOnLongClick1();//Item 长按方式弹出菜单多选方式1
        return false;
    }
    private void ItemOnLongClick1() {
//注：setOnCreateContextMenuListener是与下面onContextItemSelected配套使用的
        usershow1.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "ShowPwd");
                menu.add(0, 1, 0, "Edit");
            }
        });
    }

    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        MID = (int) info.id;// 这里的info.id对应的就是数据库中_id的值
        switch (item.getItemId()) {
            case 0:
                User userInfo1 = userList.get(MID);
                // 添加操作
                Toast.makeText(UserListAcitvity.this, "your pwd is :" + userInfo1.getuPwd(), Toast.LENGTH_SHORT).show();
                break;
            case 1:
                User userInfo = userList.get(MID);
                Intent intent = new Intent(UserListAcitvity.this, UserDetail1Activity.class);
                intent.putExtra("UserDetailData", userInfo);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
}