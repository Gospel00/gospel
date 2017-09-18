/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.cadutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dxc.mycollector.R;
import com.gstar.android.GstarCadFilesActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalFilesActivity extends Activity {
    private Context mContext;
    private Button buttonBack;
    private TextView textViewPath;
    private ListView listViewShowData;
    /**
     * 本地文件数据源
     */
    private List<FileModel> m_ListFileModels = null;
    /**
     * 本地文件数据源的适配器
     */
    private LocalFileModelsAdapter m_LocalFileModelsAdapter = null;
    /**
     * 当前显示的文件路径
     */
    private String strCurrectPath = "";
    private final static int FILE_OPEN = 10;
    private final static int FILE_COPY_OR_MOVE = 20;

    /**
     * 查询状态，true查询中，false查询结束
     */
    private boolean boolSerachStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.localfiles_activity);
        mContext = this;

        initBindData();
        initControl();
    }

    private void initBindData() {
        try {
            strCurrectPath = Environment.getExternalStorageDirectory().getPath() + "/Tunnel/cad/";//FileUtils.getStorageRootPath(mContext);
            if (!TextUtils.isEmpty(strCurrectPath)) {
                getLocalFileModels(true, strCurrectPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // This is what gets called on finishing a media piece to import
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode != RESULT_OK) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示目录下的文件
     *
     * @param strPath 当前目录
     */
    private void getLocalFileModels(boolean isShowProgress, final String strPath) {
        try {

            // 临时数据源，保证线程数据安全，防止ListView出现数据同步异常
            List<FileModel> listFileModelsTemp = new ArrayList<FileModel>();
            FileUtils.getFileModelListCurrent(mContext, new File(strPath), listFileModelsTemp);
            Message msg = handlerMain.obtainMessage();
            msg.what = 100;
            msg.obj = listFileModelsTemp;
            handlerMain.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handlerMain = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:// 获取文件列表
                    try {
                        List<FileModel> tempListFileModels = new ArrayList<FileModel>();
                        if (msg.obj != null) {
                            tempListFileModels = (List<FileModel>) msg.obj;
                        }
                        formatFileModels(tempListFileModels);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

            }
        }
    };

    /**
     * 预加载图纸文件的缩略图，并排序文件列表，加载并刷新数据
     *
     * @方法名称: addFileModelsIcon
     * @创建人: StoneJxn
     * @创建时间: 2016年1月6日上午9:11:16
     * @参数:
     * @返回值: void
     */
    private void formatFileModels(List<FileModel> listFileModels) {
        try {
            if (listFileModels != null && listFileModels.size() > 0) {
                int size = listFileModels.size();
                // 开始排序
                if (listFileModels != null && listFileModels.size() > 1) {
                    FileUtils.sortFileModelList(listFileModels, "fileName", true);
                }
            }
            if (m_LocalFileModelsAdapter != null) {
                if (listFileModels == null) {
                    listFileModels = new ArrayList<FileModel>();
                }
                if (m_ListFileModels == null) {
                    m_ListFileModels = new ArrayList<FileModel>();
                }
                m_ListFileModels = listFileModels;
                m_LocalFileModelsAdapter.clearSelect();
                m_LocalFileModelsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化所有UI
     */
    private void initControl() {
        try {
            buttonBack = (Button) this.findViewById(R.id.buttonBack);
            buttonBack.setOnClickListener(myClickListener);
            textViewPath = (TextView) findViewById(R.id.textViewPath);
            textViewPath.setText(strCurrectPath);

            listViewShowData = (ListView) this.findViewById(R.id.listViewShowData);
            m_LocalFileModelsAdapter = new LocalFileModelsAdapter();
            listViewShowData.setAdapter(m_LocalFileModelsAdapter);
            listViewShowData.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    String strFilePath = m_ListFileModels.get(arg2).getFilePath();
                    File openFile = new File(strFilePath);
                    // 本地图纸点击打开
                    if (openFile.isFile()) {
                        // 打开图纸
                        Intent intent = new Intent(mContext, GstarCadFilesActivity.class);
                        intent.putExtra("fileName", strFilePath);
                        startActivityForResult(intent, FILE_OPEN);
                    } else {
                        strCurrectPath = strFilePath;
                        textViewPath.setText(strCurrectPath);
                        getLocalFileModels(true, strCurrectPath);
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OnClickListener myClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.buttonBack) {
                goBackPath();
            }
        }
    };

    /**
     * 后退
     */
    private void goBackPath() {
        try {
            if (!FileUtils.isStorageRootPath(mContext, strCurrectPath)) {
                strCurrectPath = FileUtils.getStorageBackPath(mContext, strCurrectPath);
                textViewPath.setText(strCurrectPath);
                getLocalFileModels(false, strCurrectPath);
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 本地文件适配器
     *
     * @创建时间： 2014-6-12 上午9:24:10
     */
    public class LocalFileModelsAdapter extends BaseAdapter {

        /**
         * 行对象所包含的View对象集合
         */
        private class ViewHolder {
            public ImageView imageViewFileIcon;
            public TextView textViewFileName;
            // 隐藏的布局
            public View viewFileSizeAndTime;
            public TextView textViewFileSize;
            public TextView textViewFileLastModifiedTime;
        }

        private List<String> selectFilePath;
        private List<String> selectPosition;

        public LocalFileModelsAdapter() {
            selectFilePath = new ArrayList<String>();
            selectPosition = new ArrayList<String>();
        }

        public List<String> getSelectFilePath() {
            return selectFilePath;
        }

        public boolean checkSelectPosition(int position) {
            if (selectPosition != null && selectPosition.size() > 0) {
                String strPosition = String.valueOf(position);
                return selectPosition.contains(strPosition);
            }
            return false;
        }

        public List<String> getSelectPosition() {
            return selectPosition;
        }

        public void setSelectPosition(int position, String filePath) {
            try {
                if (m_ListFileModels.size() > 0 && m_ListFileModels.size() > position && position >= 0) {
                    String strPosition = String.valueOf(position);
                    if (selectPosition.contains(strPosition)) {// 重复选择时移除
                        selectPosition.remove(strPosition);
                        selectFilePath.remove(filePath);
                    } else {// 不重复选择添加
                        selectPosition.add(strPosition);
                        selectFilePath.add(filePath);
                    }
                    // 顺序取反，将最后的最先使用
                    Collections.reverse(selectPosition);
                    Collections.reverse(selectFilePath);
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void removeSelectPosition(int position, String filePath) {
            try {
                if (m_ListFileModels.size() > 0 && m_ListFileModels.size() > position && position >= 0) {
                    String strPosition = String.valueOf(position);
                    selectPosition.remove(strPosition);
                    selectFilePath.remove(filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void clearSelect() {
            selectFilePath.clear();
            selectPosition.clear();
        }

        @Override
        public int getCount() {
            if (m_ListFileModels != null) {
                return m_ListFileModels.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return m_ListFileModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            try {
                final ViewHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.localfiles_listitems, null);
                    holder = new ViewHolder();
                    holder.imageViewFileIcon = (ImageView) convertView.findViewById(R.id.imageViewFileIcon);
                    holder.textViewFileName = (TextView) convertView.findViewById(R.id.textViewFileName);

                    // 隐藏的部分
                    holder.viewFileSizeAndTime = convertView.findViewById(R.id.viewFileSizeAndTime);
                    holder.textViewFileSize = (TextView) convertView.findViewById(R.id.textViewFileSize);
                    holder.textViewFileLastModifiedTime = (TextView) convertView.findViewById(R.id.textViewFileLastModifiedTime);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.viewFileSizeAndTime.setVisibility(View.GONE);
                final FileModel fileModel = m_ListFileModels.get(position);
                String fileName = fileModel.getFileName();
                String filePath = fileModel.getFilePath();
                String fileIcon = fileModel.getFileIcon();
                String fileSizeShow = fileModel.getFileSizeShow();
                String fileDate = fileModel.getFileDateShow();
                boolean isFile = fileModel.isFile();
                if (isFile) { // 是个文件
                    holder.viewFileSizeAndTime.setVisibility(View.VISIBLE);
                    holder.textViewFileSize.setText(fileSizeShow);
                    holder.textViewFileLastModifiedTime.setText(fileDate);
                    holder.imageViewFileIcon.setImageResource(R.drawable.icon_dwg);
                } else {// 是个文件夹
                    holder.viewFileSizeAndTime.setVisibility(View.GONE);
                    holder.imageViewFileIcon.setImageResource(R.drawable.icon_folder);
                }

                holder.textViewFileName.setText(fileName);
                convertView.setTag(holder);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return convertView;
        }
    }

    private void openFileByGstarSDK(Context context, String filePath) {
        try {
            // 打开图纸
            Intent intent = new Intent(context, GstarCadFilesActivity.class);
            intent.putExtra("fileName", filePath);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
