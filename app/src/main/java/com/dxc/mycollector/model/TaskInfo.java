package com.dxc.mycollector.model;

/**
 * Created by gospel on 2017/8/30.
 * 下载测量任务-详细信息
 */

public class TaskInfo {
    private int id;
    private String taskId;//任务ID
    private String userId;//用户名称
    private String taskType;//任务类型
    private String measureType;//量测设备
    private String startTime;//开始时间
    private String endTime;//结束时间
    private DetailData[] detail;//详细信息

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

    public DetailData[] getDetail() {
        return detail;
    }

    public void setDetail(DetailData[] detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "taskId='" + taskId + '\'' +
                ", userId='" + userId + '\'' +
                ", taskType='" + taskType + '\'' +
                ", measureType='" + measureType + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }

    public class DetailData {
        private String proName;//项目名称
        private String section;//所属标段
        private String mileageLabel;//测点里程
        private String mileageId;//测点里程ID
        private String pointLabel;//测量点
        private String pointId;//测量点ID
        private String initialValue;//初始值

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
