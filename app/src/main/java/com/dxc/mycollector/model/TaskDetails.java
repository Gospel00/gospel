/*
 * copyright (c)2018-8-15
 * DXC technology
 */

package com.dxc.mycollector.model;

/**
 * Created by gospel on 2017/8/30.
 * 下载测量任务详情
 */

public class TaskDetails {
    private String proName;
    private String section;
    private String mileageLabel;
    private String mileageId;
    private String pointLabel;
    private String pointId;
    private String initialValue;

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getMileageLabel() {
        return mileageLabel;
    }

    public void setMileageLabel(String mileageLabel) {
        this.mileageLabel = mileageLabel;
    }

    public String getMileageId() {
        return mileageId;
    }

    public void setMileageId(String mileageId) {
        this.mileageId = mileageId;
    }

    public String getPointLabel() {
        return pointLabel;
    }

    public void setPointLabel(String pointLabel) {
        this.pointLabel = pointLabel;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    @Override
    public String toString() {
        return "TaskDetails{" +
                "proName='" + proName + '\'' +
                ", section='" + section + '\'' +
                ", mileageLabel='" + mileageLabel + '\'' +
                ", mileageId='" + mileageId + '\'' +
                ", pointLabel='" + pointLabel + '\'' +
                ", pointId='" + pointId + '\'' +
                ", initialValue='" + initialValue + '\'' +
                '}';
    }
}
