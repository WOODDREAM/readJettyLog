package com.dfire.jettylog.bean;

import com.sun.istack.internal.NotNull;
import org.nutz.dao.entity.annotation.*;

/**
 * User:huangtao
 * Date:2015-09-22
 * description：
 */
@Table("stopwatch_monitor_details")
public class StopWatchMDetail {
    @Id
    private int id;
    @NotNull
    @Column("run_data")
    private String runDate;
    @Column("end_time")
    @NotNull
    private String endTime;
    @Column("pro_time")
    private int proTime;
    @Column("opt_time")
    private String optTime;
    @Column("request_path")
    @ColDefine(type= ColType.VARCHAR, width=100)
    private String requestPath;
    @Column("monitor_name")
    @NotNull
    private String monitorName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getProTime() {
        return proTime;
    }

    public void setProTime(int proTime) {
        this.proTime = proTime;
    }

    public String getOptTime() {
        return optTime;
    }

    public void setOptTime(String optTime) {
        this.optTime = optTime;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }
}
