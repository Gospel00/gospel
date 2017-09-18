package com.dxc.mycollector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dxc.mycollector.R;
import com.dxc.mycollector.model.User;

import java.util.List;

/**
 * Created by yazhang on 2017/9/13.
 */

public class Adapter extends BaseAdapter {
    private LayoutInflater layoutInflater = null;
//    private List<Person> persons;
    List<User> userList;

    public Adapter(Context context, List<User> userList) {

        layoutInflater = LayoutInflater.from(context);
        this.userList=userList;

    }



    /**
     * 获取adapter里有多少个数据项
     */
    @Override
    public int getCount() {
        return userList!=null?userList.size():0;
    }
    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 优化后
        ViewHolder holder;
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.userlist_item, null);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.name_number);
            /*holder.tell = (TextView)convertView.findViewById(R.id.textView3);
            holder.adress = (TextView)convertView.findViewById(R.id.textView2);*/
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.name.setText(userList.get(position).getuName());
       /* holder.tell.setText(userList.get(position).getuPhone());
        holder.adress.setText(userList.get(position).getuAddress());*/
        return convertView;
    }
    /**
     * 界面上的显示控件
     *
     * @author jiqinlin
     *
     */
    private static class ViewHolder{
        private TextView name;
       /* private TextView tell;
        private TextView adress;*/
    }
}

