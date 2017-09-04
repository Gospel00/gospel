package com.dxc.mycollector;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.MeasureData;
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
import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * About BlueToothFolder getReceiveFiles
 */
public class BlueToothFolder extends BaseActivity {
    Context context;
    private ListView fileList;//文件列表
    private String pathFile = "";//文件路径
    private TextView text;//解析按钮
    private List<String> listf;
    DLApplication myapp = null;
    /**
     * html change to json
     */
    List<String> l = new ArrayList();
    JSONObject tmpObj = null;
    JSONArray jsonArray = new JSONArray();
    String personInfos = "";
    String createTime = "";
    String hightProcess = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_bluetooth_fileslist_main_layout);
//        myapp = (DLApplication) getApplication();
        fileList = (ListView) findViewById(R.id.showbluetoothfilelistView);
        context = this;
        String aa = searchFile("");
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
        ((TextView) findViewById(R.id.title_name)).setText("数据管理");

       //以下代码用于去除阴影
        if(Build.VERSION.SDK_INT>=21){
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    text = (TextView) convertView.findViewById(R.id.jiexi);
                    convertView.setTag(holder);
                    text.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int i = 0;
                            try {
                                readFile(pathFile);
                            } catch (Exception e) {
                                String dff = e.toString();
                                e.printStackTrace();
                            }
                        }
                    });

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
        if(files !=null) {
            for (File file : files) {
//            if (file.getName().indexOf(keyword) >= 0) {
                String fn = file.getName();
                listf.add(fn);
                result += fn + "\n";// + "(" + file.getPath() + ")\n";
                pathFile = file.getPath();
//            }
            }
//            if (result.equals("")) {
//                result = "找不到文件!!";
//            }
            return result;
        }else
        {
            result = "找不到文件!!";
            return result;
        }

    }

    /**
     * @param filePath Memory card Bluetooth path address
     */
    private void readFile(String filePath) throws JSONException {
        String htmlCode = "";
        if (filePath == null) return;
        File file = new File(filePath);
        if (file.isDirectory()) {
            return;
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
                    changeToJson(htmlCode);
                    // showDialog(Arrays.asList(getArrayBcak()));
                    //在这里将解析出来的数据放到MeasureData里，调用saveData方法存起来，再调用select方法显示出来(复核)
                    sendToObject();

                }
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    }

    /**
     * From html change to json and  show dialog
     *
     * @param htmlCode get one page's html code
     */
    private void changeToJson(String htmlCode) throws JSONException {
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
        a[1] = "用户：" + "";//zrw  有问题
        a[2] = "DateTime:" + dateChange();
        a[3] = "高程:" + hightProcess;
        a[4] = "收敛:" + "0";
        return a;
    }

    private void sendToObject() {
        MeasureData measureData = new MeasureData();
        measureData.setSources(personInfos);
        measureData.setGaocheng(hightProcess);
        measureData.setShoulian("收敛");
        measureData.setCldian("测量点");
        measureData.setCllicheng("测量里程");
        measureData.setStatus("1");
        measureData.setDataType("1");
        measureData.setCltime(dateChange());
        measureData.setClren("");
        SqliteUtils sdb = new SqliteUtils(this);
        Logger.e(TAG, sdb.saveMeasure(measureData) + "插入结果");
        if (sdb.saveMeasure(measureData) == 1) {

            //startActivity(new Intent(BlueToothFolder.this, CeLiangActivity.class));
            Intent intent = new Intent(BlueToothFolder.this, CeLiangActivity.class);
            intent.putExtra("measureData", measureData);
            startActivity(intent);
            finish();
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
