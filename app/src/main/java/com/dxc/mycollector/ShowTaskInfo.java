/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.TaskInfo;
import com.dxc.mycollector.taskDownload.adapter.ListAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gospel on 2017/8/21.
 * About ShowExamineRecord
 */

public class ShowTaskInfo extends BaseActivity {
    private ListView listview;
    private ListAdapter adapter;
    private Button taskAdd;
    Context context;
    List<TaskInfo> listtasks = null;

    /*使用DownLoadManager时只能通过DownLoadService.getDownLoadManager()的方式来获取下载管理器，不能通过new DownLoadManager()的方式创建下载管理器*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.task_download_main_layout);
        context = this;
        taskAdd = (Button) this.findViewById(R.id.add_task);
        listview = (ListView) this.findViewById(R.id.task_listView);
        //新增安全检查记录
        taskAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), AddExamineRecord.class));
                Toast.makeText(context, "insert.......", Toast.LENGTH_LONG);
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskType("T0101");
                taskInfo.setMeasureType("T0201");
                int result = SqliteUtils.getInstance(context).saveTaskInfo(taskInfo);
                if (result == 1) {
                    Logger.i(TAG, "insert taskinfo success");
                }
                startActivity(new Intent(context, CeLiangActivity.class));
            }
        });
        //获取已经下载的任务信息
        getAllTasks();
        //初始化ListView
        initDrawerList();
    }

    private void getAllTasks() {
        listtasks = new ArrayList<>();
        List<TaskInfo> alltask = SqliteUtils.getInstance(this).loadTasks();
        for (TaskInfo taskinfo : alltask) {
            listtasks.add(taskinfo);
            Log.i(TAG, "getAllTasks: " + taskinfo.getTaskId() + "-" + taskinfo.getTaskType());
        }
    }

    private void initDrawerList() {
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Holder holder = null;
                if (convertView == null) {
                    holder = new Holder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.task_download_list_item_layout, null);
                    holder.fileName = (TextView) convertView.findViewById(R.id.show_task_name);
                    Button upbtn = (Button) convertView.findViewById(R.id.kaishicl);
                    convertView.setTag(holder);
                    upbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ShowTaskInfo.this, "接口正在开发中...", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    holder = (Holder) convertView.getTag();
                }
                holder.fileName.setText(listtasks.get(position).getTaskId() + "-" + listtasks.get(position).getTaskType());
                return convertView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return listtasks.get(position);
            }

            @Override
            public int getCount() {
                return listtasks.size();
            }
        };
        listview.setAdapter(adapter);
    }

    static class Holder {
        TextView fileName = null;
    }
}
