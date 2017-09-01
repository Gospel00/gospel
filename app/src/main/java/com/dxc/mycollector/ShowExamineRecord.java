package com.dxc.mycollector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.model.TaskInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gospel on 2017/8/21.
 * About ShowExamineRecord
 */

public class ShowExamineRecord extends BaseActivity {
    private ListView listview;
    private Button taskAdd;
    Context context;
    List<TaskInfo> listtasks = null;

    /*使用DownLoadManager时只能通过DownLoadService.getDownLoadManager()的方式来获取下载管理器，不能通过new DownLoadManager()的方式创建下载管理器*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.show_examine_record_main_layout);
        context = this;
        taskAdd = (Button) this.findViewById(R.id.taskAdd);
        listview = (ListView) this.findViewById(R.id.examine_listView);
        //新增安全检查记录
        taskAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddExamineRecord.class));
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
        }
    }

    private void initDrawerList() {
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Holder holder = null;
                if (convertView == null) {
                    holder = new Holder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.show_examine_list_item_layout, null);
                    holder.fileName = (TextView) convertView.findViewById(R.id.show_examine_name);
                    Button upbtn = (Button) convertView.findViewById(R.id.upload);
//                    TextView text = (TextView) convertView.findViewById(R.id.jiexi);
                    convertView.setTag(holder);
                    upbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ShowExamineRecord.this, "接口正在开发中...", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    holder = (Holder) convertView.getTag();
                }
                holder.fileName.setText(listtasks.get(position).getTaskId());
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
