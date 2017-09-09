package com.dxc.mycollector.model;

import java.io.Serializable;

/**
 * Created by gospel on 2017/8/30.
 * 存放解析后的测量信息
 */

public class MeasureData implements Serializable {
    private int id;
    private String taskId;
    private String cllicheng;
    private String cldian;
    private String cllichengId;
    private String cldianId;
    private String sources;
    private String clren;
    private String cltime;//测量时间-
    private String gaocheng;//高程值
    private String shoulian;//收敛值
    private String createTime;//创建时间
    private String updateTime;//更新时间
    private String dataType;//0是手动录入，1是蓝牙录入
    private String status;//0是已经上传成功，1否
    public String downloadId;

    public String getCllicheng() {
        return cllicheng;
    }

    public void setCllicheng(String cllicheng) {
        this.cllicheng = cllicheng;
    }

    public String getCldian() {
        return cldian;
    }

    public void setCldian(String cldian) {
        this.cldian = cldian;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getClren() {
        return clren;
    }

    public void setClren(String clren) {
        this.clren = clren;
    }

    public String getCltime() {
        return cltime;
    }

    public void setCltime(String cltime) {
        this.cltime = cltime;
    }

    public String getGaocheng() {
        return gaocheng;
    }

    public void setGaocheng(String gaocheng) {
        this.gaocheng = gaocheng;
    }

    public String getShoulian() {
        return shoulian;
    }

    public void setShoulian(String shoulian) {
        this.shoulian = shoulian;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getCllichengId() {
        return cllichengId;
    }

    public void setCllichengId(String cllichengId) {
        this.cllichengId = cllichengId;
    }

    public String getCldianId() {
        return cldianId;
    }

    public void setCldianId(String cldianId) {
        this.cldianId = cldianId;
    }

    @Override
    public String toString() {
        return "MeasureData{" +
                "id=" + id +
                ", taskId='" + taskId + '\'' +
                ", cllicheng='" + cllicheng + '\'' +
                ", cldian='" + cldian + '\'' +
                ", sources='" + sources + '\'' +
                ", clren='" + clren + '\'' +
                ", cltime='" + cltime + '\'' +
                ", gaocheng='" + gaocheng + '\'' +
                ", shoulian='" + shoulian + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", dataType='" + dataType + '\'' +
                ", status='" + status + '\'' +
                ", downloadId='" + downloadId + '\'' +
                '}';
    }
}
