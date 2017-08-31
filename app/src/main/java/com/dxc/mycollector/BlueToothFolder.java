package com.dxc.mycollector;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

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


        fileList = (ListView) findViewById(R.id.showbluetoothfilelistView);
        context = this;
        String aa = searchFile("");
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
        for (File file : files) {
//            if (file.getName().indexOf(keyword) >= 0) {
            String fn = file.getName();
            listf.add(fn);
            result += fn + "\n";// + "(" + file.getPath() + ")\n";
            pathFile = file.getPath();
//            }
        }
        if (result.equals("")) {
            result = "找不到文件!!";
        }
        return result;
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
                    showDialog(Arrays.asList(getArrayBcak(personInfos)));
                    //在这里将解析出来的数据放到MeasureData里，调用saveData方法存起来，再调用select方法显示出来(复核)

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

    public String[] getArrayBcak(String jsonCode) {
        String[] a = new String[5];
        a[0] = jsonCode;
        a[1] = "用户：";
        a[2] = "DateTime:" + createTime;
        a[3] = "高程:" + hightProcess;
        a[4] = "收敛:" + "0";
        return a;
    }
}
