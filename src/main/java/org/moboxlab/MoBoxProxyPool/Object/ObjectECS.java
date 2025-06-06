package org.moboxlab.MoBoxProxyPool.Object;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

import java.text.SimpleDateFormat;
import java.util.*;

public class ObjectECS {
    public String instanceID;
    public String IP;

    public String username = BasicInfo.config.getString("ECSUser");
    public String password = BasicInfo.config.getString("ECSAuth");

    public String proxyPort = "22339";
    public String proxyAccount = BasicInfo.config.getString("proxyAccount");
    public String proxyPassword = BasicInfo.config.getString("proxyPassword");

    public long createTime;
    public boolean available = false;

    public BasicInfo.ECSStatus status = BasicInfo.ECSStatus.CREATED;

    public Map<String,Long> sessionMap = new HashMap<>();

    public Session getSSHSession() {
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(username,IP,22);
            session.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sshConfig);
            session.setTimeout(8000);
            session.connect();
            return session;
        } catch (Exception e) {
            //BasicInfo.logger.sendException(e);
            return null;
        }
    }

    public boolean timeCheck() {
        try {
            long nowTime = System.currentTimeMillis();
            long hasLive = nowTime-createTime;
            long maxLive = BasicInfo.config.getInteger("liveTime")*1000L;
            if (hasLive > maxLive) {
                BasicInfo.logger.sendWarn("实例"+instanceID+"存活时间已超时，即将释放！");
                status = BasicInfo.ECSStatus.CYCLING;
                available = false;
                return true;
            }
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
        }
        return false;
    }

    public void setCreateTime(String create) {
        try {
            create = create.replace("T"," ").replace("Z"," ");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            createTime = format.parse(create).getTime()+8*3600*1000L;
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
        }
    }

    public long getRemainTime() {
        long nowTime = System.currentTimeMillis();
        long hasLive = nowTime-createTime;
        long maxLive = BasicInfo.config.getInteger("liveTime")*1000L;
        return maxLive-hasLive;
    }

    public void checkSessionMap() {
        Set<String> keySet = new HashSet<>(sessionMap.keySet());
        keySet.forEach(key -> {
            long maxTime = sessionMap.get(key)+BasicInfo.config.getInteger("sessionTime")*1000L;
            if (maxTime < System.currentTimeMillis()) sessionMap.remove(key);
        });
    }
}
