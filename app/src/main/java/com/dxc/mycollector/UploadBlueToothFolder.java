package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.MeasureData;
import com.dxc.mycollector.pullableview.MyListener;
import com.dxc.mycollector.pullableview.PullToRefreshLayout;
import com.dxc.mycollector.taskDownload.DownLoadManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangruw on 9/4/2017.
 */

public class UploadBlueToothFolder extends BaseActivity {
    String TAG = UploadBlueToothFolder.class.getSimpleName();
    private ListView uploadfileList;//文件列表
    private List<String> listf;
    private TextView text;//上传按钮
    Context context;
    List<MeasureData> listtasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_bluetooth_fileslist_main_layout);
        waitingDialog();//加载等待页面方法
        uploadfileList = (ListView) findViewById(R.id.showuploadbluetoothfilelistView);
        context = this;
//        String aa = searchFile("");
        queryDBCDate();
        searchDrawerList();

//        findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    DownLoadManager downLoadManager = new DownLoadManager(UploadBlueToothFolder.this);
//                    downLoadManager.uploadMeasure(null);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        //下拉刷新
        ((PullToRefreshLayout) findViewById(R.id.refresh_view2))
                .setOnRefreshListener(new MyListener());
        uploadfileList = (ListView) findViewById(R.id.showuploadbluetoothfilelistView);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//??????
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("测量数据列表");

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


    /**
     * the one get date not in database
     */
    public void queryDBCDate() {
        listtasks = new ArrayList<>();
        List<MeasureData> alltask = SqliteUtils.getInstance(this).queryMeasure();
        for (MeasureData md : alltask) {
            listtasks.add(md);
        }
    }

    private void searchDrawerList() {
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Holder holder = null;
                if (convertView == null) {
                    holder = new Holder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.upload_bluetooth_list_item_layout, null);
                    holder.fileName = (TextView) convertView.findViewById(R.id.upload_bluetoothfile_file_name);
                    holder.fileTime = (TextView) convertView.findViewById(R.id.upload_bluetoothfile_file_time);
                    text = (TextView) convertView.findViewById(R.id.upload);
                    convertView.setTag(holder);
                    text.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int i = 0;
                            try {
//                                readFile(pathFile);
                            } catch (Exception e) {
                                String dff = e.toString();
                                e.printStackTrace();
                            }
                        }
                    });

                } else {
                    holder = (Holder) convertView.getTag();
                }
//                holder.fileName.setText(listtasks.get(position).g);
                MeasureData taskInfo = listtasks.get(position);


                holder.fileName.setText(taskInfo.getCldian() + "-" + taskInfo.getCllicheng() + "-" + taskInfo.getShoulian());
                holder.fileTime.setText(taskInfo.getCltime());
                Logger.e(TAG, taskInfo.toString());
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
        uploadfileList.setAdapter(adapter);
        uploadfileList.setOnItemClickListener(new DrawerItemClickListener());
    }

    static class Holder {
        TextView fileName = null;
        TextView fileTime = null;
    }

    boolean uptrue = false;
    private Handler uhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    //上传成功，更新本地数据上传状态
//                    int result = SqliteUtils.getInstance(mycontext).saveTaskInfo(maps.get(ti), ti);
//                    Logger.i(TAG, "任务保存成功。测量详情：" + maps.get(ti).toString())
                    if (uptrue) {
                        Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "上传失败!", Toast.LENGTH_LONG).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(context, "开始上传", Toast.LENGTH_SHORT).show();
            MeasureData taskInfo = listtasks.get(position);
            DownLoadManager downLoadManager = new DownLoadManager(UploadBlueToothFolder.this);
            downLoadManager.uploadMeasure(taskInfo);
            downLoadManager.setUploadCallback(new DownLoadManager.UploadCallback() {
                @Override
                public void callback(boolean statu) {
                    uptrue = statu;
                    uhandler.sendEmptyMessage(1);
                }
            });

        }
    }


    /**
     * showUploadResult
     *
     * @param result
     */
    private void showUploadResult(String result) {
        String[] strarr = new String[1];
        strarr[0] = result;
        new AlertDialog.Builder(this)
                .setTitle("上传测量数据")
                .setItems(strarr, null)
                .setNegativeButton("确定", null)
                .show();
    }

}
