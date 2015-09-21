package com.dfire.jettylog.task;

import com.dfire.jettylog.controller.ReadFileController;
import com.dfire.jettylog.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User:huangtao
 * Date:2015-09-17
 * description：
 */
@Component
public class ScheduleTask extends TimerTask {
    //定时：一天
    long daySpan = 1 * 60 * 1000;

    //每天零晨执行任务
    final SimpleDateFormat dataForm = new SimpleDateFormat("yyyy-mm-dd '23:10:00'");
    private Date startTime;
    @Autowired
    private HttpUtil httpUtil;
    @Autowired
    private ReadFileController readFileController;

    private Timer timer = new Timer();

    public void executeTask(final String path, final String url) throws ParseException {
        startTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(dataForm.format(new Date()));
        // 以每24小时执行一次
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println("scheduleTask start :++++++++++++++++");
                    readFileController.startWatchOrderPayLogTask();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, startTime, daySpan);

    }

    @Override
    public void run() {
        System.out.println("++++++++++++++++++++++++++++++++");
//        readFileController.startWatchOrderPayLogTask();
        readFileController.batchTaskForWatchOrderPay();
    }

}
