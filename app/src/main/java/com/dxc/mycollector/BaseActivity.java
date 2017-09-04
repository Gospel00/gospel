package com.dxc.mycollector;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.logs.Logger;

import java.util.ArrayList;
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
            R.drawable.data, R.drawable.safe, R.drawable.measure, R.drawable.update, R.drawable.system, R.drawable.safe};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public void finish() {
//        super.finish();
//        if (isTaskRoot()) {
//            Toast.makeText(this, "已经退出程序", Toast.LENGTH_LONG).show();
//        }
//    }

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
        setUpNavigation();
    }

    /**
     * 初始化侧滑菜单
     */
    private void setUpNavigation() {
        planetTitles = getResources().getStringArray(R.array.planets_array);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View layout = View.inflate(getApplicationContext(), R.layout.menu_list_item, null);
                ImageView imgv = (ImageView) layout.findViewById(R.id.lgface);
                TextView name1 = (TextView) layout.findViewById(R.id.name1);
                LinearLayout lin = (LinearLayout) layout.findViewById(R.id.item_lin_1);
                ImageView face = (ImageView) layout.findViewById(R.id.lgicon);
                TextView name = (TextView) layout.findViewById(R.id.name);
                TextView num = (TextView) layout.findViewById(R.id.num);
                if (position > 0) {
                    imgv.setVisibility(View.GONE);
                    name1.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    layout.invalidate();
                }
                //是否显示任务数
                if (position != 1 && position != 2 && position != 3) {
                    num.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    layout.invalidate();
                }
//                DLApplication myapp = (DLApplication) getApplicationContext();
//                Logger.i(TAG, "application user:" + DLApplication.userName);
//                Logger.i(TAG, "application admin:" + DLApplication.amdin);
                //admin
                if (DLApplication.userName != null && !DLApplication.userName.equals(DLApplication.amdin)) {
                    if (position != 7) {
                        face.setImageResource(imagesId[position]);
                        name.setText(planetTitles[position]);
                    } else {
                        lin.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                        layout.invalidate();
                    }
                } else {
                    face.setImageResource(imagesId[position]);
                    name.setText(planetTitles[position]);
                }
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
                Logger.i(TAG, "click task download.");
                break;
            case 2:
                startActivity(new Intent(this, BlueToothFolder.class));
                Logger.i(TAG, "click bluetooth folder  search.");
                break;
            case 3:
                startActivity(new Intent(this, ShowExamineRecord.class));
                Logger.i(TAG, "click safety examine.");
                break;
            case 4:
                Logger.i(TAG, "click devices setting.");
                break;
            case 5:
                Logger.i(TAG, "click update system.");
                break;
            case 6:
                Logger.i(TAG, "click about system.");
                break;
            case 7:
                startActivity(new Intent(this, UserListAcitvity.class));
                Logger.i(TAG, "click user list.This operation belongs to the administrator.");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            this.finish();
            // 创建退出对话框
//            AlertDialog isExit = new AlertDialog.Builder(this).create();
//            // 设置对话框标题
//            isExit.setTitle("系统提示");
//            // 设置对话框消息
////            isExit.setMessage("确定要退出吗");
//            // 添加选择按钮并注册监听
//            isExit.setButton("确定", listener);
//            isExit.setButton2("取消", listener);
//            // 显示对话框
//            isExit.show();
//            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    finish();
//                    System.exit(0);
                    startActivity(new Intent(getApplication(), MainActivity.class));
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };
}