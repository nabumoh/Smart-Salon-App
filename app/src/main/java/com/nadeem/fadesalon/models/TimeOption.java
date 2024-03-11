package com.nadeem.fadesalon.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

/* class to handle setting work time Routine in a day*/
@IgnoreExtraProperties
public class TimeOption {

    private Date startTime;
    private Date endTime;

    public TimeOption() {
    }

    public TimeOption(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
