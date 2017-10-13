/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.cadutils;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dxc.mycollector.R;
import com.dxc.mycollector.fragment.Constants;
import com.dxc.mycollector.utils.DateConver;
import com.gstar.android.GstarCadFilesActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by gospel on 2017/8/18.
 * About Login
 */
public class LocalCADFilesFragment extends Fragment {

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

    public static LocalCADFilesFragment newInstance(String s) {
        LocalCADFilesFragment localCADFilesFragment = new LocalCADFilesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARGS, s);
        localCADFilesFragment.setArguments(bundle);
        return localCADFilesFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        //设置ActionBar名称
        android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_plus);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);  //根据字面意思是显示类型为显示自定义
        actionBar.setDisplayShowCustomEnabled(true); //自定义界面是否可显示
        ((TextView) getActivity().findViewById(R.id.title_name)).setText(getString(R.string.item_cad));
        //以下代码用于去除阴影
        if (Build.VERSION.SDK_INT >= 21) {
            actionBar.setElevation(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.gas_fragment, container, false);
        View view = inflater.inflate(R.layout.gstarlocalfiles_activity, container, false);
        initBindData();
        initControl(view);
//        startActivity(new Intent(mContext, LocalFilesActivity.class));
        return view;
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

    private View.OnClickListener myClickListener = new View.OnClickListener() {

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
                getActivity().finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 初始化所有UI
     */
    private void initControl(View view) {
        try {
            buttonBack = (Button) view.findViewById(R.id.cad_buttonBack);
            buttonBack.setOnClickListener(myClickListener);
            textViewPath = (TextView) view.findViewById(R.id.textViewPath);
            textViewPath.setText(strCurrectPath);

            listViewShowData = (ListView) view.findViewById(R.id.listViewShowData);
            m_LocalFileModelsAdapter = new LocalFileModelsAdapter();
            listViewShowData.setAdapter(m_LocalFileModelsAdapter);
            listViewShowData.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
                final LocalFileModelsAdapter.ViewHolder holder;
                if (convertView == null) {
                    convertView = View.inflate(mContext, R.layout.localfiles_listitems, null);
                    holder = new LocalFileModelsAdapter.ViewHolder();
                    holder.imageViewFileIcon = (ImageView) convertView.findViewById(R.id.imageViewFileIcon);
                    holder.textViewFileName = (TextView) convertView.findViewById(R.id.textViewFileName);

                    // 隐藏的部分
                    holder.viewFileSizeAndTime = convertView.findViewById(R.id.viewFileSizeAndTime);
                    holder.textViewFileSize = (TextView) convertView.findViewById(R.id.textViewFileSize);
                    holder.textViewFileLastModifiedTime = (TextView) convertView.findViewById(R.id.textViewFileLastModifiedTime);
                    convertView.setTag(holder);
                } else {
                    holder = (LocalFileModelsAdapter.ViewHolder) convertView.getTag();
                }
                holder.viewFileSizeAndTime.setVisibility(View.GONE);
                final FileModel fileModel = m_ListFileModels.get(position);
                String fileName = fileModel.getFileName();
                String filePath = fileModel.getFilePath();
                String fileIcon = fileModel.getFileIcon();
                String fileSizeShow = fileModel.getFileSizeShow();
                String fileDate = fileModel.getFileDateShow();//DateConver.dateToStrLong(DateConver.strToDateLong(fileModel.getFileDateShow()));
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
}
