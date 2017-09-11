package com.dxc.mycollector;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;

import java.util.List;

/**
 * Created by sunyi on 2017/8/25.
 */

public class BaseActivity extends AppCompatActivity {
    String TAG = BaseActivity.class.getSimpleName();
    //    protected String[] planetTitles;
    protected DrawerLayout drawerLayout;
    protected ListView drawerList;
    protected FrameLayout frameLayout;
    protected String[] planetTitles = null;//{"个人信息", "任务管理", "数据管理", "安全管理", "仪器设置", "系统升级", "关于系统"};
    protected int[] imagesId = {R.drawable.assignment, R.drawable.down,
            R.drawable.data, R.drawable.measure, R.drawable.update, R.drawable.system, R.drawable.safe};
    Context context;
    private Dialog mWeiboDialog;//对话框
    private ActionBarDrawerToggle toggle;

    long alltask = 0;//任务数
    long uploadtask = 0;//上传

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    //定义加载等待页面方法
    public void waitingDialog() {
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(context, "加载中...");//加载对话框
        mHandler.sendEmptyMessageDelayed(1, 1000);//处理消息
    }

    //消息处理线程
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //DialogThridUtils.closeDialog(mDialog);
                    if (context != null && mWeiboDialog != null) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                    }
                    break;
            }
        }
    };

    /**
     * 重写setContentView，以便于在保留侧滑菜单的同时，让子Activity根据需要加载不同的界面布局
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.menu_activity_first, null);
        frameLayout = (FrameLayout) drawerLayout.findViewById(R.id.content_frame);
        // 将传入的layout加载到activity_base的content_frame里面
        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(drawerLayout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        setUpNavigation();
//        View layout = View.inflate(context, R.layout.menu_list_item, null);
//        LinearLayout lin1 = (LinearLayout) layout.findViewById(R.id.item_lin_1_1);
//        final TextView num = (TextView) layout.findViewById(R.id.num);
//        toggle = new ActionBarDrawerToggle(this, drawerLayout,
//                R.drawable.more_wirte,
//                R.string.open,
//                R.string.close) {
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
////                Toast.makeText(context, "open", Toast.LENGTH_SHORT).show();
//                alltask = SqliteUtils.getInstance(context).loadTasksCount();
//                num.setText(String.valueOf(alltask));
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
////                Toast.makeText(context, "close", Toast.LENGTH_SHORT).show();
//                uploadtask = SqliteUtils.getInstance(context).queryMeasureCount();
//                num.setText(String.valueOf(uploadtask));
//            }
//
//        };
//        drawerLayout.setDrawerListener(toggle);
    }

    /**
     * 初始化侧滑菜单
     */
    private void setUpNavigation() {
        planetTitles = getResources().getStringArray(R.array.planets_array);
        BaseAdapter adapter = new BaseAdapter() {
            @SuppressLint("NewApi")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View layout = View.inflate(context, R.layout.menu_list_item, null);
                ImageView imgv = (ImageView) layout.findViewById(R.id.lgface);
                TextView name1 = (TextView) layout.findViewById(R.id.name1);
                LinearLayout lin = (LinearLayout) layout.findViewById(R.id.item_lin_1);
                LinearLayout lin1 = (LinearLayout) layout.findViewById(R.id.item_lin_1_1);
                ImageView face = (ImageView) layout.findViewById(R.id.lgicon);
                TextView name = (TextView) layout.findViewById(R.id.menu_name);
                TextView num = (TextView) layout.findViewById(R.id.num);
                LinearLayout logout = (LinearLayout) layout.findViewById(R.id.item_tltle1);
                ImageView mm = (ImageView) layout.findViewById(R.id.lgface1);
                mm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context)
                                .setTitle("退出系统")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setMessage("您确定要从系统中退出？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DLApplication.userName = "";
                                        finish();
                                        startActivity(new Intent(BaseActivity.this, MainActivity.class));
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();

                    }
                });
                if (position > 0) {
                    imgv.setVisibility(View.GONE);
                    name1.setVisibility(View.GONE);
                    logout.setVisibility(View.GONE);
                    mm.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    layout.invalidate();
                }
                //是否显示任务数
                if (position != 1 && position != 2) {
                    lin1.setVisibility(View.GONE);
                    num.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    layout.invalidate();
                } else {
                    //任务数
                    if (position == 1) {
                        {
                            num.setVisibility(View.VISIBLE);
                            alltask = SqliteUtils.getInstance(context).loadTasksCount();
                            num.setText(String.valueOf(alltask));
                            lin1.setBackground(getResources().getDrawable(R.drawable.textviewstyle));
                        }
                    }
                    //待上传的测量数
                    if (position == 2) {
                        {
                            num.setVisibility(View.VISIBLE);
                            uploadtask = SqliteUtils.getInstance(context).queryMeasureCount();
                            num.setText(String.valueOf(uploadtask));
                            lin1.setBackground(getResources().getDrawable(R.drawable.textviewstyle3));
                        }
                    }
                }

                //admin
                if (DLApplication.userName != null && !DLApplication.userName.equals(DLApplication.amdin)) {
                    if (position != 6) {
                        face.setImageResource(imagesId[position]);
                        name.setText(planetTitles[position]);
                    } else {//==7
                        lin.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                        layout.invalidate();
                    }
                } else {
                    face.setImageResource(imagesId[position]);
                    name.setText(planetTitles[position]);
                }

                if (position != 6) {
//                    logout.setVisibility(View.GONE);
//                    btnexitall.setVisibility(View.GONE);
//                    mm.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    layout.invalidate();
                }
