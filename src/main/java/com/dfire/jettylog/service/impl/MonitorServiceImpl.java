package com.dfire.jettylog.service.impl;

import com.dfire.jettylog.Constants;
import com.dfire.jettylog.bean.DailyMonitor;
import com.dfire.jettylog.bean.Monitor;
import com.dfire.jettylog.bean.MonitorDetails;
import com.dfire.jettylog.bean.StopWatchMDetail;
import com.dfire.jettylog.dao.IMonitorDao;
import com.dfire.jettylog.service.IMonitorService;
import com.dfire.jettylog.utils.CombineString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User:huangtao
 * Date:2015-09-14
 * description：
 */
@Service
public class MonitorServiceImpl implements IMonitorService {

    @Autowired
    private CombineString combineString;

    @Autowired
    private IMonitorDao monitorDao;

    private int monitorCount;
    private int firstFailedCount;
    private int secondFailedCount;
    private int succeedTime;
    private int failedTime;
    private int firstFailedTime;
    private int secondFailedTime;
    private String monitorDate = null;
    private String dailyMonitorName = null;

    private List<String> stringList = new LinkedList<String>();

    private DailyMonitor dailyMonitor = new DailyMonitor();


    /**
     * 过滤读取的内容
     *
     * @param tempString
     * @param monitorName
     * @param date
     * @throws Exception
     */
    @Override
    public void obtainString(String tempString, String monitorName, String date) throws Exception {

        monitorDate = date;
        dailyMonitorName = monitorName;
        if (monitorName.equals(Constants.Task.WATCH_ORDER_PAY_MONITOR)) {
            if (null != tempString.trim() && !tempString.isEmpty()) {
                if (!tempString.startsWith("-----------------------------------------")
                        && !tempString.startsWith("ms     %     Task name")) {
                    stringList.add(tempString);
                }
            } else {
                insertWatchOrderPayMonitor(stringList, monitorName);
                stringList.clear();
            }
        }
        if (monitorName.equals(Constants.Task.STOP_WATCH_MONITOR)) {
            insertStopWatchMonitor(tempString.trim(), monitorName);
            stringList.clear();
        }
    }

    private void insertStopWatchMonitor(String string, String monitorName) throws Exception {
        if (string.isEmpty()) {
            return;
        } else {
            String[] tempString = string.split(" \\{");
            StopWatchMDetail stopWatchMonitor = new StopWatchMDetail();
            String endTime = null;
            for (String temp : tempString) {
                if (combineString.recognizeStartWithData(temp.toString().trim())) {
                    endTime = temp.toString().trim().substring(11, 19);
                }else {
                    stopWatchMonitor = combineSubordinateTableSW(temp.trim());
                }
                stopWatchMonitor.setMonitorName(monitorName);
                stopWatchMonitor.setEndTime(endTime);
            }
            monitorDao.insertStopWatchMonitor(stopWatchMonitor);
        }
    }

    /**
     * 首行记录到主表，次行记录到子表，统计数据到日常表
     *
     * @param stringList
     * @param monitorName
     * @throws Exception
     */
    public void insertWatchOrderPayMonitor(List<String> stringList, String monitorName) throws Exception {
        //list不能为空
        List<MonitorDetails> monitorDetailsList = new LinkedList<MonitorDetails>();
        if (stringList.isEmpty()) {
            return;
        } else {
            Monitor monitor = new Monitor();
            int childTask = 0;
            for (String tempString : stringList) {
                //第一条为主表所需字符串
                if (childTask == 0) {
                    monitor = combinePrimaryTable(tempString);
                } else {
                    //解析子表
                    monitorDetailsList.add(combineSubordinateTableWOP(tempString));
                }
                childTask++;
            }
            if (monitor != null) {
                int newChildTask = --childTask;
                monitor.setChildNumber(newChildTask);
                if (newChildTask != 0) {
                    monitor.setAverageTime(monitor.getRunTime() / newChildTask);
                }
                monitor.setMonitorDetailsList(monitorDetailsList);
                monitor.setMonitorName(monitorName);

                //统计日常表所需数据
                createDailyMonitorDetails(newChildTask, monitor);

                monitorDao.insertMonitor(monitor);
            }
//            if (null != monitorDetailsList && null != monitor) {
//                monitorDao.insertMonitor(monitor, monitorDetailsList);
//            }
            else {
                //错误处理
                System.out.println("不和法字符串!!!!");
            }
        }
    }

