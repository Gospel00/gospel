package com.dxc.mycollector;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.adapter.MyListViewAdapter;
import com.dxc.mycollector.dbhelp.SqliteUtils;
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
    private Dialog mWeiboDialog;//对话框
    private MyListViewAdapter myAdapter;
    boolean uptrue = false;
    String msgstr = "";
    String taskIds = "";
    String did = "";
    int poist = -1;
    //上传成功处理
    private Handler uhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                //单个上传
                case 1:
                    if (uptrue) {
                        mHandler.sendEmptyMessageDelayed(1, 500);//关闭等待框的handle处理消息
                        Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
                        //上传成功，更新本地数据上传状态
                        int result = SqliteUtils.getInstance(context).updateUpLoadStatus(taskIds, did, false);
                        if (result > 0) {
                            //更新上传列表
                            listtasks.remove(poist);
                            myAdapter.notifyDataSetChanged();
                        }
                    } else {
                        mHandler.sendEmptyMessageDelayed(1, 500);//关闭等待框的handle处理消息
                        if (msgstr != null && msgstr.length() > 0) {
                            msgstr = msgstr.substring(msgstr.indexOf("msg"), msgstr.length() - 1);
                        }
                        Toast.makeText(getApplicationContext(), "上传失败!\n" + msgstr, Toast.LENGTH_LONG).show();
                        //如果服务器以及关闭或者删除这个任务，手机会暂存这个测量结果，状态设置为
                        listtasks.remove(poist);
                        myAdapter.notifyDataSetChanged();
                        SqliteUtils.getInstance(context).updateUpLoadStatus(taskIds, did, true);
                    }
                    break;
                //批量上传
                case 2:
                    String uptruestr = msg.getData().getString("uptrue");
                    String taskIdstr = msg.getData().getString("taskId");
                    String didstr = msg.getData().getString("did");
                    String msgstrstr = msg.getData().getString("msgstr");
                    if (uptruestr.equals("true")) {
//                        mHandler.sendEmptyMessageDelayed(1, 500);//关闭等待框的handle处理消息
//                        Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
                        //上传成功，更新本地数据上传状态
                        int result = SqliteUtils.getInstance(context).updateUpLoadStatus(taskIdstr, didstr, false);
                        if (result > 0) {
                            /**
                             * 更新上传列表
                             * 1、如果上传列表不为空
                             * 2、判断要删除的这个对象是否是被选中的（也就是说删除已经当前上传的数据对象）
                             */
                            if (listtasks != null && listtasks.size() > 0) {
                                //2、判断要删除的这个对象是否是被选中的（也就是说删除已经当前上传的数据对象）
                                if (listtasks.get(0).isCheck) {
                                    listtasks.remove(0);
                                    myAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
//                        mHandler.sendEmptyMessageDelayed(1, 500);//关闭等待框的handle处理消息
//                            if (msgstrstr != null && msgstrstr.length() > 0) {
//                                msgstrstr = msgstrstr.substring(msgstrstr.indexOf("msg"), msgstrstr.length() - 1);
//                            }
//                        Toast.makeText(getApplicationContext(), "上传失败!\n" + msgstr, Toast.LENGTH_LONG).show();
                            //如果服务器以及关闭或者删除这个任务，手机会暂存这个测量结果，状态设置为
//                            listtasks.remove(0);
//                            myAdapter.notifyDataSetChanged();
//                            SqliteUtils.getInstance(context).updateUpLoadStatus(taskId, did, true);
                        }
                    }
                    break;
                default:
//                    mHandler.sendEmptyMessageDelayed(1, 500);//关闭等待框的handle处理消息
                    break;
            }
            TextView txvEmpty = (TextView) findViewById(R.id.empty);//获取textview对象
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.upload_tool);//获取textview对象
            /**
             * 判断listview是是否为空，如果为空时显示提示信息，如果不为空时设置为gone
             */
            if (listtasks != null && listtasks.size() > 0) {
                txvEmpty.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            } else {
                txvEmpty.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.GONE);
            }
        }
    };

    //消息处理线程
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (context != null && mWeiboDialog != null) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_bluetooth_fileslist_main_layout);
        waitingDialog();//加载等待页面方法
        uploadfileList = (ListView) findViewById(R.id.showuploadbluetoothfilelistView);
        context = this;
