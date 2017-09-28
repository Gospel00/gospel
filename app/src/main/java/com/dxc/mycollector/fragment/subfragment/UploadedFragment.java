/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.fragment.subfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dxc.mycollector.R;
import com.dxc.mycollector.adapter.MyListViewAdapter;
import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.model.MeasureData;
import com.dxc.mycollector.pullableview.MyListener;
import com.dxc.mycollector.pullableview.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gospel on 2017/8/18.
 * About Login
 */
public class UploadedFragment extends Fragment {
    private View view;
    private ListView noUploadlistView;//未上传列表
    Context context;
    List<MeasureData> listtasks;
    private MyListViewAdapter myAdapter;

    private Dialog mWeiboDialog;//等待框

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.uploaded_fragment, container, false);

        queryDBCDate();//查询数据库的方法
        searchDrawerList();//查询 DrawerList的方法

        // 下拉刷新
        ((PullToRefreshLayout) view.findViewById(R.id.upload_refresh_view)).setOnRefreshListener(new MyListener());
        /**
         * 判断listview是否为空，如果为空时显示提示信息，如果不为空时设置为gone
         */
        TextView txvEmpty = (TextView) view.findViewById(R.id.upload_empty);//获取textview对象
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

    /**
     * 查询已上传的测量数据
     */
    public void queryDBCDate() {
        listtasks = new ArrayList<>();
        List<MeasureData> alltask = SqliteUtils.getInstance(context).queryMeasure("0");
        for (MeasureData md : alltask) {
            listtasks.add(md);
        }
    }

    private void searchDrawerList() {
        noUploadlistView = (ListView) view.findViewById(R.id.upload_listView);
        myAdapter = new MyListViewAdapter(context, listtasks);
        noUploadlistView.setAdapter(myAdapter);
        noUploadlistView.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//            Toast.makeText(context, "开始上传", Toast.LENGTH_SHORT).show();
            final MeasureData taskInfo = listtasks.get(position);
            new AlertDialog.Builder(context)
                    .setTitle("系统提示")
                    .setIcon(R.drawable.warn_small)
                    .setMessage("该量测任务已经上传到平台了！\n本次测量： " + taskInfo.getGaocheng() + "   " + "\n初始值： " + taskInfo.getChushizhi() + "\n" + "本次测量与初始值差：" + taskInfo.getChazhi())
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
    }

}
