/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.model.TaskDetails;
import com.dxc.mycollector.model.TaskInfo;
import com.dxc.mycollector.pullableview.MyListener;
import com.dxc.mycollector.pullableview.PullToRefreshLayout;

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
    String potid = "";
    public static ShowTaskInfo instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.task_download_main_layout);
        waitingDialog();//加载等待页面对话框方法
        context = this;
        instance = this;
        //获取任务完成标识
        potid = this.getIntent().getStringExtra("potid");


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

        //下拉刷新
        ((PullToRefreshLayout) findViewById(R.id.refresh_view1))
                .setOnRefreshListener(new MyListener());
        context = this;
        //初始化listview对象
        listview = (ListView) findViewById(R.id.task_listView);
        //获取已经下载的任务信息
        getAllTasks();
        //初始化ListView
        initDrawerList();
        //
        TextView txvEmpty = (TextView) findViewById(R.id.empty);//获取textview对象
        /**
         * 判断listview是是否为空，如果为空时显示提示信息，如果不为空时设置为gone
         */
        if (listtasks != null && listtasks.size() > 0) {
            txvEmpty.setVisibility(View.GONE);
        } else {
            txvEmpty.setVisibility(View.VISIBLE);
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

    private void getAllTasks() {
        listtasks = new ArrayList<>();
        List<TaskInfo> alltask = SqliteUtils.getInstance(this).loadTasks();
        for (TaskInfo taskinfo : alltask) {
            listtasks.add(taskinfo);
        }
    }

    private void initDrawerList() {
        BaseAdapter adapter = new BaseAdapter() {
            @SuppressLint("NewApi")
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                TaskInfo taskInfo = listtasks.get(position);
                Holder holder = null;
                View layout = View.inflate(getApplicationContext(), R.layout.task_download_list_item_layout, null);

                if (convertView == null) {

                    holder = new Holder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.task_download_list_item_layout, null);
                    //已完成任务
                    ImageView imageViews1 = (ImageView) convertView.findViewById(R.id.imageView1);
                    //待完成任务
                    ImageView imageViews = (ImageView) convertView.findViewById(R.id.imageView);
                    holder.tasknamepoint = (TextView) convertView.findViewById(R.id.show_task_name_point);
                    holder.tasknamepoint1 = (TextView) convertView.findViewById(R.id.show_task_name_point1);
                    holder.taskname = (TextView) convertView.findViewById(R.id.show_task_name);

                    // 如果potid不为空说明是完成任务后跳转过来的，在列表中能找到该任务，将其状态改为已完成
                    if (taskInfo != null && taskInfo.getStatus().equals("0")) {
                        imageViews.setVisibility(View.GONE);
                        imageViews1.setVisibility(View.VISIBLE);
                        convertView.invalidate();
                    } else {
                        imageViews1.setVisibility(View.GONE);
                        imageViews.setVisibility(View.VISIBLE);
                        convertView.invalidate();
                    }
                    convertView.setTag(holder);
                } else {
                    //已完成任务
                    ImageView imageViews1 = (ImageView) convertView.findViewById(R.id.imageView1);
                    //待完成任务
                    ImageView imageViews = (ImageView) convertView.findViewById(R.id.imageView);
                    //如果potid不为空说明是完成任务后跳转过来的，在列表中能找到该任务，将其状态改为已完成
                    if (taskInfo != null && taskInfo.getStatus().equals("0")) {
                        imageViews.setVisibility(View.GONE);
                        imageViews1.setVisibility(View.VISIBLE);
                        convertView.invalidate();
                    } else {
                        imageViews1.setVisibility(View.GONE);
                        imageViews.setVisibility(View.VISIBLE);
                        convertView.invalidate();
                    }
                    holder = (Holder) convertView.getTag();
                }

                String starttime = "";
                if (taskInfo.getStartTime() != null && taskInfo.getStartTime().length() > 10)
                    starttime = taskInfo.getStartTime().substring(0, 10);
                else
                    starttime = taskInfo.getStartTime();
                String showstr1 = ((taskInfo.getTaskType() != null && taskInfo.getTaskType().equals("T0101")) ? "沉降" : "收敛");
                // "(" + getMeasureType(taskInfo.getMeasureType()) + ")" +
                String showstr = taskInfo.getTaskId() + "-" + taskInfo.getTaskDetail().getMileageLabel() + "-" + taskInfo.getTaskDetail().getPointLabel();
                holder.tasknamepoint1.setText(showstr1);
                if (taskInfo.getTaskType() != null && taskInfo.getTaskType().equals("T0101"))
                    holder.tasknamepoint1.setBackground(getResources().getDrawable(R.drawable.textviewstyle2));
                else
                    holder.tasknamepoint1.setBackground(getResources().getDrawable(R.drawable.textviewstyle1));
                holder.tasknamepoint.setText(showstr);
                holder.taskname.setText(taskInfo.getTaskDetail().getProName() + "-" + taskInfo.getTaskDetail().getSection() + "-" + starttime);// + "-" + taskInfo.getEndTime().substring(0, 10)

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
        TextView tasknamepoint1 = null;
        TextView taskname = null;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(context, "开始测量", Toast.LENGTH_SHORT).show();
            TaskInfo taskInfo = listtasks.get(position);
            if (taskInfo != null && taskInfo.getStatus().equals("0")) {
                new AlertDialog.Builder(context)
                        .setTitle("系统提示")
                        .setIcon(R.drawable.success_small)
                        .setMessage("本次测量： " + taskInfo.getSjz() + "   " + "\n初始值： " + taskInfo.getTaskDetail().getInitialValue() +
                                "\n" + "本次测量与初始值差：" + taskInfo.getCz() + "\n该任务已经测量完成了！请在数据管理查看并上传测量结果。")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            } else {
                Intent intent = new Intent(ShowTaskInfo.this, CeLiangActivity.class);
                TaskDetails detailDatas = taskInfo.getTaskDetail();
                detailDatas.setDateTime(taskInfo.getStartTime());
                intent.putExtra("detailDatas", detailDatas);
                intent.putExtra("taskId", taskInfo.getTaskId());
                intent.putExtra("tasktypes", taskInfo.getTaskType());
                startActivity(intent);
            }
        }
    }

//    /**
//     * 广播接收器，用于销毁Activity
//     * @author honest
//     *
//     */
//    private class MyReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Parent.this.finish();
//        }
//    }
}
