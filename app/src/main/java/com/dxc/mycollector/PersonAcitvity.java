package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by gospel on 2017/8/18.
 * About PersonWellcom
 */
public class PersonAcitvity extends BaseActivity {

    boolean isOpen = false;
    TextView textView;
    String result = null;
    ImageView mv;
    Context context;
    //    protected DrawerLayout drawerLayout;
    private ImageView measure;
    private ImageView data;
    private ImageView task;
    private ImageView safe;
    private ImageView add;
    private Dialog mDialog;
    private Dialog mWeiboDialog;
    private Button btn_show_weibo_loading;
    private Button btn_show_thrid_loading;
    //退出时的时间
    private long mExitTime;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    //Map<String, String> map = slist.get(2); // 例子而已，直接获取下标为2的值了，可以通过循环将list的值取出
                    textView.setText(result);//在handler中更新UI
                    break;
                case 1:
                    DialogThridUtils.closeDialog(mDialog);
                    WeiboDialogUtils.closeDialog(mWeiboDialog);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_homepage_layout);

        context = this;

        measure = (ImageView) findViewById(R.id.image1);
        data = (ImageView) findViewById(R.id.image2);
        task = (ImageView) findViewById(R.id.image3);
        safe = (ImageView) findViewById(R.id.image4);
        add = (ImageView) findViewById(R.id.image5);


        findViewById(R.id.clgl_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonAcitvity.this, ShowTaskInfo.class));
            }
        });
        findViewById(R.id.sjgl_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonAcitvity.this, UploadBlueToothFolder.class));
            }
        });
        findViewById(R.id.rwgl_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonAcitvity.this, ShowTaskInfo.class));
            }
        });
        findViewById(R.id.aqgl_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PersonAcitvity.this, "敬请期待......", Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.tjgl_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PersonAcitvity.this, "敬请期待......", Toast.LENGTH_LONG).show();
            }
        });


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_plus);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("我的信息");


//        mv = (ImageView) findViewById(R.id.left_imbt);
        findViewById(R.id.title_personal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isOpen) {
                    drawerLayout.closeDrawers();
                    isOpen = false;
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                    isOpen = true;
                }
            }
        });

        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }
    }
//按返回键到最后一个activity推出侧滑菜单
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if ((System.currentTimeMillis() - mExitTime) > 2000) {

                Object mHelperUtils;
                mExitTime = System.currentTimeMillis();
                drawerLayout.openDrawer(Gravity.LEFT);//侧滑菜单栏
                isOpen = true;//设置为打开状态

            } else {

                finish();

            }

            return true;

        }

        return super.onKeyDown(keyCode, event);

    }

}

