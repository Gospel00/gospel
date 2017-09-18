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
        ((TextView) findViewById(R.id.title_name)).setText("上传量测数据");

        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
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
        final Button allupload = (Button) findViewById(R.id.all_upload);
        allupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myAdapter.flage) {
                    waitingDialog1();
                    DownLoadManager downLoadManager = new DownLoadManager(UploadBlueToothFolder.this);
                    for (int i = 0; i < listtasks.size(); i++) {
                        final MeasureData taskInfo = listtasks.get(i);
                        if (listtasks.get(i).isCheck) {
                            downLoadManager.uploadMeasure(listtasks.get(i));
                            final int finalI = i;
                            downLoadManager.setUploadCallback(new DownLoadManager.UploadCallback() {
                                @Override
                                public void callback(boolean statu, String msg) {
                                    poist = finalI;
                                    uptrue = statu;
                                    msgstr = msg;
                                    did = taskInfo.getCldianId();
                                    taskId = taskInfo.getTaskId();
                                    uhandler.sendEmptyMessage(2);
                                }
                            });
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(1, 500);//关闭等待框的handle处理消息
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


    boolean uptrue = false;
    String msgstr = "";
    String taskId = "";
    String did = "";
    int poist = -1;
    //上传成功处理
    private Handler uhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (uptrue) {
                        mHandler.sendEmptyMessageDelayed(1, 500);//关闭等待框的handle处理消息
                        Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
                        //上传成功，更新本地数据上传状态
                        int result = SqliteUtils.getInstance(context).updateUpLoadStatus(taskId, did, false);
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
                        SqliteUtils.getInstance(context).updateUpLoadStatus(taskId, did, true);
                    }
                    break;
                case 2:
                    if (uptrue) {
//                        mHandler.sendEmptyMessageDelayed(1, 500);//关闭等待框的handle处理消息
                        Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
                        //上传成功，更新本地数据上传状态
                        int result = SqliteUtils.getInstance(context).updateUpLoadStatus(taskId, did, false);
                        if (result > 0) {
                            //更新上传列表
                            if (poist <= listtasks.size()) {
                                listtasks.remove(poist);
                                myAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
//                        mHandler.sendEmptyMessageDelayed(1, 500);//关闭等待框的handle处理消息
                        if (msgstr != null && msgstr.length() > 0) {
                            msgstr = msgstr.substring(msgstr.indexOf("msg"), msgstr.length() - 1);
                        }
                        Toast.makeText(getApplicationContext(), "上传失败!\n" + msgstr, Toast.LENGTH_LONG).show();
                        //如果服务器以及关闭或者删除这个任务，手机会暂存这个测量结果，状态设置为
                        listtasks.remove(poist);
                        myAdapter.notifyDataSetChanged();
                        SqliteUtils.getInstance(context).updateUpLoadStatus(taskId, did, true);
                    }
                    break;
                default:
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
                    break;
            }
        }
    };

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
                                public void callback(boolean statu, String msg) {
                                    poist = position;
                                    uptrue = statu;
                                    msgstr = msg;
                                    did = taskInfo.getCldianId();
                                    taskId = taskInfo.getTaskId();
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
