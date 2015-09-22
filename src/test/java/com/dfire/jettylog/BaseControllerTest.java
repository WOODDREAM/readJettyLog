package com.dfire.jettylog;

import com.dfire.jettylog.controller.ReadFileController;
import com.dfire.jettylog.file.ReadFile;
import com.dfire.jettylog.task.QuartzTask;
import com.dfire.jettylog.task.ScheduleTask;
import com.dfire.jettylog.utils.HttpUtil;
import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

/**
 * User:huangtao
 * Date:2015-09-15
 * description：
 */
@ContextConfiguration(locations = {"classpath*:applicationContext.xml"})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BaseControllerTest {
    protected MockMvc mockMvc;
    @Autowired
    ReadFileController readFileController;
    @Autowired
    ScheduleTask scheduleTask;
    @Autowired
    HttpUtil httpUtil;
    @Autowired
    QuartzTask quartzTask;
    @Autowired
    ReadFile readFile;

}
