package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.User;
import com.dxc.mycollector.taskDownload.DownLoadManager;

/**
 * Created by gospel on 2017/8/18.
 * About Register
 */
public class RegisterAcitvity extends AppCompatActivity {
    String TAG = RegisterAcitvity.class.getSimpleName();
    Context context;
    private Button button;
    private Spinner provinceSpinner = null;  //省级（省、直辖市）
    private Spinner citySpinner = null;     //地级市
    private Spinner countySpinner = null;    //县级（区、县、县级市）
    ArrayAdapter<String> provinceAdapter = null;  //省级适配器
    ArrayAdapter<String> cityAdapter = null;    //地级适配器
    ArrayAdapter<String> countyAdapter = null;    //县级适配器
    static int provincePosition = 3;
    private EditText username;
    private EditText password;
    private EditText repassword;
    private EditText phone;
    private EditText adress;
    private EditText realname;
    private EditText idcard;
    private EditText gongdian;
    /* private RadioGroup sex;
     private RadioButton male;
     private RadioButton female;*/
    private Dialog mWeiboDialog;//对话框

    //省级选项值
    private String[] province = new String[]{"省份", "北京", "上海", "天津", "广东"};//,"重庆","黑龙江","江苏","山东","浙江","香港","澳门"};
    //地级选项值
    private String[][] city = new String[][]
            {
                    {"市"},
                    {"东城区", "西城区", "崇文区", "宣武区", "朝阳区", "海淀区", "丰台区", "石景山区", "门头沟区",
                            "房山区", "通州区", "顺义区", "大兴区", "昌平区", "平谷区", "怀柔区", "密云县",
                            "延庆县"},
                    {"长宁区", "静安区", "普陀区", "闸北区", "虹口区"},
                    {"和平区", "河东区", "河西区", "南开区", "河北区", "红桥区", "塘沽区", "汉沽区", "大港区",
                            "东丽区"},
                    {"广州", "深圳", "韶关" // ,"珠海","汕头","佛山","湛江","肇庆","江门","茂名","惠州","梅州",
                            // "汕尾","河源","阳江","清远","东莞","中山","潮州","揭阳","云浮"
                    }
            };

