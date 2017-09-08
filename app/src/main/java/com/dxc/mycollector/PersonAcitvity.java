package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
        measure = (ImageView) findViewById(R.id.image1);
        data = (ImageView) findViewById(R.id.image2);
        task = (ImageView) findViewById(R.id.image3);
        safe = (ImageView) findViewById(R.id.image4);
        add = (ImageView) findViewById(R.id.image5);

        findViewById(R.id.image1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonAcitvity.this, ShowTaskInfo.class));
            }
        });
        findViewById(R.id.image2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonAcitvity.this, UploadBlueToothFolder.class));
            }
        });
        findViewById(R.id.image3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PersonAcitvity.this, ShowTaskInfo.class));
            }
        });
        findViewById(R.id.image4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PersonAcitvity.this, "敬请期待......", Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.image5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PersonAcitvity.this, "敬请期待......", Toast.LENGTH_LONG).show();
            }
        });
//        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.menu_activity_first, null);
//        textView = (TextView) findViewById(R.id.textView4);
        // button=(Button)findViewById(R.id.button);
//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    String resultJsonupload = HttpUtils.postJSONObjectString("http://106.38.157.46:48080/restcenter/measureTaskService/feedbackTask", null);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                DownLoadManager downLoadManager = new DownLoadManager(PersonAcitvity.this);
//                downLoadManager.uploadMeasure(null);
        // startActivity(new Intent(PersonAcitvity.this, CeLiangActivity.class));
//                new Thread() {//创建子线程进行网络访问的操作
//                    public void run() {
//                        try {
//                            result = HttpUtils.getJSONObjectString("",textView.getText().toString());// HttpUtils.doPost(null, textView.getText().toString());
//                            handler.sendEmptyMessage(0);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }.start();
//            }
//        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_plus);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("我的信息");


        mv = (ImageView) findViewById(R.id.left_imbt);
        mv.setOnClickListener(new View.OnClickListener() {
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

    protected void backToHome() {
//        Intent intent = new Intent(this, a.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        this.startActivity(intent);
//        finish();finish
    }

    private void setSimulateClick(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//        if(keyCode == KeyEvent.KEYCODE_BACK ) {
//            RelativeLayout relativeLayout= (RelativeLayout) findViewById(R.id.item_r);
//            if(relativeLayout.getVisibility()==View.VISIBLE){
//                //当左边的菜单栏是可见的，则关闭
//                drawerLayout.closeDrawer(relativeLayout);
//            } else {
//                finish();
//            }
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

}

