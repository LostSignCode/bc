package com.zltel.broadcast.threeone.bean;

import java.util.Date;

public class ThreeoneSummary {
    private Integer id;

    private Integer scheduleId;

    private Date updateDate;

    private Date addDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getAddDate() {
        return addDate == null ? new Date(System.currentTimeMillis()) : addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }
}