    //县级选项值
    private String[][][] county = new String[][][]
            {{{"区"}},
                    {   //北京
                            {"东单"}, {"人定湖北巷"}, {"天坛公园"}, {"三井社区"}, {"望京"}, {"北京大学"}, {"永定河"}, {"无"}, {"无"}, {"无"},
                            {"无"}, {"后沙峪"}, {"无"}, {"无"}, {"无"}, {"无"}, {"无"}, {"无"}
                    },
                    {    //上海
                            {"无"}, {"无"}, {"无"}, {"无"}, {"无"}
                    },
                    {    //天津
                            {"无"}, {"无"}, {"无"}, {"无"}, {"无"}, {"无"}, {"无"}, {"无"}, {"无"}, {"无"}
                    },
                    {    //广东
                            {"海珠区", "荔湾区", "越秀区", "白云区", "萝岗区", "天河区", "黄埔区", "花都区", "从化市", "增城市", "番禺区", "南沙区"}, //广州
                            {"宝安区", "福田区", "龙岗区", "罗湖区", "南山区", "盐田区"}, //深圳
                            {"武江区", "浈江区", "曲江区", "乐昌市", "南雄市", "始兴县", "仁化县", "翁源县", "新丰县", "乳源县"}  //韶关
                    }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        setSpinner();
        context = this;
        button = (Button) findViewById(R.id.register);
        username = (EditText) findViewById(R.id.siguername);
        password = (EditText) findViewById(R.id.sigpassword);
        repassword = (EditText) findViewById(R.id.sigrepassword);
        phone = (EditText) findViewById(R.id.sigphone);
        adress = (EditText) findViewById(R.id.sigaddress);
        realname = (EditText) findViewById(R.id.sigrealname);
        idcard = (EditText) findViewById(R.id.sigidcard);
        gongdian = (EditText) findViewById(R.id.siggongdian);
       /* sex = (RadioGroup) this.findViewById(R.id.radiogroup1);
        male = (RadioButton) this.findViewById(R.id.male);
        female = (RadioButton) this.findViewById(R.id.female);*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User newUser = GetText();
                waitingDialog();//加载等待页面对话框方法

                if (!isEmpty(newUser)) {
//                boolean isTure = userServices.register(user);
                    int isTure = SqliteUtils.getInstance(context).saveUser(newUser);
                    if (isTure == 1) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        new AlertDialog.Builder(context)
                                .setTitle("系统提示")
                                .setIcon(R.drawable.success_small)
                                .setMessage("恭喜您注册成功!")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Logger.i(TAG, username.getText().toString() + " register success.");
                                        startActivity(new Intent(RegisterAcitvity.this, MainActivity.class));
                                        finish();
                                    }
                                })
                                .show();
                    } else if (isTure == -1) {
                        Toast.makeText(RegisterAcitvity.this, "该用户已存在", Toast.LENGTH_SHORT).show();
                        Logger.i(TAG, username.getText().toString() + " user name exist.register failed.");
                    } else {
                        Toast.makeText(RegisterAcitvity.this, "注册失败", Toast.LENGTH_SHORT).show();
                        Logger.i(TAG, username.getText().toString() + "register failed.");
                    }
                }

            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        // 使用setText的方法对textview动态赋值
        ((TextView) findViewById(R.id.title_name)).setText("注册");

        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //DialogThridUtils.closeDialog(mDialog);
                    if (context != null && mWeiboDialog != null) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                    }
                    break;
            }
        }
    };

    //定义加载等待页面方法
    public void waitingDialog() {
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(context, "加载中...");//加载对话框
        mHandler.sendEmptyMessageDelayed(1, 1000);//处理消息
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public User GetText() {
        User user = new User();
        user.setuName(username.getText().toString().trim());
        user.setuPwd(password.getText().toString().trim());
        user.setuPhone(phone.getText().toString().trim());
        user.setuAddress(adress.getText().toString().trim());
        user.setuRePwd(repassword.getText().toString().trim());
        user.setuAddress(adress.getText().toString().trim());
        //user.setuSex(sex.getCheckedRadioButtonId() + "");
        user.seturealName(realname.getText().toString().trim());
        user.setidCard(idcard.getText().toString().trim());
        user.setgongDian(gongdian.getText().toString().trim());
        return user;
    }

    private boolean isEmpty(User user) {
//        GetText();
        Log.i("register", "pao ");
        if (TextUtils.isEmpty(user.getuName())) {
            Toast.makeText(RegisterAcitvity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(user.getuPwd())) {
            Toast.makeText(RegisterAcitvity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (user.getuPwd().length() < 0) {
            Toast.makeText(RegisterAcitvity.this, "请输入6位密码", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(user.getuRePwd())) {
            Toast.makeText(RegisterAcitvity.this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (!user.getuPwd().equals(user.getuRePwd())) {
            Toast.makeText(RegisterAcitvity.this, "确认密码和原密码输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(user.getuPhone())) {
            Toast.makeText(RegisterAcitvity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(user.geturealName())) {
            Toast.makeText(RegisterAcitvity.this, "真实姓名不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(user.getidCard())) {
            Toast.makeText(RegisterAcitvity.this, "18位证件号不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(user.getuAddress())) {
            Toast.makeText(RegisterAcitvity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
            return true;
        } else if (TextUtils.isEmpty(user.getgongDian())) {
            Toast.makeText(RegisterAcitvity.this, "您的工点不能为空", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void setSpinner() {
        provinceSpinner = (Spinner) findViewById(R.id.spin_province);
        citySpinner = (Spinner) findViewById(R.id.spin_city);
        countySpinner = (Spinner) findViewById(R.id.spin_county);

        //绑定适配器和值
        provinceAdapter = new ArrayAdapter<String>(RegisterAcitvity.this,
                android.R.layout.simple_spinner_item, province);
        provinceSpinner.setAdapter(provinceAdapter);
        provinceSpinner.setSelection(0, true);  //设置默认选中项，此处为默认选中第4个值

        cityAdapter = new ArrayAdapter<String>(RegisterAcitvity.this,
                android.R.layout.simple_spinner_item, city[0]);
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setSelection(0, true);  //默认选中第0个

        countyAdapter = new ArrayAdapter<String>(RegisterAcitvity.this,
                android.R.layout.simple_spinner_item, county[3][0]);
        countySpinner.setAdapter(countyAdapter);
        countySpinner.setSelection(0, true);


        //省级下拉框监听
        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // 表示选项被改变的时候触发此方法，主要实现办法：动态改变地级适配器的绑定值
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //position为当前省级选中的值的序号

                //将地级适配器的值改变为city[position]中的值
                cityAdapter = new ArrayAdapter<String>(
                        RegisterAcitvity.this, android.R.layout.simple_spinner_item, city[position]);
                // 设置二级下拉列表的选项内容适配器
                citySpinner.setAdapter(cityAdapter);
                provincePosition = position;    //记录当前省级序号，留给下面修改县级适配器时用
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });


        //地级下拉监听
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                countyAdapter = new ArrayAdapter<String>(RegisterAcitvity.this,
                        android.R.layout.simple_spinner_item, county[provincePosition][position]);
                countySpinner.setAdapter(countyAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }
}
