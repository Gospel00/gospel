/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.fragment.GasFragment;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.GasInfo;
import com.dxc.mycollector.utils.DateConver;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gospel on 2017/8/18.
 * About Register
 */
public class AddGasAcitvity extends BaseActivity {
    String TAG = AddGasAcitvity.class.getSimpleName();
    Context context;
    @BindView(R.id.eyhtvalue)
    EditText eyhtvalue;
    @BindView(R.id.jwvalue)
    EditText jwvalue;
    @BindView(R.id.yyhtvalue)
    EditText yyhtvalue;
    @BindView(R.id.lhtvalue)
    EditText lhtvalue;
    private Button button;
    private Dialog mWeiboDialog;//对话框
    private FragmentManager fmanager;
    private FragmentTransaction ftransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_gas_layout);
        ButterKnife.bind(this);
        context = this;
        button = (Button) findViewById(R.id.savegas);
        //保存录入结果
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitingDialog();//加载等待页面对话框方法
                GasInfo gasInfo = new GasInfo();
                gasInfo.setCarbonDioxide(eyhtvalue.getText().toString());
                gasInfo.setCarbonMonoxide(yyhtvalue.getText().toString());
                gasInfo.setMethane(jwvalue.getText().toString());
                gasInfo.setHydrogenSulfide(lhtvalue.getText().toString());
                gasInfo.setStatus("1");
                gasInfo.setCheckTime(DateConver.getStringDate());
                int isTure = SqliteUtils.getInstance(context).saveGasByManual(gasInfo);
                if (isTure == 1) {
                    new AlertDialog.Builder(context)
                            .setTitle("系统提示")
                            .setIcon(R.drawable.success_small)
                            .setMessage("气体检查结果保存成功!")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //结束本身
                                    //finish();
                                    //回到列表Fragment
                                    fmanager = getSupportFragmentManager();
                                    ftransaction = fmanager.beginTransaction();
                                    GasFragment gasFragment = new GasFragment();
                                    ftransaction.replace(R.id.sub_content, gasFragment);
                                    ftransaction.commit();
                                }
                            })
                            .show();
                } else {
                    Logger.e(TAG, "手动录入气体检查结果保存失败!");
                }
            }
        });

        /**
         * 自定义ActionBar
         */
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        // 使用setText的方法对textview动态赋值
        ((TextView)

                findViewById(R.id.title_name)).

                setText("手动录入气体检查结果");

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
}
