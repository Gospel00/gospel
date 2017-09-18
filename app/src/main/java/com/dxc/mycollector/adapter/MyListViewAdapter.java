/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.dxc.mycollector.R;
import com.dxc.mycollector.UploadBlueToothFolder;
import com.dxc.mycollector.model.MeasureData;

import java.util.List;

/**
 * @desc Created by Gospel on 2017/9/15 10:56
 * DXC technology
 */

public class MyListViewAdapter extends BaseAdapter {


    private Context context;

    private List<MeasureData> listtasks;

    private LayoutInflater mInflater;

    public boolean flage = false;


    public MyListViewAdapter(Context mContext, List<MeasureData> mDatas) {
        this.context = mContext;
        this.listtasks = mDatas;
        mInflater = LayoutInflater.from(this.context);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.upload_bluetooth_list_item_layout, null);
            holder.fileName = (TextView) convertView.findViewById(R.id.upload_bluetoothfile_file_name);
            holder.fileTime = (TextView) convertView.findViewById(R.id.upload_bluetoothfile_file_time);
            holder.checkboxOperateData = (CheckBox) convertView.findViewById(R.id.checkbox_operate_data);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        final MeasureData taskInfo = listtasks.get(position);
        if (taskInfo != null) {
            holder.fileName.setText(taskInfo.getTaskId() + "-" + taskInfo.getCldian() + "-" + taskInfo.getCllicheng());
            holder.fileTime.setText(taskInfo.getCltime() + "-" + (taskInfo.getDataType().equals("0") ? "手动录入" : "蓝牙读取"));
            // 根据isSelected来设置checkbox的显示状况
            if (flage) {
                holder.checkboxOperateData.setVisibility(View.VISIBLE);
            } else {
                holder.checkboxOperateData.setVisibility(View.GONE);
            }
            holder.checkboxOperateData.setChecked(taskInfo.isCheck);
            //注意这里设置的不是onCheckedChangListener，还是值得思考一下的
            holder.checkboxOperateData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (taskInfo.isCheck) {
                        taskInfo.isCheck = false;
                    } else {
                        taskInfo.isCheck = true;
                    }
                }
            });
        }
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

    static class Holder {
        TextView fileName = null;
        TextView fileTime = null;
        CheckBox checkboxOperateData;
    }
}
