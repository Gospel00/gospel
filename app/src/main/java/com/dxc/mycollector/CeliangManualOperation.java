package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.format.Time;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.model.TaskDetails;
import com.dxc.mycollector.model.TaskInfo;
import com.dxc.mycollector.utils.CalcUtils;
import com.dxc.mycollector.utils.DateConver;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by zhangruw on 9/6/2017.
 */

public class CeliangManualOperation extends BaseActivity {
    EditText etshoulian;
    EditText egaocheng;

    TextView twcllc;//测量里程
    TextView twetcld;//测量点
    TextView twetclr;//测量人
    TextView twetclsj;//测量时间
    Button btn;
    TaskDetails td;
    String taskId;
    String taskname;
    String sl = "";
    String gc = "";
    String taskType = "";//测量类型T0101拱顶，T0102收敛

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ce_liang_manual);

        taskType = ((String) this.getIntent().getStringExtra("tasktypes"));
        td = (TaskDetails) this.getIntent().getSerializableExtra("taskpass");
        taskId = (String) this.getIntent().getStringExtra("taskId_operation");
        taskname = (String) this.getIntent().getStringExtra("task_user");

        getAndSetDate();

        btn.setOnClickListener(new View.OnClickListener() {//手动跳转
            @Override
            public void onClick(View v) {
                insertToDB(taskId, td, taskname, String.valueOf(twetclsj.getText()));
            }
        });

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//??????
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("录入测量数据");
        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getAndSetDate() {
        btn = (Button) findViewById(R.id.getbluedatamanuals);
        egaocheng = (EditText) findViewById(R.id.gcmanual);
        twcllc = (TextView) findViewById(R.id.cllcmanual);
        twetcld = (TextView) findViewById(R.id.cldmanual);
        twetclr = (TextView) findViewById(R.id.clrmanual);
        twetclsj = (TextView) findViewById(R.id.clsjmanual);

        // 用intent1.getStringExtra()来得到activity1发过来的字符串。
        twcllc.setText(td.getMileageLabel());
        twetcld.setText(td.getPointLabel());
        twetclr.setText(taskname);
        twetclsj.setText(DateConver.getStringDate());
    }

    public void insertToDB(final String taskId, final TaskDetails td, final String taskname, final String time) {
        gc = String.valueOf(egaocheng.getText());
        if (gc == null || gc.length() == 0) {
            new AlertDialog.Builder(context)
                    .setTitle("系统提示")
                    .setIcon(R.drawable.warn_small)
                    .setMessage("测量结果不能为空!\n初始值：" + td.getInitialValue())
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle("系统提示")
                .setIcon(R.drawable.warn_small)
                .setMessage("本次测量： " + gc + "   " + "\n初始值： " + td.getInitialValue() + "\n" + "本次测量与初始值差：" +CalcUtils.sub(Double.parseDouble(gc), Double.parseDouble(td.getInitialValue())))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertDB(taskId, td, taskname, time);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    //只管gc这个字段，因为每次测量保存只有一个测量值，现在都用gc这个字段存储
    public void insertDB(String taskId, final TaskDetails td, String taskname, String time) {
        final SqliteUtils su = new SqliteUtils(this);
        if (taskId != null && gc != null) {
            if (su.saveCustomMeasure(taskId, td, taskname, time, gc, "0") == 1) {
                new AlertDialog.Builder(context)
                        .setTitle("系统提示")
                        .setIcon(R.drawable.success_small)
                        .setMessage("测量数据保存成功")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //更新任务状态
                                uhandler.sendEmptyMessage(1);
                            }
                        })
                        .show();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("系统提示")
                        .setIcon(R.drawable.bedefeated_small)
                        .setMessage("测量数据保存失败")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        } else {
            Toast.makeText(CeliangManualOperation.this, "高程,收敛不能为空", Toast.LENGTH_LONG).show();
        }
    }

    private Handler uhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    //上传成功，更新本地数据上传状态
                    int result = SqliteUtils.getInstance(context).updateTaskStatus(td.getPointId());
                    if (result > 0) {
                        new AlertDialog.Builder(context)
                                .setTitle("系统提示")
                                .setIcon(R.drawable.success_small)
                                .setMessage("恭喜您完成本次测量任务，请在数据管理查看并上传测量结果。")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!ShowTaskInfo.instance.isFinishing())
                                            ShowTaskInfo.instance.finish();
                                        Intent intent = new Intent();
                                        intent.setClass(CeliangManualOperation.this, ShowTaskInfo.class);
                                        intent.putExtra("State", true);
                                        intent.putExtra("potid", td.getPointId());
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
}


