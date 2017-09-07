package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.model.TaskDetails;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ce_liang_manual);

        getAndSetDate();
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

        btn.setOnClickListener(new View.OnClickListener() {//手动跳转
            @Override
            public void onClick(View v) {
                insertToDB(taskId, td, taskname, String.valueOf(twetclsj.getText()));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getAndSetDate() {
        btn = (Button) findViewById(R.id.getbluedatamanuals);
        etshoulian = (EditText) findViewById(R.id.gcmanual);
        egaocheng = (EditText) findViewById(R.id.slmanual);
        twcllc = (TextView) findViewById(R.id.cllcmanual);
        twetcld = (TextView) findViewById(R.id.cldmanual);
        twetclr = (TextView) findViewById(R.id.clrmanual);
        twetclsj = (TextView) findViewById(R.id.clsjmanual);


        // 用intent1.getStringExtra()来得到activity1发过来的字符串。
        td = (TaskDetails) this.getIntent().getSerializableExtra("taskpass");
        taskId = (String) this.getIntent().getStringExtra("taskId_operation");
        taskname = (String) this.getIntent().getStringExtra("task_user");
        twcllc.setText(td.getMileageLabel());
        twetcld.setText(td.getPointLabel());
        twetclr.setText(taskname);

//        String date=new SimpleDateFormat("yyyy-MM-DD").format(new java.util.Date());

        twetclsj.setText(String.valueOf(""));


    }


    public void insertToDB(final String taskId, final TaskDetails td, final String taskname, final String time) {
        gc = String.valueOf(egaocheng.getText());
        sl = String.valueOf(etshoulian.getText());
        if (!egaocheng.equals(sl)) {
            new AlertDialog.Builder(context)
                    .setTitle("复合")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage("测量数据高程与历史记录不一致" + "\r\n" + "高程新：" + "" + "")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            demo(taskId, td, taskname, time);
                            //startActivity(new Intent(BaseActivity.this, MainActivity.class));
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();

        }

//                Toast.makeText(CeliangManualOperation.this, "保存失败", Toast.LENGTH_LONG).show();


    }

    public void demo(String taskId, TaskDetails td, String taskname, String time) {
        final SqliteUtils su = new SqliteUtils(this);
        if (taskId != null && gc != null && sl != null) {
            if (su.UpdateState(taskId, td, taskname, time, gc, sl) == 1) {
                //Toast.makeText(CeliangManualOperation.this, "保存成功", Toast.LENGTH_LONG).show();

                new AlertDialog.Builder(context)
                        .setTitle("保存结果")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage("测量数据保存成功")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                DLApplication.userName = "";
//                                finish();
                                Intent intent = new Intent();
                                intent.setClass(CeliangManualOperation.this, ShowTaskInfo.class);
                                intent.putExtra("State", true);
                                startActivity(intent);

                                // startActivity(new Intent(CeliangManualOperation.this, MainActivity.class));
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            } else {
                new AlertDialog.Builder(context)
                        .setTitle("保存结果")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage("测量数据保存失败")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//                                startActivity(new Intent(CeliangManualOperation.this, ShowTaskInfo.class));
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        } else {
            Toast.makeText(CeliangManualOperation.this, "高程,收敛不能为空", Toast.LENGTH_LONG).show();
        }
    }

}


