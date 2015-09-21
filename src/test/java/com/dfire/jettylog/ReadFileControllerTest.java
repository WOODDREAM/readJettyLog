package com.dfire.jettylog;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.LinkedList;
import java.util.List;

/**
 * User:huangtao
 * Date:2015-09-15
 * description：
 */
public class ReadFileControllerTest extends BaseControllerTest {

    @Before
    public void init(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(readFileController).build();
    }

    @Test
    public void testReadFile(){
        String s1 = "F:\\watch_order_pay_monitor.log.2015-09-10";
        String s2 = "F:\\watch_order_pay_monitor.log.2015-09-11";
        String s3 = "F:\\watch_order_pay_monitor.log.2015-09-12";
        String s4 = "F:\\watch_order_pay_monitor.log.2015-09-13";
        String s5 = "F:\\watch_order_pay_monitor.log.2015-09-14";
        String s6 = "F:\\watch_order_pay_monitor.log.2015-09-15";
        String s = "F:\\111.txt";
        List<String> stringList = new LinkedList<String>();
//        stringList.add(s);
        stringList.add(s1);
        stringList.add(s2);
        stringList.add(s3);
        stringList.add(s4);
        stringList.add(s5);
        stringList.add(s6);
//
        for(String fileAdress : stringList){
            readFileController.readLocalFile(fileAdress);
        }
    }
    @Test
    public void testStartWatchOrderPayLogTask(){
        readFileController.startWatchOrderPayLogTask();
    }


}
