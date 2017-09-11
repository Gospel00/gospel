package com.dxc.mycollector;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dxc.mycollector.bluetooth.BlueToothTestActivity;
import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.MeasureData;
import com.dxc.mycollector.model.TaskDetails;
import com.dxc.mycollector.utils.CalcUtils;
import com.dxc.mycollector.utils.DateConver;
import com.dxc.mycollector.utils.FilesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * About BlueToothFolder getReceiveFiles
 */
public class BlueToothFolder extends BaseActivity {
    String TAG = BlueToothFolder.class.getSimpleName();
    Context context;
    private ListView fileList;//文件列表
    private String pathFile = "";//文件路径
    private TextView text;//解析按钮
    private List<File> listf = new ArrayList<>();
    DLApplication myapp = null;
    private Dialog mWeiboDialog;//对话框
    /**
     * html change to json
     */
    List<String> l = new ArrayList();
    JSONObject tmpObj = null;
    JSONArray jsonArray = new JSONArray();
    String personInfos = "";
    String createTime = "";
    String hightProcess = "";//存放解析出来的测量值
    String shoulProcess = "";
    //蓝牙地址
    String lyaddress = "";
    TaskDetails td;
    String tId = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_bluetooth_fileslist_main_layout);
        context = this;

        fileList = (ListView) findViewById(R.id.showbluetoothfilelistView);

        waitingDialog("正在读取蓝牙接收到的文件...");//加载等待页面对话框方法