    /**
     * 统计数据
     *
     * @param newChildTask
     * @param monitor
     */
    private void createDailyMonitorDetails(int newChildTask, Monitor monitor) {
        if (null != monitor && 0 != monitor.getMonitorDetailsList().size()) {
            monitorCount += newChildTask;
            //失败任务的第一步
            if (newChildTask == 1) {
                if (null != monitor.getMonitorDetailsList().get(0)) {

                    firstFailedCount += newChildTask;
                    firstFailedTime += monitor.getMonitorDetailsList().get(0).getRunTime();
                }
            }
            //失败任务的第二步
            if (newChildTask == 2) {
                if (null != monitor.getMonitorDetailsList().get(1)) {

                    firstFailedCount += newChildTask;
                    firstFailedTime += monitor.getMonitorDetailsList().get(0).getRunTime();
                    secondFailedCount += newChildTask;
                    secondFailedTime += monitor.getMonitorDetailsList().get(1).getRunTime();
                }
            }
            //成功任务
            if (newChildTask >= 3) {
                succeedTime += monitor.getRunTime();
            } else {
                failedTime += monitor.getRunTime();
            }
        }
    }


    /**
     * 组装主表实体
     *
     * @param tempString
     */
    private Monitor combinePrimaryTable(String tempString) {
        Monitor monitor = new Monitor();
        StringBuffer stringBuffer = new StringBuffer();
        char tempChar;
        int strLength = tempString.length();
        for (int i = 0; i <= strLength; i++) {
            if (i != strLength && (tempChar = tempString.charAt(i)) != ' '
                    && tempChar != '\'') {
                stringBuffer.append(tempChar);
            } else {
                if (null != stringBuffer.toString() && !stringBuffer.toString().isEmpty()) {

                    if (combineString.recognizeDate(stringBuffer.toString().trim())) {
                        monitor.setRunDate(stringBuffer.toString().trim());
                    }
                    if (combineString.recognizeEndTime(stringBuffer.toString().trim())) {
                        monitor.setEndTime(stringBuffer.toString().trim().substring(0, 8));
                    }
                    if (combineString.recognizeTask(stringBuffer.toString().trim())) {
                        monitor.setTaskName(stringBuffer.toString().trim());
                    }
                    if (combineString.recognizeRunTime(stringBuffer.toString().trim())) {
                        String time = stringBuffer.toString().trim();
                        String newStr = time.replaceFirst("^0*", "");
                        System.out.println("time: " + newStr);
                        if (null != newStr && !newStr.isEmpty()) {
                            monitor.setRunTime(Integer.valueOf(newStr));
                        }
                    }
                    stringBuffer.delete(0, strLength);
                }
            }
        }
        return monitor;
    }

    /**
     * 组装watch_order_pay从表实体
     *
     * @param tempString
     */
    private MonitorDetails combineSubordinateTableWOP(String tempString) {

        MonitorDetails monitorDetails = new MonitorDetails();
        StringBuffer stringBuffer = new StringBuffer();
        char tempChar;
        int strLength = tempString.length();
        for (int i = 0; i <= strLength; i++) {
            if (i != strLength && (tempChar = tempString.charAt(i)) != ' ') {
                stringBuffer.append(tempChar);
            } else {
                if (null != stringBuffer.toString() && !stringBuffer.toString().isEmpty()) {
                    if (combineString.recognizeDate(stringBuffer.toString().trim())) {
                        monitorDetails.setRunDate(stringBuffer.toString().trim());
                        stringBuffer.delete(0, stringBuffer.length());
                        continue;
                    }
                    if (combineString.recognizeRunTime(stringBuffer.toString().trim())) {
                        monitorDetails.setRunTime(Integer.valueOf(stringBuffer.toString().trim()));
                        stringBuffer.delete(0, stringBuffer.length());
                        continue;
                    }
                    if (combineString.recognizeEndTime(stringBuffer.toString().trim())) {
                        monitorDetails.setEndTime(Time.valueOf(stringBuffer.toString().trim()));
                    }
                    if (combineString.recognizePercentage(stringBuffer.toString().trim())) {
                        String newStr = stringBuffer.toString().trim().replaceFirst("^0*", "");
                        if (null != newStr && !newStr.isEmpty()) {
                            monitorDetails.setTimePercent(newStr);
                        }
                        stringBuffer.delete(0, stringBuffer.length());
                        continue;
                    }
                    if (combineString.recognizeGetFunction(stringBuffer.toString().trim())) {
                        Map map = fetchAttribute(stringBuffer.toString().trim());
                        if (map != null) {
                            assert monitorDetails != null;
                            monitorDetails.setTaskName((String) map.get("taskName"));
                            monitorDetails.setTaskId((String) map.get("taskId"));
                            stringBuffer.delete(0, stringBuffer.length());
                            continue;
                        }
                    }
                    //如果没有一个匹配到
                    stringBuffer.delete(0, strLength);
                }
            }
        }
        return monitorDetails;
    }


