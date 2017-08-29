package com.dxc.mycollector.utils;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by sunyi on 2017/8/28.
 */
public class HttpUtils {
    /**
     * 访问数据库并返回JSON数据字符串
     *
     * @param params 向服务器端传的参数
     * @param url
     * @return
     * @throws Exception
     */
    public static String doPost(List<NameValuePair> params, String url)
            throws Exception {
        String result = null;
        // 获取HttpClient对象
        HttpClient httpClient = new DefaultHttpClient();
        // 新建HttpPost对象
        HttpPost httpPost = new HttpPost(url);
        if (params != null) {
            // 设置字符集
            HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            // 设置参数实体
            httpPost.setEntity(entity);
        }

         /*// 连接超时
         httpClient.getParams().setParameter(
                 CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
         // 请求超时
         httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
                 3000);*/
        // 获取HttpResponse实例
        HttpResponse httpResp = httpClient.execute(httpPost);
        // 判断是够请求成功
        if (httpResp.getStatusLine().getStatusCode() == 200) {
            // 获取返回的数据
            result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
        } else {
            Log.i("HttpPost", "HttpPost方式请求失败");
        }
        return result;
    }


    /**
     * 获取网络中的JSON数据
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String getJSONObjectString(String path)
            throws Exception {
        //List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String result = null;
        Map<String, String> map = null;
        URL url = new URL(path);
        // 利用HttpURLConnection对象，我们可以从网页中获取网页数据
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 单位为毫秒，设置超时时间为5秒
        conn.setConnectTimeout(15 * 1000);
        // HttpURLConnection对象是通过HTTP协议请求path路径的，所以需要设置请求方式，可以不设置，因为默认为get
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {// 判断请求码是否200，否则为失败
            InputStream is = conn.getInputStream(); // 获取输入流
            byte[] data = readStream(is); // 把输入流转换成字符串组
            result = new String(data); // 把字符串组转换成字符串
        }
        return result;
    }

    /**
     * 获取网络中的JSON数据
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> getJSONObject(String path)
            throws Exception {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map = null;
        URL url = new URL(path);
        // 利用HttpURLConnection对象，我们可以从网页中获取网页数据
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 单位为毫秒，设置超时时间为5秒
        conn.setConnectTimeout(15 * 1000);
        // HttpURLConnection对象是通过HTTP协议请求path路径的，所以需要设置请求方式，可以不设置，因为默认为get
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {// 判断请求码是否200，否则为失败
            InputStream is = conn.getInputStream(); // 获取输入流
            byte[] data = readStream(is); // 把输入流转换成字符串组
            String json = new String(data); // 把字符串组转换成字符串

            // 数据形式：{"total":2,"success":true,"arrayData":[{"id":1,"name":"张三"},{"id":2,"name":"李斯"}]}
            JSONObject jsonObject = new JSONObject(json); // 返回的数据形式是一个Object类型，所以可以直接转换成一个Object
            int total = jsonObject.getInt("count");
            String keywords = jsonObject.getString("keywords");

            // 里面有一个数组数据，可以用getJSONArray获取数组
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i); // 得到每个对象
                int id = item.getInt("id");
                String title = item.getString("title");
                String description = item.getString("description");
                int time = item.getInt("time");
                map = new HashMap<String, String>();
                map.put("id", id + "");
                map.put("title", title);
                map.put("description", description);
                map.put("time", time + "");
                list.add(map);
            }
        }
        return list;
    }

    private static byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();
        return bout.toByteArray();
    }
}
