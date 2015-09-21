package com.dfire.jettylog.controller;

import com.dfire.jettylog.Constants;
import com.dfire.jettylog.file.ReadFile;
import com.dfire.jettylog.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User:huangtao
 * Date:2015-09-15
 * description：
 */
@Controller
public class ReadFileController {

    @Autowired
    private ReadFile readFile;

    private static int count = 0;

    /**
     * 下载并读取日志文件
     * @param monitorName 监控器名称，示例：watch_order_pay(注意不带monitor。统一制定)
     */
    public synchronized void startLogTask(String monitorName) {
        Date nowDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat(Constants.Task.SIMPLE_DATE_FORMAT);
        String nowTime = format.format(nowDate);
        StringBuffer dateBuffer = new StringBuffer();

        dateBuffer.append(nowTime);
//        char lastChar = dateBuffer.charAt(dateBuffer.length() - 1);
//        dateBuffer.delete(dateBuffer.length() - 1, dateBuffer.length());
//        int lastInt = lastChar;
//        //当天日志下载的为空,所以延迟一天执行
//        char newLast = (char) ((char) lastInt - 1);
//        dateBuffer.append(newLast);
        String localFileLocalPath = ReadFile.getFilePath(dateBuffer.toString(),monitorName);
        String url = ReadFile.getFileUrl(dateBuffer.toString(),monitorName);
        try {
            HttpUtil.downloadFile(localFileLocalPath, url);
            readFile.readLocalFile(localFileLocalPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 批量操作
     * @param monitorName 监控器名称，示例：watch_order_pay(注意不带monitor。统一制定)
     */
    public void batchTask(String monitorName) {
        //批量操作年月
        String nowTime = Constants.Task.START_DATE;
        StringBuffer dateBuffer = new StringBuffer();
        //添加"-",日志格式示例：2015-09-01
        dateBuffer.append("-");
        //任务数量
        if (count <= Constants.Task.TASK_NUMBER) {
            if(count <= 9){
                //如果小于9，添加"0"，匹配标准日志格式
                dateBuffer.append(nowTime).append("0");
            }
            dateBuffer.append(nowTime).append(count);
            String localFileLocalPath = ReadFile.getFilePath(dateBuffer.toString(),monitorName);
            String url = ReadFile.getFileUrl(dateBuffer.toString(),monitorName);
            try {
                HttpUtil.downloadFile(localFileLocalPath, url);
                readFile.readLocalFile(localFileLocalPath);
                count++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void startTask(String monitorName, String url) {
        StringBuffer pathBuffer = new StringBuffer();
        pathBuffer.append(Constants.File.PATH).append(monitorName);
        String fileLocalPath = pathBuffer.toString().trim();
        try {
            HttpUtil.downloadFile(fileLocalPath, ReadFile.getFileUrl(url,monitorName));
            readFile.readLocalFile(fileLocalPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
