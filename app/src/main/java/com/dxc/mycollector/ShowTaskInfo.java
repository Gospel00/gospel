/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.TaskDetails;
import com.dxc.mycollector.model.TaskInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by gospel on 2017/8/21.
 * About ShowExamineRecord
 */

public class ShowTaskInfo extends BaseActivity {
    private ListView listview;
    TextView textView;
    String result = null;
    private Button taskAdd;
    Context context;
    List<TaskInfo> listtasks = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.task_download_main_layout);
        context = this;
        waitingDialog();//加载等待页面对话框方法
        //获取已经下载的任务信息
        getAllTasks();
        //初始化ListView
        initDrawerList();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        // 使用setText的方法对textview动态赋值
        ((TextView) findViewById(R.id.title_name)).setText("我的任务列表");

        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }
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

    private void getAllTasks() {
        listtasks = new ArrayList<>();
        List<TaskInfo> alltask = SqliteUtils.getInstance(this).loadTasks();
        for (TaskInfo taskinfo : alltask) {
            listtasks.add(taskinfo);
        }
    }

    private void initDrawerList() {
        listview = (ListView) this.findViewById(R.id.task_listView);
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                Holder holder = null;
                if (convertView == null) {
                    holder = new Holder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.task_download_list_item_layout, null);
                    holder.tasknamepoint = (TextView) convertView.findViewById(R.id.show_task_name_point);
                    holder.taskname = (TextView) convertView.findViewById(R.id.show_task_name);
                    convertView.setTag(holder);
//                    Button upbtn = (Button) convertView.findViewById(R.id.kaishicl);
//                    upbtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            startActivity(new Intent(getApplicationContext(), CeLiangActivity.class));
////                            Toast.makeText(ShowTaskInfo.this, "接口正在开发中...", Toast.LENGTH_SHORT).show();
////                            selectItem(position);
//                        }
//                    });
                } else {
                    holder = (Holder) convertView.getTag();
                }
                TaskInfo taskInfo = listtasks.get(position);
                String starttime = "";
                if (taskInfo.getStartTime() != null && taskInfo.getStartTime().length() > 10)
                    starttime = taskInfo.getStartTime().substring(0, 10);
                else
                    starttime = taskInfo.getStartTime();
                String showstr = taskInfo.getTaskId() + "-" + (taskInfo.getTaskType().equals("T0101") ? "拱顶沉降" : "水平收敛");
                showstr += "(" + getMeasureType(taskInfo.getMeasureType()) + ")" + taskInfo.getTaskDetail().getPointLabel() + "-" + taskInfo.getTaskDetail().getSection();
                holder.tasknamepoint.setText(showstr);
                holder.taskname.setText(taskInfo.getTaskDetail().getProName() + "-" + taskInfo.getTaskDetail().getMileageLabel() + "-" + starttime);// + "-" + taskInfo.getEndTime().substring(0, 10)

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
        listview.setOnItemClickListener(new DrawerItemClickListener());
    }

    public String getMeasureType(String measureType) {
        String str = "";
        if (measureType.equals("T0201")) {
            str = "水准仪";
        }
        if (measureType.equals("T0202")) {
            str = "刚挂尺";
        }
        if (measureType.equals("T0203")) {
            str = "全站仪";
        }
        if (measureType.equals("T0204")) {
            str = "收敛计";
        }
        return str;
    }

    static class Holder {
        TextView tasknamepoint = null;
        TextView taskname = null;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(context, "开始测量", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ShowTaskInfo.this, CeLiangActivity.class);
            TaskInfo taskInfo = listtasks.get(position);
            TaskDetails detailDatas = taskInfo.getTaskDetail();
            detailDatas.setDateTime(taskInfo.getStartTime().substring(0, 10));
            intent.putExtra("detailDatas", detailDatas);
            intent.putExtra("taskId", taskInfo.getTaskId());
            startActivity(intent);
//            finish();
//            selectItem(position);
//            actionAlertDialog();
        }
    }

    protected void actionAlertDialog() {
//        ArrayList<Person> list = initData();
        AlertDialog.Builder builder;
        AlertDialog alertDialog;
//        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
//        LayoutInflater.from(context).inflate(R.layout.task_download_list_item_layout, null);
        View layout = inflater.inflate(R.layout.task_download_main_layout, (ViewGroup) findViewById(R.id.mylistview));
//        ListView myListView = (ListView) layout.findViewById(R.id.mylistview);
//        MyAdapter adapter = new MyAdapter(context, list);
//        myListView.setAdapter(adapter);
        builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();

    }


    /**
     * 选择行
     *
     * @param position
     */
    private void selectItem(int position) {
//        Toast.makeText(this, planetTitles[position], Toast.LENGTH_SHORT).show();
        TaskInfo taskInfo = listtasks.get(position);
        TaskDetails detailDatas = taskInfo.getTaskDetail();
        String[] strarr = new String[1];
//        int i = 0;
//        for (TaskDetails detailData : detailDatas) {
        strarr[0] = detailDatas.getProName() + "-" + detailDatas.getMileageLabel() + "-" + detailDatas.getPointLabel();
//        }
        new AlertDialog.Builder(this)
                .setTitle("测量仪器列表")
                .setItems(strarr, null)
                .setNegativeButton("确定", null)
                .show();
    }

    /**
     * 嵌套ListView
     *
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
