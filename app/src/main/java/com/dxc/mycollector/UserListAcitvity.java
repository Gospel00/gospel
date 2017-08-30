package com.dxc.mycollector;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dxc.mycollector.dbhelp.SqliteDB;
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
                List<User> userList = SqliteDB.getInstance(getApplicationContext()).loadUser();
                String rr = "";
                for (User user : userList) {
                    rr += user.getuName() + "   --------------  " + user.getuPwd() + "\n";
                }
                textView.setText(rr);
            }
        });
    }

}