//        String aa = searchFile("");
        //接收蓝牙地址
        lyaddress = this.getIntent().getStringExtra("device_address");
        //测量详情
        td = (TaskDetails) this.getIntent().getSerializableExtra("tdesl");
        tId = this.getIntent().getStringExtra("taskId_lya");

        initDrawerList();

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar);
        //必须加2句
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        // 使用setText的方法对textview动态赋值
        ((TextView) findViewById(R.id.title_name)).setText("接收到的测量数据列表");

        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(0);
        }
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
    public void waitingDialog(String msg) {
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(context, msg);//加载对话框
        mHandler.sendEmptyMessageDelayed(1, 2000);//处理消息
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

    private void initDrawerList() {
        //初始化List
        listf = FilesUtils.listFileSortByModifyTime(Environment.getExternalStorageDirectory().getPath() + "/bluetooth/");

        fileList.setAdapter(adapter);
        fileList.setOnItemClickListener(new DrawerItemClickListener());
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(context).inflate(R.layout.show_bluetooth_list_item_layout, null);
                holder.fileName = (TextView) convertView.findViewById(R.id.show_bluetoothfile_file_name);
                holder.fileTime = (TextView) convertView.findViewById(R.id.show_bluetoothfile_file_time);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            File files = listf.get(position);
            Date datef = new Date(files.lastModified());
            String fName = files.getName();
            Logger.i(TAG, "filen:" + fName);
            holder.fileName.setText(fName + " " + (lyaddress == null ? "" : "(" + lyaddress + ")"));
            holder.fileTime.setText(DateConver.ConverToStringNYRSFM(datef));
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

    static class Holder {
        TextView fileName = null;
        TextView fileTime = null;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            waitingDialog("正在解析测量数据...");
            File files = listf.get(position);
            //解析文件
            readFile(files.getAbsolutePath(), position);
        }
    }

    private Handler uhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    //上传成功，更新本地数据上传状态
                    int result = SqliteUtils.getInstance(context).updateTaskStatus(hightProcess, CalcUtils.sub(Double.parseDouble(hightProcess), Double.parseDouble(td.getInitialValue())), td.getPointId());
                    if (result > 0) {
                        new AlertDialog.Builder(context)
                                .setTitle("系统提示")
                                .setCancelable(false)//按返回键不关闭窗口
                                .setIcon(R.drawable.success_small)
                                .setMessage("恭喜您完成本次测量任务，请在数据管理查看并上传测量结果。")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!ShowTaskInfo.instance.isFinishing())
                                            ShowTaskInfo.instance.finish();
                                        if (!CeLiangActivity.ceLiangActivityinstance.isFinishing())
                                            CeLiangActivity.ceLiangActivityinstance.finish();
                                        Intent intent = new Intent();
                                        intent.setClass(BlueToothFolder.this, ShowTaskInfo.class);
                                        intent.putExtra("State", true);
                                        intent.putExtra("potid", td.getPointId());
                                        startActivity(intent);
                                        finish();
                                    }
                                }).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * @param filePath Memory card Bluetooth path address
     */
    private int readFile(String filePath, final int position) {
        String htmlCode = "";
        if (filePath == null) return -1;
        File file = new File(filePath);
        if (file.isDirectory()) {
            return -1;
        } else {
            try {
                InputStream is = new FileInputStream(file);
                if (is != null) {
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    while ((line = br.readLine()) != null) {
                        htmlCode = htmlCode + line;
                    }
                    //如果数据解析成，则提示用户本次测量的数据，并对数据进行复核检查
                    int r = changeToJson(htmlCode);
                    if (r > 0) {
//                        showDialog(Arrays.asList(getArrayBcak()));
                        new AlertDialog.Builder(context)
                                .setTitle("系统提示")
                                .setIcon(R.drawable.warn_small)
                                .setMessage("本次测量： " + hightProcess + "   " + "\n初始值： " + td.getInitialValue() + "\n" + "本次测量与初始值差：" + CalcUtils.sub(Double.parseDouble(hightProcess), Double.parseDouble(td.getInitialValue())))
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //在这里将解析出来的数据放到MeasureData里，调用saveData方法存起来，再调用select方法显示出来(复核)
                                        int sr = sendToObject();
                                        if (sr > 0) {
                                            listf.remove(position);
                                            adapter.notifyDataSetChanged();
                                            new AlertDialog.Builder(context)
                                                    .setTitle("系统提示")
                                                    .setIcon(R.drawable.success_small)
                                                    .setMessage("测量数据保存成功")
                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            //更新任务状态
                                                            uhandler.sendEmptyMessage(1);
                                                        }
                                                    })
                                                    .show();
                                        }
                                        //startActivity(new Intent(BaseActivity.this, MainActivity.class));
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                    } else {
                        Toast.makeText(context, "数据解析错误，请确保蓝牙数据是否损坏", Toast.LENGTH_SHORT).show();
                        Logger.e(TAG, "数据解析错误，请确保蓝牙数据是否损坏");
                        return -1;
                    }
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(context, "您选择的文件不存在!", Toast.LENGTH_SHORT).show();
                Logger.e(TAG, "您选择的文件不存在!");
                return -1;
            } catch (IOException e) {
                Toast.makeText(context, "您选择的文件读取失败!", Toast.LENGTH_SHORT).show();
                Logger.e(TAG, "您选择的文件读取失败!");
                return -1;
            }
            return 1;
        }
    }


    /**
     * From html change to json and  show dialog
     *
     * @param htmlCode get one page's html code
     */
    private int changeToJson(String htmlCode) {
        try {
            l.clear();
            tmpObj = new JSONObject();
            jsonArray = new JSONArray();
            Document document = (Document) Jsoup.parse(htmlCode);
            Elements elements = document.select("tr");//elements.select("tr").size();
            for (Element ele : elements) {
                if (ele.select("th").size() > 2) {
                    for (int a = 0; a < ele.select("th").size(); a++) {
                        String titles = (ele.select("th").get(a).text()).trim();
                        String keys = (ele.select("td").get(a).text()).trim();
                        String oneLine = titles + ":" + keys + "\n";
                        if (titles != "") {
                            l.add(oneLine); // Log.i(TAG, "This is PPM .");
                            tmpObj.put(titles, keys);
                        }
                        if (titles.equals("高程")) {
                            hightProcess = keys;
                        }
                        if (titles.equals("收敛")) {
                            hightProcess = keys;
                        }
                    }
                } else {
                    String titles = (ele.select("th").get(0).text()).trim();
                    String keys = (ele.select("td").get(0).text()).trim();
                    String oneLine = titles + ":" + keys + "\n";
                    if (titles != "") {
                        l.add(oneLine);
                        tmpObj.put(titles, keys);

                        if (titles.equals("创建日期")) {
                            createTime = keys;
                        }
                    }
                }
            }
            jsonArray.put(tmpObj);
            personInfos = jsonArray.toString(); // 将JSONArray转换得到String
            return 1;
        } catch (JSONException e) {
            Logger.e(TAG, "数据解析错误，请确保蓝牙数据是否损坏。");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @param list get All Html Key Vules
     */
    private void showDialog(List<String> list) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("数据列表");
        String alllist = "";
        for (String str : list) {
            alllist = alllist + str + "\r\n";
        }
        builder.setMessage(alllist);
        AlertDialog b = builder.create();
        b.show();
    }

    private String[] getArrayBcak() {
        String[] a = new String[5];
        a[0] = personInfos;
        a[1] = "用户：" + DLApplication.userName == null ? "" : DLApplication.userName;//zrw  有问题
        a[2] = "DateTime:" + dateChange();
        a[3] = "高程:" + hightProcess;
        a[4] = "收敛:" + shoulProcess;
        return a;
    }

    private int sendToObject() {
        MeasureData measureData = new MeasureData();
        measureData.setTaskId(tId);
        measureData.setSources(personInfos);
        measureData.setGaocheng(hightProcess);
//        measureData.setShoulian(shoulProcess);
        measureData.setCldian(td.getMileageLabel());
        measureData.setCldianId(td.getMileageId());
        measureData.setCllicheng(td.getPointLabel());
        measureData.setCldianId(td.getPointId());
        measureData.setStatus("1");
        measureData.setDataType("1");
        measureData.setCltime(dateChange());
        measureData.setClren(DLApplication.userName != null ? DLApplication.userName : android.os.Build.MODEL);
        measureData.setUpdateTime("");
        measureData.setCreateTime(DateConver.getStringDate());
        measureData.setChushizhi(td.getInitialValue());
        measureData.setChazhi(CalcUtils.sub(Double.parseDouble(hightProcess), Double.parseDouble(td.getInitialValue())));
        SqliteUtils sdb = new SqliteUtils(this);
//        Logger.i(TAG, sdb.saveMeasure(measureData) + "插入结果");
        if (sdb.saveMeasure(measureData) == 1) {
            //startActivity(new Intent(BlueToothFolder.this, CeLiangActivity.class));
//            Intent intent = new Intent(BlueToothFolder.this, CeLiangActivity.class);
//            intent.putExtra("measureData", measureData);
//            startActivity(intent);
//            finish();
            Logger.i(TAG, "蓝牙传输测量数据保存成功.");
            return 1;
        } else {
            Logger.i(TAG, "蓝牙传输测量数据保存失败.");
            return -1;
        }
    }

    private String dateChange() {
        String[] ct = createTime.split(" ");
        String date = "";
        switch (ct[1]) {
            case "Jan":
                date = ct[2] + "-" + "1" + "-" + ct[0];
                break;
            case "Feb":
                date = ct[2] + "-" + "2" + "-" + ct[0];
                break;
            case "Mar":
                date = ct[2] + "-" + "3" + "-" + ct[0];
                break;
            case "Apr":
                date = ct[2] + "-" + "4" + "-" + ct[0];
                break;
            case "May":
                date = ct[2] + "-" + "5" + "-" + ct[0];
                break;
            case "Jun":
                date = ct[2] + "-" + "6" + "-" + ct[0];
                break;
            case "Jul":
                date = ct[2] + "-" + "7" + "-" + ct[0];
                break;
            case "Aug":
                date = ct[2] + "-" + "8" + "-" + ct[0];
                break;
            case "Sep":
                date = ct[2] + "-" + "9" + "-" + ct[0];
                break;
            case "Oct":
                date = ct[2] + "-" + "10" + "-" + ct[0];
                break;
            case "Nov":
                date = ct[2] + "-" + "11" + "-" + ct[0];
                break;
            case "Dec":
                date = ct[2] + "-" + "12" + "-" + ct[0];
                break;
        }
        return date;
    }
}
