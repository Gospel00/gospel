/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.fragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dxc.mycollector.AddGasAcitvity;
import com.dxc.mycollector.R;
import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.model.GasInfo;
import com.dxc.mycollector.pullableview.MyListener;
import com.dxc.mycollector.pullableview.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * About Login
 */
public class GasFragment extends Fragment {

    private ListView listview;
    Context context;
    List<GasInfo> listgas = null;


    public static GasFragment newInstance(String s) {
        GasFragment homeFragment = new GasFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARGS, s);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        //设置ActionBar名称
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_gas);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) getActivity().findViewById(R.id.title_name)).setText(getString(R.string.item_gas));
        //添加气体检查-手动输入
        ((Button) getActivity().findViewById(R.id.gas_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddGasAcitvity.class));
            }
        });
        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            actionBar.setElevation(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gas_fragment, container, false);
        //下拉刷新
        ((PullToRefreshLayout) view.findViewById(R.id.gas_refresh_view))
                .setOnRefreshListener(new MyListener());

        //初始化listview对象
        listview = (ListView) view.findViewById(R.id.gas_listView);
        //获取已经下载的任务信息
        getAllTasks();
        //初始化ListView
        initDrawerList();
        //
        TextView txvEmpty = (TextView) view.findViewById(R.id.gas_empty);//获取textview对象
        /**
         * 判断listview是是否为空，如果为空时显示提示信息，如果不为空时设置为gone
         */
        if (listgas != null && listgas.size() > 0) {
            txvEmpty.setVisibility(View.GONE);
        } else {
            txvEmpty.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void getAllTasks() {
        listgas = new ArrayList<>();
        List<GasInfo> alltask = SqliteUtils.getInstance(context).loadGasList();
        for (GasInfo gasinfo : alltask) {
            listgas.add(gasinfo);
        }
    }

    private void initDrawerList() {
        BaseAdapter adapter = new BaseAdapter() {
            @SuppressLint("NewApi")
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                GasInfo gasInfo = listgas.get(position);
                Holder holder = null;
                View layout = View.inflate(context, R.layout.gas_list_item_layout, null);

                if (convertView == null) {
                    holder = new Holder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.gas_list_item_layout, null);

//                    holder.gasnamepoint = (ImageView) convertView.findViewById(R.id.gas_logo);
                    holder.gasname = (TextView) convertView.findViewById(R.id.gas_name);
                    holder.gastime = (TextView) convertView.findViewById(R.id.gas_time);

                    if (gasInfo != null) {
                        holder.gasname.setText("C02:" + gasInfo.getCarbonDioxide()
                                + "-" + "C0:" + gasInfo.getCarbonMonoxide() + "-" + "CH4:" + gasInfo.getMethane() + "-" + "H2S:" + gasInfo.getHydrogenSulfide());
                        holder.gastime.setText(gasInfo.getCheckTime());
                    }
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }

                return convertView;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return listgas.get(position);
            }

            @Override
            public int getCount() {
                return listgas.size();
            }
        };
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new DrawerItemClickListener());
    }


    static class Holder {
        ImageView gasnamepoint = null;
        TextView gastime = null;
        TextView gasname = null;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(context, "开始测量", Toast.LENGTH_SHORT).show();
            GasInfo gasInfo = listgas.get(position);
//            Intent intent = new Intent(context, CeLiangActivity.class);
//            TaskDetails detailDatas = taskInfo.getTaskDetail();
//            detailDatas.setDateTime(taskInfo.getStartTime());
//            intent.putExtra("detailDatas", detailDatas);
//            intent.putExtra("taskId", taskInfo.getTaskId());
//            intent.putExtra("tasktypes", taskInfo.getTaskType());
//            startActivity(intent);
        }
    }
}
