package com.dxc.mycollector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.dbhelp.SqliteDB;
import com.dxc.mycollector.serviecs.UserService;
import com.dxc.mycollector.taskDownload.adapter.ListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * About BlueToothFolder getReceiveFiles
 */
public class BlueToothFolder extends BaseActivity {
    Context context;
    private ListView fileList;//文件列表
    private List<String> listf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_bluetooth_fileslist_main_layout);
        fileList = (ListView) findViewById(R.id.showbluetoothfilelistView);
        context = this;
        String aa = searchFile("");
        Log.i("bluetoothfile", "onCreate: " + aa);
        Toast.makeText(this, aa + "--" + listf.size(), Toast.LENGTH_LONG).show();
        initDrawerList();
    }

    private void initDrawerList() {
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Holder holder = null;
                if (convertView == null) {
                    holder = new Holder();
                    convertView = LayoutInflater.from(context).inflate(R.layout.show_bluetooth_list_item_layout, null);
                    holder.fileName = (TextView) convertView.findViewById(R.id.show_bluetoothfile_file_name);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                holder.fileName.setText(listf.get(position));
                return convertView;
            }
            @Override
            public long getItemId(int position) {
                return position;
            }
            @Override
            public Object getItem(int position) {
                return listf.get(position);
            }
            @Override
            public int getCount() {
                return listf.size();
            }
        };
        fileList.setAdapter(adapter);
    }

    static class Holder {
        TextView fileName = null;
    }

    private String searchFile(String keyword) {
        String path = Environment.getExternalStorageDirectory().getPath();
        String result = "";
        File[] files = new File(path + "/bluetooth/").listFiles();
        listf = new ArrayList<>();
        for (File file : files) {
//            if (file.getName().indexOf(keyword) >= 0) {
            String fn = file.getName();
            listf.add(fn);
            result += fn + "\n";// + "(" + file.getPath() + ")\n";
//            }
        }
        if (result.equals("")) {
            result = "找不到文件!!";
        }
        return result;
    }
}