//                android.view.ViewGroup.LayoutParams lp =mm.getLayoutParams();
//                lp.height=ScreenHieght();

                return layout;
            }


            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public Object getItem(int position) {
                return planetTitles[position];
            }

            @Override
            public int getCount() {
                return planetTitles.length;
            }
        };
        drawerList.setAdapter(adapter);
//        drawerList.setAdapter(new ArrayAdapter<>(BaseActivity.this,
//                R.layout.menu_list_item, planetTitles));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    public int ScreenHieght() {
        WindowManager windowManager = getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        // int de= defaultDisplay.getHeight ()-1100;
        int de = defaultDisplay.getHeight() * 180 / 1280;
        return de;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
//        Toast.makeText(BaseActivity.this, planetTitles[position], Toast.LENGTH_SHORT).show();
        switch (position) {
            case 0:
                startActivity(new Intent(this, PersonAcitvity.class));
                break;
            case 1:
                startActivity(new Intent(this, ShowTaskInfo.class));
//                Logger.i(TAG, "click task download.");
                break;
            case 2:
                //startActivity(new Intent(this, BlueToothFolder.class));
                startActivity(new Intent(this, UploadBlueToothFolder.class));
//                Logger.i(TAG, "click bluetooth folder  search.");
                break;
//            case 3:
//                startActivity(new Intent(this, ShowExamineRecord.class));
//                Logger.i(TAG, "click safety examine.");
//                break;
            case 3:
                startActivity(new Intent(this, DeviceSettingActivity.class));
//                Logger.i(TAG, "click devices setting.");
                break;
            case 4:
                startActivity(new Intent(this, UpdateSystemActivity.class));
//                Logger.i(TAG, "click update system.");
                break;
            case 5:
                startActivity(new Intent(this, AboutSystemActivity.class));
//                Logger.i(TAG, "click about system.");
                break;
            case 6:
                startActivity(new Intent(this, UserListAcitvity.class));
//                Logger.i(TAG, "click user list.This operation belongs to the administrator.");
                break;
        }
    }

    /**
     * @param list get All Html Key Vules
     */
    private void showDialog(List<String> list) {
        new AlertDialog.Builder(this)
                .setTitle("列表框")
                .setItems(new String[]{"列表项1", "列表项2", "列表项3"}, null)
                .setNegativeButton("确定", null)
                .show();
    }

}