package com.dxc.mycollector.model;

/**
 * Created by gospel on 2017/8/30.
 * 存放解析后的测量信息
 */

public class MeasureData {
    private String sources;
    private String cllicheng;
    private String cldian;
    private String clren;
    private String cltime;
    private String gaocheng;
    private String shoulian;
    private String status;//0是出数值，1是新测量的数据

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

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
                ", cllicheng='" + cllicheng + '\'' +
                ", cldian='" + cldian + '\'' +
                ", clren='" + clren + '\'' +
                ", cltime='" + cltime + '\'' +
                ", gaocheng='" + gaocheng + '\'' +
                ", shoulian='" + shoulian + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