//        String aa = searchFile("");
        queryDBCDate();//查询数据库的方法
        searchDrawerList();//查询 DrawerList的方法
        //下拉刷新
        ((PullToRefreshLayout) findViewById(R.id.refresh_view2)).setOnRefreshListener(new MyListener());
        uploadfileList = (ListView) findViewById(R.id.showuploadbluetoothfilelistView);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//??????
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) findViewById(R.id.title_name)).setText("上传量测数据");

        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }
        TextView txvEmpty = (TextView) findViewById(R.id.empty);//获取textview对象
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.upload_tool);//获取textview对象
        /**
         * 判断listview是否为空，如果为空时显示提示信息，如果不为空时设置为gone
         */
        if (listtasks != null && listtasks.size() > 0) {
            txvEmpty.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            txvEmpty.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
        }

        //全选和反选的控制
        final Button button = (Button) findViewById(R.id.allor_choose);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter.flage = !myAdapter.flage;
//                if (myAdapter.flage) {
//                    button.setText("反选");
//                } else {
//                    button.setText("全选");
//                }
                for (int i = 0; i < listtasks.size(); i++) {
                    if (listtasks.get(i).isCheck) {
                        listtasks.get(i).isCheck = false;
                    } else {
                        listtasks.get(i).isCheck = true;
                    }
                }
                myAdapter.notifyDataSetChanged();
            }
        });
        //批量上传
        final Button allupload = (Button) findViewById(R.id.all_upload);
        allupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TextView txvEmpty = (TextView) findViewById(R.id.empty);//获取textview对象
                // LinearLayout linearLayout = (LinearLayout) findViewById(R.id.upload_tool);//获取textview对象
                if (myAdapter.flage) {
                    waitingDialog1();
                    DownLoadManager downLoadManager = new DownLoadManager(UploadBlueToothFolder.this);
                    for (int i = 0; i < listtasks.size(); i++) {
                        MeasureData taskInfo = listtasks.get(i);
                        if (taskInfo.isCheck) {
                            //waitingDialog1();
                            downLoadManager.uploadMeasure(taskInfo);
//                            final int finalI = i - 1;
                            downLoadManager.setUploadCallback(new DownLoadManager.UploadCallback() {
                                @Override
                                public void callback(boolean statu, String msg, String taskId, String pointId) {
                                    //发送更新列表handle
                                    Message message = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("uptrue", String.valueOf(statu));
                                    bundle.putString("msgstr", msgstr);
                                    bundle.putString("did", pointId);
                                    bundle.putString("taskId", taskId);
                                    message.setData(bundle);//bundle传值，耗时，效率低
                                    uhandler.sendMessage(message);//发送message信息
                                    message.what = 2;//标志是哪个线程传数据
//                                    uhandler.sendEmptyMessage(2);
                                }
                            });
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(1, 500);//关闭等待框的handle处理消息
                } else {
                    Toast.makeText(getApplicationContext(), "请选择您要上传的数据", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //定义加载等待页面方法
    public void waitingDialog1() {
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(context, "正在上传...");//加载对话框
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
        myAdapter = new MyListViewAdapter(this, listtasks);
        uploadfileList.setAdapter(myAdapter);
        uploadfileList.setOnItemClickListener(new DrawerItemClickListener());
        uploadfileList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        uploadfileList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = new MenuInflater(getApplication());
                menuInflater.inflate(R.menu.menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//            Toast.makeText(context, "开始上传", Toast.LENGTH_SHORT).show();
            final MeasureData taskInfo = listtasks.get(position);
            new AlertDialog.Builder(context)
                    .setTitle("系统提示")
                    .setIcon(R.drawable.warn_small)
                    .setMessage("本次测量： " + taskInfo.getGaocheng() + "   " + "\n初始值： " + taskInfo.getChushizhi() + "\n" + "本次测量与初始值差：" + taskInfo.getChazhi())
                    .setPositiveButton("确定上传", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            waitingDialog1();
                            DownLoadManager downLoadManager = new DownLoadManager(UploadBlueToothFolder.this);
                            downLoadManager.uploadMeasure(taskInfo);
                            downLoadManager.setUploadCallback(new DownLoadManager.UploadCallback() {
                                @Override
                                public void callback(boolean statu, String msg, String taskId1, String pointId) {
                                    poist = position;
                                    uptrue = statu;
                                    msgstr = msg;
                                    did = pointId;
                                    taskIds = taskId1;
                                    uhandler.sendEmptyMessage(1);
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
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
