package com.dxc.mycollector.model;

/**
 * Created by gospel on 2017/8/30.
 * 存放解析后的测量信息
 */

public class MeasureData {
    private String sources;
    private String clren;
    private String cltime;
    private String gaocheng;
    private String shoulian;
    private String dataType;//0是出数值，1是新测量的数据
    private String status;//0是已经上传成功，1否
    public String downloadId;

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
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

    @Override
    public String toString() {
        return "MeasureData{" +
                "sources='" + sources + '\'' +
//                ", cllicheng='" + cllicheng + '\'' +
//                ", cldian='" + cldian + '\'' +
                ", clren='" + clren + '\'' +
                ", cltime='" + cltime + '\'' +
                ", gaocheng='" + gaocheng + '\'' +
                ", shoulian='" + shoulian + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
