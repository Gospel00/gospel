
package com.dxc.mycollector.taskDownload;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.dxc.mycollector.dbhelp.SqliteUtils;
import com.dxc.mycollector.logs.Logger;
import com.dxc.mycollector.model.TaskDetails;
import com.dxc.mycollector.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import com.dxc.mycollector.model.TaskInfo.DetailData;
import com.dxc.mycollector.model.TaskInfo;

/**
 * Created by gospel on 2017/8/18.
 * About 下载管理类
 */
public class DownLoadManager {

    String TAG = DownLoadManager.class.getSimpleName();
    //    private static final String pUrl = "http://106.38.157.46:48080/restcenter/restcenter/measureTaskService/getMeasureTasks";
    private static final String pUrl = "http://106.38.157.46:48080/restcenter/measureTaskService/getMeasureTasks";
    private static final String pKey = "administrator";
    private static final String upUrl = "http://106.38.157.46:48080/restcenter/measureTaskService/feedbackTask";
    private static final String upKey = "administrator";

    private Context mycontext;

    private ArrayList<DownLoader> taskList = new ArrayList<DownLoader>();

    private final int MAX_DOWNLOADING_TASK = 5; // 最大同时下载数

    private DownLoader.DownLoadSuccess downloadsuccessListener = null;

    /**
     * 服务器是否支持断点续传
     */
    private boolean isSupportBreakpoint = false;

    //线程池
    private ThreadPoolExecutor pool;
    //平台返回json串
    String resultJson = null;
    //平台返回json串
    String resultJsonupload = null;
    //控制平台接口请求
    int i = 1;
    int ui = 1;

    TaskInfo taskInfo = null;
    TaskDetails detailData = null;

