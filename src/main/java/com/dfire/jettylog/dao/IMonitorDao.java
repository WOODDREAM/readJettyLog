package com.dfire.jettylog.dao;

import com.dfire.jettylog.bean.DailyMonitor;
import com.dfire.jettylog.bean.Monitor;
import com.dfire.jettylog.bean.MonitorDetails;

import java.util.Date;
import java.util.List;

/**
 * User:huangtao
 * Date:2015-09-14
 * description：
 */
public interface IMonitorDao {

    /**
     * 级联表，插入Monitor和Monitor_details表
     * @param monitor
     * @throws Exception
     */
    void insertMonitor(Monitor monitor) throws Exception;

    void insertMonitor(Monitor monitor, List<MonitorDetails> monitorDetailsLsit) throws Exception;


    Monitor feathMonotorByName(String funtionName);

    List<Monitor> getMonitorListByData(Date date);

    /**
     *
     * @param monitorName 监控器名称
     * @param date 日志日期
     * @return
     * @throws Exception
     */
    void createTable(String monitorName,String date) throws Exception;

    /**
     *统计日期信息，存储于dailyMonitor表里
     * @param dailyMonitor 日志文件Monitor名称
     * @return
     */
    void insertDailyMonitor(DailyMonitor dailyMonitor) throws Exception;

    /**
     * 建立级联关系时，统一插入daily_Monitor、Monitor和Monitor_details表，测试只有后面两个表可以级联插入，待定
     * @param dailyMonitor
     * @throws Exception
     */
    void insertDailyMonitorByCSS(DailyMonitor dailyMonitor) throws Exception;
}
