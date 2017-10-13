/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.model;

import java.io.Serializable;

/**
 * Created by gospel on 2017/8/30.
 * 气体检查
 */

public class GasInfo implements Serializable {

    private int id;
    private String carbonDioxide;//二氧化碳
    private String carbonMonoxide;//一氧化碳
    private String methane;//甲烷
    private String hydrogenSulfide;//硫化氢
    private String checkTime;//检测试剂
    private String status;//0已上传，1未上传

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarbonDioxide() {
        return carbonDioxide;
    }

    public void setCarbonDioxide(String carbonDioxide) {
        this.carbonDioxide = carbonDioxide;
    }

    public String getCarbonMonoxide() {
        return carbonMonoxide;
    }

    public void setCarbonMonoxide(String carbonMonoxide) {
        this.carbonMonoxide = carbonMonoxide;
    }

    public String getMethane() {
        return methane;
    }

    public void setMethane(String methane) {
        this.methane = methane;
    }

    public String getHydrogenSulfide() {
        return hydrogenSulfide;
    }

    public void setHydrogenSulfide(String hydrogenSulfide) {
        this.hydrogenSulfide = hydrogenSulfide;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GasInfo{" +
                "id=" + id +
                ", carbonDioxide='" + carbonDioxide + '\'' +
                ", carbonMonoxide='" + carbonMonoxide + '\'' +
                ", methane='" + methane + '\'' +
                ", hydrogenSulfide='" + hydrogenSulfide + '\'' +
                ", checkTime='" + checkTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
