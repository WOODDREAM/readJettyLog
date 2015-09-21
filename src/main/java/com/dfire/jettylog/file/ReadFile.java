package com.dfire.jettylog.file;


import com.dfire.jettylog.Constants;
import com.dfire.jettylog.service.IMonitorService;
import com.dfire.jettylog.service.impl.MonitorServiceImpl;
import com.dfire.jettylog.utils.CombineString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.logging.Logger;

/**
 * User:huangtao
 * Date:2015-09-11
 * description：
 */
@Component
public class ReadFile {
    private final Logger LOGGER = Logger.getLogger(ReadFile.class.getName());

    @Autowired
    private IMonitorService monitorService = new MonitorServiceImpl();
    @Autowired
    private CombineString combineString;

    public void readFileByChars(String fileName) {
        File logFile = new File(fileName);
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(logFile));
            int tempChar;
            char[] chars = new char[10];
//            byte[] bytes = new byte[10];
            StringBuffer stringBuffer = new StringBuffer();
            while ((tempChar = reader.read(chars)) != -1) {
                System.out.println("chars: " + chars[0]);
                char c = (char) tempChar;

                if (c != '\t') {
                    if (c != ' ') {
                        stringBuffer.append(c);
                    } else {
                        String str = stringBuffer.toString();
                        stringBuffer.delete(0, str.length());
                        if (stringBuffer.length() == 0) {
                            System.out.println("str:  =====" + str);
                        }
//                        return;
                    }
                }

            }
        } catch (IOException e) {
            LOGGER.config(fileName);
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param localFileLocalPath 本地文件地址
     * @param monitorName
     * @param date
     * @return
     * @throws Exception
     */
    public boolean readFileByLine(String localFileLocalPath, String monitorName, String date) throws Exception {
        File file = new File(localFileLocalPath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            if (monitorName.equals(Constants.Task.STOP_WATCH_MONITOR)) {
                //如果是stopwatch_monitor 分割String字符串为主串和子串
                while ((tempString = reader.readLine()) != null) {
                    String[] temStrList = tempString.split("\\{");
                    for (String tempStr : temStrList) {
                        monitorService.obtainString(tempStr, monitorName, date);
                    }
                }
            } else {
                while ((tempString = reader.readLine()) != null) {
                    monitorService.obtainString(tempString, monitorName, date);
                }
                monitorService.insertDailyMonitor();
            }
            reader.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 解析本地文件路径
     *
     * @param localFileLocalPath 本地文件路径
     * @return
     */
    public void readLocalFile(String localFileLocalPath) throws Exception {

        char tempChar;
        StringBuffer stringBuffer = new StringBuffer();
        String monitorName = null;
        String date = null;

        int strLength = localFileLocalPath.length() - 1;
        for (int i = 0; i <= strLength; i++) {
            tempChar = localFileLocalPath.charAt(i);
            stringBuffer.append(tempChar);
            //去除.log
            if (stringBuffer.toString().equals("log.")) {
                stringBuffer.delete(0, stringBuffer.length());
                continue;
            }
            //去除\
            if (tempChar == '\\') {
                stringBuffer.delete(0, stringBuffer.length());
                continue;
            }
            //去除.，取出monitor名称
            if (tempChar == '.') {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                monitorName = stringBuffer.toString();
                stringBuffer.delete(0, stringBuffer.length());
                continue;
            }
            //取出日期
            if (combineString.recognizeDate(stringBuffer.toString().trim())) {
                date = stringBuffer.toString().trim();
            }
            //如果读到路径末尾，开始解析文件
            if (i == strLength) {
                if (null != monitorName && null != date) {
                    monitorService.createTable(monitorName, date);
                    readFileByLine(localFileLocalPath, monitorName, date);
                    stringBuffer.delete(0, stringBuffer.length());//考虑到内存问题，本屌不懂Java什么时候回收内存，可能此处不用写，待定
                }
            }
        }
    }

    public static String getFilePath(String dateParam, String monitorName) {
        StringBuffer pathBuffer = new StringBuffer();
        if (monitorName.equalsIgnoreCase(Constants.Task.WATCH_ORDER_PAY_MONITOR)) {
            pathBuffer.append(Constants.File.WATCH_ORDER_PAY_MONITOR_PATH).append(dateParam);
        }
        if (monitorName.equalsIgnoreCase(Constants.Task.STOP_WATCH_MONITOR)) {
            pathBuffer.append(Constants.File.STOP_WATCH_MONITOR_PATH).append(dateParam);
        }
        return pathBuffer.toString().trim();
    }

    public static String getFileUrl(String dateParam, String monitorName) {
        if (monitorName.equalsIgnoreCase(Constants.Task.WATCH_ORDER_PAY_MONITOR)) {
            return String.format(Constants.File.WATCH_ORDER_PAY_MONITOR_URL, dateParam);
        }
        if (monitorName.equalsIgnoreCase(Constants.Task.STOP_WATCH_MONITOR)) {
            return String.format(Constants.File.STOP_WATCH_MONITOR_URL, dateParam);
        }
        return "";
    }
}
