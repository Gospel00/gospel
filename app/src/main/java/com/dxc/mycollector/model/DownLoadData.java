package com.dxc.mycollector.model;

/**
 * Created by gospel on 2017/8/30.
 * 下载测量任务-详细信息
 */

public class DownLoadData {
    private String taskId;
    private String userId;
    private String taskType;
    private String measureType;
    private String startTime;
    private String endTime;
    private String detail;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getMeasureType() {
        return measureType;
    }

    public void setMeasureType(String measureType) {
        this.measureType = measureType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "DownLoadData{" +
                "taskId='" + taskId + '\'' +
                ", userId='" + userId + '\'' +
                ", taskType='" + taskType + '\'' +
                ", measureType='" + measureType + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }

    class DetailData {
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
            return "DownLoadDetailData{" +
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
}