    public DownLoadManager(Context context) {
        mycontext = context;
        Logger.i(TAG, "DownLoadManager init .");
        init(context);
        uploadMeasure(null);
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Gson gson = new Gson();
                    JsonArray jsonDatas = new JsonParser().parse(resultJson).getAsJsonObject().getAsJsonArray("data");
                    TaskInfo[] tempt = new TaskInfo[jsonDatas.size()];
                    Logger.i(TAG, "jsonDatas:" + jsonDatas.toString());
                    Logger.i(TAG, "master TaskInfo num:任务数:" + (jsonDatas.size()));
                    Map<TaskDetails, TaskInfo> maps = new HashMap<TaskDetails, TaskInfo>();
                    List<TaskDetails> ltd = new ArrayList<TaskDetails>();
                    int i1 = 0;
                    for (JsonElement element : jsonDatas) {
                        taskInfo = gson.fromJson(element, TaskInfo.class);
                        tempt[i1] = taskInfo;
                        i1++;
                        //遍历子任务
                        JsonArray jsonElements = new JsonParser().parse(element.toString()).getAsJsonObject().getAsJsonArray("detail");
                        TaskDetails[] temp = new TaskDetails[jsonElements.size()];
                        int i = 0;
                        for (JsonElement elementd : jsonElements) {
                            detailData = gson.fromJson(elementd, TaskDetails.class);
                            temp[i] = detailData;
                            i++;
                            maps.put(detailData, taskInfo);
                            ltd.add(detailData);
                        }
                        taskInfo.setDetail(temp);
                    }
                    Logger.i(TAG, "maps:::" + maps.toString());
                    Logger.i(TAG, "task Details info num:任务数:" + (i + 1));
                    //根据测量点保存测量任务信息
                    for (TaskDetails ti : ltd) {
                        //保存数据
                        int result = SqliteUtils.getInstance(mycontext).saveTaskInfo(maps.get(ti), ti);
                        Logger.i(TAG, "任务保存成功。测量详情：" + maps.get(ti).toString());
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private void init(final Context context) {
        new Thread() {//创建子线程进行网络访问的操作
            public void run() {
                try {
                    while (i > 0 && i < 10) {
                        resultJson = HttpUtils.getJSONObjectString(pKey, pUrl);// HttpUtils.doPost(null, textView.getText().toString());
                        if (resultJson == null || (resultJson.indexOf("FAIL") > -1)) {
                            //上传FAIL,重新请求，10次后不在
                            Logger.i(TAG, "平台任务接口返回失败，resultJson:" + resultJson);
                            Logger.i(TAG, i + " 次请求...");
                            handler.sendEmptyMessage(0);
                            //每2秒从平台下载一次任务
                            sleep(2000);
                            i++;
                        } else {
                            i = 0;
                            Logger.i(TAG, "平台任务接口返回成功，resultJson:" + resultJson);
                            //处理任务
                            handler.sendEmptyMessage(0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Handler uhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    //上传成功，更新本地数据上传状态
//                    int result = SqliteUtils.getInstance(mycontext).saveTaskInfo(maps.get(ti), ti);
//                    Logger.i(TAG, "任务保存成功。测量详情：" + maps.get(ti).toString());
                    break;
                default:
                    break;
            }
        }
    };


    public void uploadMeasure(final TaskDetails taskDetails) {
        new Thread() {//创建子线程进行网络访问的操作
            public void run() {
                try {
                    while (ui > 0 && ui < 10) {
                        resultJsonupload = HttpUtils.postJSONObjectString(upUrl, taskDetails);
                        if (resultJsonupload == null || (resultJson.indexOf("FAIL") > -1)) {
                            //下载任务接口返回FAIL,重新请求，3次后不在
                            Logger.i(TAG, "平台测量数据上传接口请求失败，resultJsonupload:" + resultJsonupload);
                            Logger.i(TAG, ui + " 次上传请求...");
                            uhandler.sendEmptyMessage(0);
                            //每2秒从平台下载一次任务
                            sleep(2000);
                            ui++;
                        } else {
                            ui = 0;
                            Logger.i(TAG, "平台测量数据上传成功，resultJsonupload:" + resultJsonupload);
                            //处理任务
                            uhandler.sendEmptyMessage(1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


//    /**
//     * (从数据库恢复下载任务信息)
//     *
//     * @param context 上下文
//     * @param userID  用户ID
//     */
//
//    private void recoverData(Context context, String userID) {
//        stopAllTask();
//        taskList = new ArrayList<DownLoader>();
//        DataKeeper datakeeper = new DataKeeper(context);
//        ArrayList<SQLDownLoadInfo> sqlDownloadInfoList = null;
//        if (userID == null) {
//            sqlDownloadInfoList = datakeeper.getAllDownLoadInfo();
//        } else {
//            sqlDownloadInfoList = datakeeper.getUserDownLoadInfo(userID);
//        }
//        if (sqlDownloadInfoList.size() > 0) {
//            int listSize = sqlDownloadInfoList.size();
//            for (int i = 0; i < listSize; i++) {
//                SQLDownLoadInfo sqlDownLoadInfo = sqlDownloadInfoList.get(i);
//                DownLoader sqlDownLoader = new DownLoader(context, sqlDownLoadInfo, pool, userID, isSupportBreakpoint, false);
//                sqlDownLoader.setDownLodSuccesslistener(downloadsuccessListener);
//                sqlDownLoader.setDownLoadListener("public", alltasklistener);
//                taskList.add(sqlDownLoader);
//            }
//        }
//    }
//
//
//    /**
//     * (设置下载管理是否支持断点续传)
//     *
//     * @param isSupportBreakpoint
//     */
//    public void setSupportBreakpoint(boolean isSupportBreakpoint) {
//        if ((!this.isSupportBreakpoint) && isSupportBreakpoint) {
//            int taskSize = taskList.size();
//            for (int i = 0; i < taskSize; i++) {
//                DownLoader downloader = taskList.get(i);
//                downloader.setSupportBreakpoint(true);
//            }
//        }
//        this.isSupportBreakpoint = isSupportBreakpoint;
//    }
//
//    /**
//     * (切换用户)
//     *
//     * @param userID 用户ID
//     */
//    public void changeUser(String userID) {
//        this.userID = userID;
//        SharedPreferences.Editor editor = sharedPreferences.edit();// 获取编辑器
//        editor.putString("UserID", userID);
//        editor.commit();// 提交修改
//        FileHelper.setUserID(userID);
//        recoverData(mycontext, userID);
//    }
//
//    public String getUserID() {
//        return userID;
//    }
//
//    /**
//     * (增加一个任务，默认开始执行下载任务)
//     *
//     * @param TaskID   任务号
//     * @param url      请求下载的路径
//     * @param fileName 文件名
//     * @return -1 : 文件已存在 ，0 ： 已存在任务列表 ， 1 ： 添加进任务列表
//     */
//    public int addTask(String TaskID, String url, String fileName) {
//        return addTask(TaskID, url, fileName, null);
//    }
//
//    /**
//     * (增加一个任务，默认开始执行下载任务)
//     *
//     * @param TaskID   任务号
//     * @param url      请求下载的路径
//     * @param fileName 文件名
//     * @param filepath 下载到本地的路径
//     * @return -1 : 文件已存在 ，0 ： 已存在任务列表 ， 1 ： 添加进任务列表
//     */
//    public int addTask(String TaskID, String url, String fileName, String filepath) {
//        if (TaskID == null) {
//            TaskID = fileName;
//        }
//        int state = getAttachmentState(TaskID, fileName, filepath);
//        if (state != 1) {
//            return state;
//        }
//
//        SQLDownLoadInfo downloadinfo = new SQLDownLoadInfo();
//        downloadinfo.setUserID(userID);
//        downloadinfo.setDownloadSize(0);
//        downloadinfo.setFileSize(0);
//        downloadinfo.setTaskID(TaskID);
//        downloadinfo.setFileName(fileName);
//        downloadinfo.setUrl(url);
//        if (filepath == null) {
//            downloadinfo.setFilePath(FileHelper.getFileDefaultPath() + "/(" + FileHelper.filterIDChars(TaskID) + ")" + fileName);
//        } else {
//            downloadinfo.setFilePath(filepath);
//        }
//        DownLoader taskDownLoader = new DownLoader(mycontext, downloadinfo, pool, userID, isSupportBreakpoint, true);
//        taskDownLoader.setDownLodSuccesslistener(downloadsuccessListener);
//        if (isSupportBreakpoint) {
//            taskDownLoader.setSupportBreakpoint(true);
//        } else {
//            taskDownLoader.setSupportBreakpoint(false);
//        }
//        taskDownLoader.start();
//        taskDownLoader.setDownLoadListener("public", alltasklistener);
//        taskList.add(taskDownLoader);
//        return 1;
//    }
//
//    /**
//     * 获取附件状态
//     *
//     * @param TaskID   任务号
//     * @param fileName 文件名
//     * @param filepath 下载到本地的路径
//     * @return -1 : 文件已存在 ，0 ： 已存在任务列表 ， 1 ： 添加进任务列表
//     */
//    private int getAttachmentState(String TaskID, String fileName, String filepath) {
//
//        int taskSize = taskList.size();
//        for (int i = 0; i < taskSize; i++) {
//            DownLoader downloader = taskList.get(i);
//            if (downloader.getTaskID().equals(TaskID)) {
//                return 0;
//            }
//        }
//        File file = null;
//        if (filepath == null) {
//            file = new File(FileHelper.getFileDefaultPath() + "/(" + FileHelper.filterIDChars(TaskID) + ")" + fileName);
//            if (file.exists()) {
//                return -1;
//            }
//        } else {
//            file = new File(filepath);
//            if (file.exists()) {
//                return -1;
//            }
//        }
//        return 1;
//    }
//
//    /**
//     * (删除一个任务，包括已下载的本地文件)
//     *
//     * @param taskID
//     */
//    public void deleteTask(String taskID) {
//        int taskSize = taskList.size();
//        for (int i = 0; i < taskSize; i++) {
//            DownLoader deletedownloader = taskList.get(i);
//            if (deletedownloader.getTaskID().equals(taskID)) {
//                deletedownloader.destroy();
//                taskList.remove(deletedownloader);
//                break;
//            }
//        }
//    }
//
//    /**
//     * (获取当前任务列表的所有任务ID)
//     *
//     * @return
//     */
//    public ArrayList<String> getAllTaskID() {
//        ArrayList<String> taskIDlist = new ArrayList<String>();
//        int listSize = taskList.size();
//        for (int i = 0; i < listSize; i++) {
//            DownLoader deletedownloader = taskList.get(i);
//            taskIDlist.add(deletedownloader.getTaskID());
//        }
//        return taskIDlist;
//    }
//
//    /**
//     * (获取当前任务列表的所有任务，以TaskInfo列表的方式返回)
//     *
//     * @return
//     */
//    public ArrayList<TaskInfo> getAllTask() {
//        ArrayList<TaskInfo> taskInfolist = new ArrayList<TaskInfo>();
//        int listSize = taskList.size();
//        for (int i = 0; i < listSize; i++) {
//            DownLoader deletedownloader = taskList.get(i);
//            SQLDownLoadInfo sqldownloadinfo = deletedownloader.getSQLDownLoadInfo();
//            TaskInfo taskinfo = new TaskInfo();
//            taskinfo.setFileName(sqldownloadinfo.getFileName());
//            taskinfo.setOnDownloading(deletedownloader.isDownLoading());
//            taskinfo.setTaskID(sqldownloadinfo.getTaskID());
//            taskinfo.setFileSize(sqldownloadinfo.getFileSize());
//            taskinfo.setDownFileSize(sqldownloadinfo.getDownloadSize());
//            taskInfolist.add(taskinfo);
//        }
//        return taskInfolist;
//    }
//
//    /**
//     * (根据任务ID开始执行下载任务)
//     *
//     * @param taskID
//     */
//    public void startTask(String taskID) {
//        int listSize = taskList.size();
//        for (int i = 0; i < listSize; i++) {
//            DownLoader deletedownloader = taskList.get(i);
//            if (deletedownloader.getTaskID().equals(taskID)) {
//                deletedownloader.start();
//                break;
//            }
//        }
//    }
//
//    /**
//     * (根据任务ID停止相应的下载任务)
//     *
//     * @param taskID
//     */
//    public void stopTask(String taskID) {
//        int listSize = taskList.size();
//        for (int i = 0; i < listSize; i++) {
//            DownLoader deletedownloader = taskList.get(i);
//            if (deletedownloader.getTaskID().equals(taskID)) {
//                deletedownloader.stop();
//                break;
//            }
//        }
//    }
//
//    /**
//     * (开始当前任务列表里的所有任务)
//     */
//    public void startAllTask() {
//        int listSize = taskList.size();
//        for (int i = 0; i < listSize; i++) {
//            DownLoader deletedownloader = taskList.get(i);
//            deletedownloader.start();
//        }
//    }
//
//    /**
//     * (停止当前任务列表里的所有任务)
//     */
//    public void stopAllTask() {
//        int listSize = taskList.size();
//        for (int i = 0; i < listSize; i++) {
//            DownLoader deletedownloader = taskList.get(i);
//            deletedownloader.stop();
//        }
//    }
//
//    /**
//     * (根据任务ID将监听器设置到相对应的下载任务)
//     *
//     * @param taskID
//     * @param listener
//     */
//    public void setSingleTaskListener(String taskID, DownLoadListener listener) {
//        int listSize = taskList.size();
//        for (int i = 0; i < listSize; i++) {
//            DownLoader deletedownloader = taskList.get(i);
//            if (deletedownloader.getTaskID().equals(taskID)) {
//                deletedownloader.setDownLoadListener("private", listener);
//                break;
//            }
//        }
//    }
//
//    /**
//     * (将监听器设置到当前任务列表所有任务)
//     *
//     * @param listener
//     */
//    public void setAllTaskListener(DownLoadListener listener) {
//        alltasklistener = listener;
//        int listSize = taskList.size();
//        for (int i = 0; i < listSize; i++) {
//            DownLoader deletedownloader = taskList.get(i);
//            deletedownloader.setDownLoadListener("public", listener);
//        }
//    }
//
//    /**
//     * (根据任务ID移除相对应的下载任务的监听器)
//     *
//     * @param taskID
//     */
//    public void removeDownLoadListener(String taskID) {
//        DownLoader downLoader = getDownloader(taskID);
//        if (downLoader != null) {
//            downLoader.removeDownLoadListener("private");
//        }
//    }
//
//    /**
//     * (删除监听所有任务的监听器)
//     */
//    public void removeAllDownLoadListener() {
//        int listSize = taskList.size();
//        for (int i = 0; i < listSize; i++) {
//            DownLoader deletedownloader = taskList.get(i);
//            deletedownloader.removeDownLoadListener("public");
//        }
//    }
//
//    /**
//     * (根据任务号获取当前任务是否正在下载)
//     *
//     * @param taskID
//     * @return
//     */
//    public boolean isTaskdownloading(String taskID) {
//        DownLoader downLoader = getDownloader(taskID);
//        if (downLoader != null) {
//            return downLoader.isDownLoading();
//        }
//        return false;
//    }
//
//    /**
//     * 根据附件id获取下载器
//     */
//    private DownLoader getDownloader(String taskID) {
//        for (int i = 0; i < taskList.size(); i++) {
//            DownLoader downloader = taskList.get(i);
//            if (taskID != null && taskID.equals(downloader.getTaskID())) {
//                return downloader;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 根据id获取下载任务列表中某个任务
//     */
//    public TaskInfo getTaskInfo(String taskID) {
//        DownLoader downloader = getDownloader(taskID);
//        if (downloader == null) {
//            return null;
//        }
//        SQLDownLoadInfo sqldownloadinfo = downloader.getSQLDownLoadInfo();
//        if (sqldownloadinfo == null) {
//            return null;
//        }
//        TaskInfo taskinfo = new TaskInfo();
//        taskinfo.setFileName(sqldownloadinfo.getFileName());
//        taskinfo.setOnDownloading(downloader.isDownLoading());
//        taskinfo.setTaskID(sqldownloadinfo.getTaskID());
//        taskinfo.setDownFileSize(sqldownloadinfo.getDownloadSize());
//        taskinfo.setFileSize(sqldownloadinfo.getFileSize());
//        return taskinfo;
//    }
}
