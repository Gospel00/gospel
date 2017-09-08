/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.utils;

import android.os.Environment;

import com.dxc.mycollector.logs.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @desc 文件处理类
 * Created by Gospel on 2017/9/7 13:56
 * DXC technology
 */

public class FilesUtils {
    /**
     * 获取目录下所有文件(按时间排序)
     *
     * @param path
     * @return
     */
    public static List<File> listFileSortByModifyTime(String path) {
        List<File> list = searchFile(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;//最后修改的文件在前
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;//小文件在前
                    }
                }
            });
        }
        return list;
    }

    /**
     * 获取目录下所有文件
     *
     * @param realpath
     * @param files
     * @return
     */
    public static List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), files);
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }

    /**
     * 读取蓝牙文件夹
     *
     * @return
     */
    public static List<File> searchFile(String realpath, List<File> files) {
        File[] filearr = new File(realpath).listFiles();
        if (filearr != null && filearr.length > 0) {
            for (File file : filearr) {
                String fn = file.getName();
                Logger.i("FilesUtils", "filename:" + fn);
                files.add(file);
            }
            return files;
        } else {
            return files;
        }
    }
}
