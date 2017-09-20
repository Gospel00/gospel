package com.dxc.mycollector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.model.User;

public class UserDetail1Activity extends BaseActivity {
    private Button save;
    private TextView username;//真实姓名
    private TextView userphone;//电话号
    private TextView useridcard;//身份证号
    private TextView useraddress;//地址
    private TextView usergongdian;//工点
    private Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail1);
        username = (TextView) findViewById(R.id.uname2);
        userphone = (TextView) findViewById(R.id.uphone2);
        useridcard = (TextView) findViewById(R.id.uidcard2);
        useraddress = (TextView) findViewById(R.id.uaddress2);
        usergongdian = (TextView) findViewById(R.id.ugongdian2);
        intent1 = getIntent();
        //定义一个接收UserDetailData值的方法
        final User u = (User) getIntent().getSerializableExtra("UserDetailData");
        //给每一个控件赋值
        if (intent1 != null) {
            username.setText(u.geturealName());
            userphone.setText(u.getuPhone());
            useridcard.setText(u.getidCard());
            useraddress.setText(u.getuAddress());
            usergongdian.setText(u.getgongDian());
        }

        save = (Button) findViewById(R.id.save);
        //保存修改过的信息
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 1、从布局（页面）拿到用户修改的字段信息
                 * 2、将拿到的信息封装成一个新的User对象
                 * 3、将User对象保存到数据库
                 */
                //封装User对象
                User users = new User();
                users.setId(u.getId());
                users.setuPhone(userphone.getText().toString());
                users.setidCard(useridcard.getText().toString());
                users.setuAddress(useraddress.getText().toString());
                users.setgongDian(usergongdian.getText().toString());
                //保存修改的用户信息
                int isTure = SqliteUtils.getInstance(getApplicationContext()).updateUserInfo(users);
                if (isTure == 1) {
                    new AlertDialog.Builder(context)
                            .setTitle("系统提示")
                            .setIcon(R.drawable.success_small)
                            .setMessage("用户信息修改成功")
                            .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(UserDetail1Activity.this, UserDetailActivity.class));
                                }
                            })
                            .show();
                } else if (isTure == 0) {
                    AlertDialog show = new AlertDialog.Builder(context)
                            .setTitle("系统提示")
                            .setIcon(R.drawable.bedefeated_small)
                            .setMessage("用户信息修改失败")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
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
        ((TextView) findViewById(R.id.title_name)).setText("个人信息修改");
    }
    //点击标题栏的返回键返回
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
