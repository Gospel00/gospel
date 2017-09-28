/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.fragment.subfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dxc.mycollector.CeLiangActivity;
import com.dxc.mycollector.R;
import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.model.TaskDetails;
import com.dxc.mycollector.model.TaskInfo;
import com.dxc.mycollector.pullableview.MyListener;
import com.dxc.mycollector.pullableview.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gospel on 2017/8/18.
 * About Login
 */
public class FinishedFragment extends Fragment {
    private View view;

    private ListView listview;
    Context context;
    List<TaskInfo> listtasks = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.finished_fragment, container, false);

        //下拉刷新
        ((PullToRefreshLayout) view.findViewById(R.id.finish_refresh_view))
                .setOnRefreshListener(new MyListener());

        //初始化listview对象
        listview = (ListView) view.findViewById(R.id.finish_listView);
        //获取已经下载的任务信息
        getAllTasks();
        //初始化ListView
        initDrawerList();
        //
        TextView txvEmpty = (TextView) view.findViewById(R.id.finish_empty);//获取textview对象
        /**
         * 判断listview是是否为空，如果为空时显示提示信息，如果不为空时设置为gone
         */
        if (listtasks != null && listtasks.size() > 0) {
            txvEmpty.setVisibility(View.GONE);
        } else {
            txvEmpty.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void getAllTasks() {
        listtasks = new ArrayList<>();
        List<TaskInfo> alltask = SqliteUtils.getInstance(context).loadTasksByStatus("0");
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
                View layout = View.inflate(context, R.layout.task_download_list_item_layout, null);

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
        listview.setOnItemClickListener(new FinishedFragment.DrawerItemClickListener());
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
            Intent intent = new Intent(context, CeLiangActivity.class);
            TaskDetails detailDatas = taskInfo.getTaskDetail();
            detailDatas.setDateTime(taskInfo.getStartTime());
            intent.putExtra("detailDatas", detailDatas);
            intent.putExtra("taskId", taskInfo.getTaskId());
            intent.putExtra("tasktypes", taskInfo.getTaskType());
            startActivity(intent);
        }
    }
}