    /**
     * 组装stopwatch子表实体
     *
     * @param tempString
     * @return
     */
    private StopWatchMDetail combineSubordinateTableSW(String tempString) {
        StopWatchMDetail monitorDetails = new StopWatchMDetail();
        String[] detailsList = tempString.trim().split(",");
        StringBuffer strBuffer = new StringBuffer();
        String[] childStr;
        for (String detailStr : detailsList) {
            if (detailStr.contains("pro_time")) {
                childStr = detailStr.trim().split("\":");
                monitorDetails.setProTime(Integer.parseInt(childStr[1].trim()));
                strBuffer.delete(0, strBuffer.length());
                continue;
            }
            if (detailStr.contains("opt_time")) {
                childStr = detailStr.trim().split("\":\"");
                strBuffer.append(childStr[1].trim());

                monitorDetails.setRunDate(strBuffer.substring(0, 10).toString().trim());
                monitorDetails.setOptTime(strBuffer.substring(strBuffer.length() - 10, strBuffer.length() - 1).toString().trim());
                strBuffer.delete(0, strBuffer.length());
                continue;
            }

            if (detailStr.contains("request_path")) {
                childStr = detailStr.trim().split(":");
                strBuffer.append(childStr[1].trim());
                strBuffer.delete(0, 1);
                strBuffer.delete(strBuffer.length() - 1, strBuffer.length());
                monitorDetails.setRequestPath(strBuffer.toString().trim());
                strBuffer.delete(0, strBuffer.length());
                continue;
            }
        }
        return monitorDetails;
    }

    /**
     * 解析连续字符串
     *
     * @param tempString
     * @return
     */
    private Map fetchAttribute(String tempString) {

        Map<String, String> propertyMap = new HashMap<String, String>();
        char tempChar;
        StringBuffer stringBuffer = new StringBuffer();
        int strLength = tempString.length() - 1;
        for (int i = 0; i <= strLength; i++) {
            tempChar = tempString.charAt(i);
            if (i != strLength && tempChar != ':' && tempChar != '(' && tempChar != ')') {
                stringBuffer.append(tempChar);
            } else {
                if (null != stringBuffer.toString() && !stringBuffer.toString().trim().isEmpty()) {
                    if (combineString.recognizeRunTime(stringBuffer.toString().trim())) {
                        //暂时不做处理
                        stringBuffer.delete(0, stringBuffer.length());
                        continue;
                    }
                    if (combineString.recognizeTask(stringBuffer.toString().trim())) {
                        propertyMap.put("taskName", stringBuffer.toString().trim());
                    } else {
                        if (stringBuffer.toString().trim() != "null") {
                            propertyMap.put("taskId", stringBuffer.toString().trim());
                        }
                    }
                    stringBuffer.delete(0, stringBuffer.length());
                }

            }
        }
        return propertyMap;
    }

    public void createTable(String monitorName, String date) throws Exception {

        try {
            //创建表
            if (monitorName.equals(Constants.Task.STOP_WATCH_MONITOR)) {
                monitorDao.createStopWatchTable(monitorName, date);
            }
            if (monitorName.equals(Constants.Task.WATCH_ORDER_PAY_MONITOR)) {
                monitorDao.createWatchOrderPayTable(monitorName, date);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

    @Override
    public void insertDailyMonitor() throws Exception {
        try {
            if (createDailyMonitor()) {
                monitorDao.insertDailyMonitor(dailyMonitor);
                dailyMonitor = null;
                dailyMonitor = new DailyMonitor();
                monitorCount = 0;
                firstFailedCount = 0;
                firstFailedTime = 0;
                secondFailedCount = 0;
                secondFailedTime = 0;
                succeedTime = 0;
                failedTime = 0;
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean createDailyMonitor() throws Exception {

        try {
            dailyMonitor.setRunDate(monitorDate);
            dailyMonitor.setSucceedTime(succeedTime);
            dailyMonitor.setMonitorCount(monitorCount);
            dailyMonitor.setMonitorName(dailyMonitorName);
            dailyMonitor.setFirstFailedCount(firstFailedCount);
            dailyMonitor.setFirstFailedTime(firstFailedTime);
            dailyMonitor.setSecondFailedCount(secondFailedCount);
            dailyMonitor.setSecondFailedTime(secondFailedTime);
            if (monitorCount != 0) {
                dailyMonitor.setSucceedPercent(String.valueOf(100 * (monitorCount - (firstFailedCount)) / monitorCount) + "%");
                dailyMonitor.setSecondFailedPercent(String.valueOf((100 * secondFailedCount / monitorCount)) + "%");
                dailyMonitor.setFirstFailedPercent(String.valueOf((100 * firstFailedCount / monitorCount)) + "%");
                dailyMonitor.setFailedPercent(String.valueOf((100 * firstFailedCount) / monitorCount) + "%");
            }
            if (firstFailedCount != 0) {
                dailyMonitor.setFirtFailedAverageTime(firstFailedTime / firstFailedCount);
                dailyMonitor.setFailedAverageTime(failedTime / (firstFailedCount + secondFailedCount));
            }
            if (secondFailedCount != 0) {
                dailyMonitor.setSecondFailedAverageTime(secondFailedTime / secondFailedCount);
            }
            if (firstFailedCount != monitorCount) {
                dailyMonitor.setSucceedAverageTime(succeedTime / (monitorCount - firstFailedCount));
            }
            return true;
        } catch (Exception e) {
            throw e;
        }
    }
}
