package com.dfire.jettylog.dao.impl;

import com.dfire.jettylog.bean.DailyMonitor;
import com.dfire.jettylog.bean.Monitor;
import com.dfire.jettylog.bean.MonitorDetails;
import com.dfire.jettylog.bean.StopWatchMDetail;
import com.dfire.jettylog.dao.IMonitorDao;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.TableName;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User:huangtao
 * Date:2015-09-14
 * description：
 */
@Repository(value = "monitorService")
public class MonitorDaoImpl implements IMonitorDao {

    @Autowired
    private Dao nutzDao;


    @Override
    public void createWatchOrderPayTable(final String monitorName, final String date) throws Exception {

        try {
            nutzDao.create(DailyMonitor.class, false);
            Trans.exec(new Atom() {
                public void run() {
                    //如果dailyMonitor表中存在此日期的日志，先删除此日期的所有信息，再创建表
                    DailyMonitor dailyMonitor = nutzDao.fetch(DailyMonitor.class, Cnd.where("monitor_name", "LIKE", monitorName).and("run_date", "LIKE", date).asc("id"));
                    if (dailyMonitor != null) {
                        List<Monitor> monitorList = nutzDao.query(Monitor.class, Cnd.where("monitor_name", "LIKE", monitorName).and("run_date", "LIKE", date).asc("id"));
                        if (0 != monitorList.size()) {
                            for (Monitor monitor : monitorList) {
                                monitor.setMonitorDetailsList(nutzDao.query(MonitorDetails.class, Cnd.where("monitor_id", "=", monitor.getId()).asc("id")));
                                nutzDao.deleteWith(monitor, "monitorDetailsList");
                            }
                            nutzDao.clear(DailyMonitor.class, Cnd.where("monitor_name", "LIKE", monitorName).and("run_date", "LIKE", date).asc("id"));
                        }

                    } else {
                        nutzDao.create(MonitorDetails.class, false);
                        nutzDao.create(Monitor.class, false);
                    }


                    //动态建表
//                    TableName.run(monitorName, new Runnable() {
//                        public void run() {
//                            nutzDao.create(MonitorDetails.class, false);
//                        }
//                    });
//                    TableName.run(monitorName, new Runnable() {
//                        public void run() {
//                            nutzDao.create(Monitor.class, false);
//                        }
//                    });
                }
            });
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void createStopWatchTable(final String monitorName, final String date) throws Exception {

        try {
            nutzDao.create(DailyMonitor.class, false);
            Trans.exec(new Atom() {
                public void run() {
                    //如果dailyMonitor表中存在此日期的日志，先删除此日期的所有信息，再创建表
                    DailyMonitor dailyMonitor = nutzDao.fetch(DailyMonitor.class, Cnd.where("monitor_name", "LIKE", monitorName).and("run_date", "LIKE", date).asc("id"));
                    if (dailyMonitor != null) {
                        List<StopWatchMDetail> monitorList = nutzDao.query(StopWatchMDetail.class, Cnd.where("monitor_name", "LIKE", monitorName).and("run_date", "LIKE", date).asc("id"));
                        if (0 != monitorList.size()) {
                            for (StopWatchMDetail stopWatchMDetail : monitorList) {
                                nutzDao.delete(stopWatchMDetail);
                            }
                            nutzDao.clear(DailyMonitor.class, Cnd.where("monitor_name", "LIKE", monitorName).and("run_date", "LIKE", date).asc("id"));
                        }
                    } else {
                        nutzDao.create(StopWatchMDetail.class, false);
                    }
                }
            });
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void insertDailyMonitorByCSS(final DailyMonitor dailyMonitor) throws Exception {
        try {
            if (null != dailyMonitor.getMonitorName()) {
                TableName.run(dailyMonitor.getMonitorName(), new Runnable() {
                    public void run() {
                        nutzDao.insertWith(dailyMonitor, "monitorList");
                    }
                });
            }

        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public void insertMonitor(final Monitor monitor) throws Exception {
        try {
            if (monitor.getMonitorName() != null) {
                TableName.run(monitor.getMonitorName(), new Runnable() {
                    public void run() {
                        nutzDao.insertWith(monitor, "monitorDetailsList");
                    }
                });
            }
        } catch (Exception e) {
            throw e;
        }
    }


    @Override
    public void insertDailyMonitor(DailyMonitor dailyMonitor) throws Exception {
        try {
            if (dailyMonitor.getMonitorName() != null) {
                nutzDao.insert(dailyMonitor);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void insertStopWatchMonitor(StopWatchMDetail stopWatchMonitor) throws Exception {
        try{
            if(null != stopWatchMonitor){
                nutzDao.insert(stopWatchMonitor);
            }
        }catch (Exception e){
            throw e;
        }
    }
}
