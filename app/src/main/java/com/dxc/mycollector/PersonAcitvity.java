package com.dxc.mycollector;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dxc.mycollector.utils.HttpUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gospel on 2017/8/18.
 * About PersonWellcom
 */
public class PersonAcitvity extends BaseActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_homepage_layout);
        textView = (TextView) findViewById(R.id.textView4);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String result = HttpUtils.getJSONObjectString(textView.getText().toString());// HttpUtils.doPost(null, textView.getText().toString());
                    textView.setText(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}

