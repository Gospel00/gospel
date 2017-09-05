package com.dxc.mycollector;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dxc.mycollector.logs.Logger;

/**
 * Created by zhangruw on 9/5/2017.
 */

public class a extends BaseActivity {
    /**
     * 初始化侧滑菜单
     */
    public void setUpNavigation(Context c) {
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
                //startActivity(new Intent(this, BlueToothFolder.class));
                startActivity(new Intent(this, UploadBlueToothFolder.class));
                Logger.i(TAG, "click bluetooth folder  search.");
                break;
//            case 3:
//                startActivity(new Intent(this, ShowExamineRecord.class));
//                Logger.i(TAG, "click safety examine.");
//                break;
            case 3:
                startActivity(new Intent(this, DeviceSettingActivity.class));
                Logger.i(TAG, "click devices setting.");
                break;
            case 4:
                startActivity(new Intent(this, UpdateSystemActivity.class));
                Logger.i(TAG, "click update system.");
                break;
            case 5:
                startActivity(new Intent(this, AboutSystemActivity.class));
                Logger.i(TAG, "click about system.");
                break;
            case 6:
                startActivity(new Intent(this, UserListAcitvity.class));
                Logger.i(TAG, "click user list.This operation belongs to the administrator.");
                break;
        }
    }

}
