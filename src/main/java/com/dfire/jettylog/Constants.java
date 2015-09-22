package com.dfire.jettylog;

/**
 * User:huangtao
 * Date:2015-09-18
 * description：
 */
public class Constants {

   public class File{


       public static final String WATCH_ORDER_PAY_MONITOR_PATH =  "F:\\monitor\\watch_order_pay_monitor.log.";

       public static final String WATCH_ORDER_PAY_MONITOR_URL = "http://viewlog.2dfire.com/down.php?file=/var/log/jetty/logs/watch_order_pay_monitor.log%s&ip=10.161.129.69&hostname=weixin-meal_publish_10-161-129-69";

       public static final String STOP_WATCH_MONITOR_URL = "http://viewlog.2dfire.com/down.php?file=/var/log/jetty/logs/stopwatch_monitor.log%s&ip=10.161.129.69&hostname=weixin-meal_publish_10-161-129-69";

       public static final String STOP_WATCH_MONITOR_PATH = "F:\\monitor\\stopwatch_monitor.log.";

       public static final String PATH = "F:\\monitor\\";

   }
    public class Task{
        public static final String STARTTIME = "23:57:00";

        public static final String START_DATE = "2015-09";

        public static final int TASK_NUMBER = 2;

        public static final String STOP_WATCH_MONITOR = "stopwatch_monitor";

        public static final String WATCH_ORDER_PAY_MONITOR = "watch_order_pay_monitor";

        public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
               // SimpleDateFormat
    }
}
