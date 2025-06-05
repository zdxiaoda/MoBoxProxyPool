package org.moboxlab.MoBoxProxyPool.MySQL;

import org.moboxlab.MoBoxProxyPool.BasicInfo;
import org.mossmc.mosscg.MossLib.Mysql.MysqlManager;
import org.mossmc.mosscg.MossLib.Object.ObjectMysqlInfo;

public class MySQLMain {
    public static ObjectMysqlInfo mysqlInfo;
    public static int mysqlCheckCoolDown = 60;
    public static int mysqlRefreshCoolDown = 300;
    public static void initMySQL() {
        mysqlInfo = new ObjectMysqlInfo();
        mysqlInfo.address = BasicInfo.config.getString("MySQLAddress");
        mysqlInfo.port = BasicInfo.config.getString("MySQLPort");
        mysqlInfo.database = BasicInfo.config.getString("MySQLDatabase");
        mysqlInfo.username = BasicInfo.config.getString("MySQLUser");
        mysqlInfo.password = BasicInfo.config.getString("MySQLPassword");
        mysqlInfo.poolSize = 3;
        updatePool();
        BasicInfo.logger.sendInfo("初始化MySQL完成");
    }

    public static long lastCheck = 0;
    public static long lastRefresh = 0;

    public static synchronized void checkMysql() {
        if ((System.currentTimeMillis()-lastCheck) < mysqlCheckCoolDown*1000L) return;
        try {
            boolean isClosed = BasicInfo.mysql.getConnection().isClosed();
            boolean isValid = BasicInfo.mysql.getConnection().isValid(1);
            if (isClosed || !isValid) {
                updatePool();
            }
            lastCheck = System.currentTimeMillis();
            if ((System.currentTimeMillis()-lastRefresh) < mysqlRefreshCoolDown*1000L) return;
            updatePool();
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
        }
    }

    public static void updatePool() {
        if (BasicInfo.mysql!= null) {
            MysqlManager mysqlManager = BasicInfo.mysql;
            Thread thread = new Thread(() -> timerClose(mysqlManager));
            thread.start();
        }
        lastRefresh = System.currentTimeMillis();
        BasicInfo.mysql = new MysqlManager();
        BasicInfo.mysql.initMysql(mysqlInfo,BasicInfo.logger,false);
        BasicInfo.sendDebug("已刷新MySQL链接！");
    }

    public static void timerClose(MysqlManager mysql) {
        try {
            Thread.sleep(10000L);
            mysql.close();
            BasicInfo.sendDebug("已关闭失效MySQL链接！");
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
        }
    }
}
