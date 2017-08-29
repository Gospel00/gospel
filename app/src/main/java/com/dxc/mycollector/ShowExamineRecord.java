package com.dxc.mycollector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dxc.mycollector.taskDownload.DownLoadManager;
import com.dxc.mycollector.taskDownload.DownLoadService;
import com.dxc.mycollector.taskDownload.TaskInfo;
import com.dxc.mycollector.taskDownload.adapter.ListAdapter;


/**
 * Created by gospel on 2017/8/21.
 * About ShowExamineRecord
 */

public class ShowExamineRecord extends BaseActivity {
    private ListView listview;
    private EditText nameText;
    private EditText urlText;
    private ListAdapter adapter;
    private Button taskAdd;

    /*使用DownLoadManager时只能通过DownLoadService.getDownLoadManager()的方式来获取下载管理器，不能通过new DownLoadManager()的方式创建下载管理器*/
    private DownLoadManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.show_examine_record_main_layout);

        taskAdd = (Button) this.findViewById(R.id.taskAdd);
        listview = (ListView) this.findViewById(R.id.listView);
        //添加下载任务
        taskAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddExamineRecord.class));
            }
        });

    }
}
