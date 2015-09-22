package com.dfire.jettylog.service;

/**
 * User:huangtao
 * Date:2015-09-14
 * description：
 */
public interface IMonitorService {

    /**
     *
     * @param monitorName 监视器名称
     * @param date 日志生成日期
     * @throws Exception
     */
    void createTable(String monitorName,String date) throws Exception;

    /**
     *
     * @param tempString 读取日志一行
     * @param monitorName 监视器名称
     * @param date 日志生成日期
     * @throws Exception
     */
    void obtainString(String tempString,String monitorName,String date) throws Exception;

    /**
     * watch_order_pay日志需要统计子任务，暂时这么做吧
     * @throws Exception
     */
    void insertDailyMonitor() throws Exception;
}
