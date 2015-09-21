package com.dfire.jettylog.service;

import java.util.List;

/**
 * User:huangtao
 * Date:2015-09-14
 * description：
 */
public interface IMonitorService {

    void obtainString(String tempString,String monitorName,String date) throws Exception;

    void insertMonitor(List<String> stringList,String monitorName) throws Exception;

    void createTable(String monitorName,String date) throws Exception;

    void insertDailyMonitor() throws Exception;
}
