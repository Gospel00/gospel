package com.dxc.mycollector;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dxc.mycollector.utils.HttpUtils;

/**
 * Created by gospel on 2017/8/18.
 * About PersonWellcom
 */
public class PersonAcitvity extends BaseActivity {
    TextView textView;
    String result = null;
   // private Button button;
   private TextView titleName;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    //Map<String, String> map = slist.get(2); // 例子而已，直接获取下标为2的值了，可以通过循环将list的值取出
                    textView.setText(result);//在handler中更新UI
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
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.index_actionbar);

//必须加2句
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示

//可动态改变标题内容，即获取到标题栏的控件

        titleName = (TextView) findViewById(R.id.title_name);

        //textView = (TextView) findViewById(R.id.textView4);
       // button=(Button)findViewById(R.id.button);
       /* findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(PersonAcitvity.this, CeLiangActivity.class));
                new Thread() {//创建子线程进行网络访问的操作
                    public void run() {
                        try {
                            result = HttpUtils.getJSONObjectString("",textView.getText().toString());// HttpUtils.doPost(null, textView.getText().toString());
                            handler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });*/
    }

}

