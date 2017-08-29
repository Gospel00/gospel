package com.dxc.mycollector;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.dxc.mycollector.utils.HttpUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by gospel on 2017/8/18.
 * About PersonWellcom
 */
public class PersonAcitvity extends BaseActivity {
    TextView textView;
    String result = null;
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
        textView = (TextView) findViewById(R.id.textView4);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {//创建子线程进行网络访问的操作
                    public void run() {
                        try {
                            result = HttpUtils.getJSONObjectString(textView.getText().toString());// HttpUtils.doPost(null, textView.getText().toString());
                            handler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

}

