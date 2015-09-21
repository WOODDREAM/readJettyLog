package com.dfire.jettylog.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User:huangtao
 * Date:2015-09-14
 * description：
 */
@Component
public class CombineString {

    private final static String contentDataRegExp = "([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))";
    private final static String runTimeRegExp = "^[0-9]\\d*$";
    private final static String functionRegExp = "\\d{1,4}[:][g][e][t]w*";
    private final static String percentageRegExp = "\\d{1,3}[%]$";
    private final static String timeRegExp = "\\d{2}[:]\\d{2}[:]\\d{2}";
    private final static String taskRegExp = "^[g][e][t] | ^[S][T][O][P]"; //匹配行的开始处
    private final static String startWithDatRegExp = "^\\d{4}[-]\\d{2}[-]\\d{2}";
    private final static String dataRegExp = "^\\d{4}[-]\\d{2}[-]\\d{2}$";//纯日期格式

    /**
     * 识别主表与从表所需的string
     *
     * @param tempString 传入的string
     * @return
     */
    public boolean identifyTempString(String tempString) {

        if (tempString.indexOf("running time (millis) =") != -1) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 解析是否是以日期开头的字符串
     * @param str
     * @return
     */
    public boolean recognizeStartWithData(String str){
        Pattern pat = Pattern.compile(startWithDatRegExp);
        Matcher mat = pat.matcher(str);
        boolean rs = mat.find();
        if (rs) {
            System.out.println("日期开头的字符串: " + str);
            return true;
        }
        return false;
    }

    public boolean recognizeDate(String str){
        Pattern pat = Pattern.compile(dataRegExp);
        Matcher mat = pat.matcher(str);
        boolean rs = mat.find();
        if (rs) {
            System.out.println("日期: " + str);
            return true;
        }
        return false;
    }
    /**
     * 解析是否包含日期
     *
     * @param str
     * @return
     */
    public boolean recognizeContentDate(String str) {
        Pattern pat = Pattern.compile(contentDataRegExp);
        Matcher mat = pat.matcher(str);
        boolean rs = mat.find();
        if (rs) {
            System.out.println("datawww: " + str);
            return true;
        }
        return false;
    }

    /**
     * 解析运行结束时间 HH:MM:SS
     *
     * @param str
     * @return
     */
    public boolean recognizeEndTime(String str) {
        Pattern pat = Pattern.compile(timeRegExp);
        Matcher mat = pat.matcher(str);
        boolean rs = mat.find();
        if (rs) {
            System.out.println("endTime: " + str);
            return true;
        }
        return false;
    }

    /**
     * 解析运行所需时间
     *
     * @param str
     * @return
     */
    public boolean recognizeRunTime(String str) {
        Pattern pat = Pattern.compile(runTimeRegExp);
        Matcher mat = pat.matcher(str);
        boolean rs = mat.find();
        if (rs) {
            System.out.println("runtime: " + str.toString());
            return true;
        }
        return false;
    }

    /**
     * 解析运行时间所占百分比
     *
     * @param str
     * @return
     */
    public boolean recognizePercentage(String str) {
        Pattern pat = Pattern.compile(percentageRegExp);
        Matcher mat = pat.matcher(str);
        boolean rs = mat.find();
        if (rs) {
            System.out.println("percentatge: " + str);

            return true;
        }
        return false;
    }

    /**
     * 解析运行函数所在行窜
     *
     * @param str
     * @return
     */
    public boolean recognizeGetFunction(String str) {
        Pattern pat = Pattern.compile(functionRegExp);
        Matcher mat = pat.matcher(str);

        boolean rs = mat.find();
        if (rs) {
            System.out.println("funtion: " + str);
            return true;
        }
        return false;
    }

    /***
     * 解析任务名称
     * @param str
     * @return
     */
    public boolean recognizeTask(String str){
        Pattern pat = Pattern.compile(taskRegExp);
        Matcher mat = pat.matcher(str);
        boolean rs = mat.find();
        if (rs) {
            System.out.println("funtion: " + str);
            return true;
        }
        return false;
    }
}


