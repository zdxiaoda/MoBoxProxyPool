package org.moboxlab.MoBoxProxyPool.Object;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.TimeZone;

public class ObjectECS {
    public String instanceID;
    public String IP;

    public String username = BasicInfo.config.getString("CVMUser");
    public String password = BasicInfo.config.getString("CVMAuth");

    public String proxyPort = "22339";
    public String proxyAccount = BasicInfo.config.getString("proxyAccount");
    public String proxyPassword = BasicInfo.config.getString("proxyPassword");

    public long createTime;
    public boolean available = false;

    public BasicInfo.ECSStatus status = BasicInfo.ECSStatus.CREATED;

    public Map<String, Long> sessionMap = new HashMap<>();

    public Session getSSHSession() {
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(username, IP, 22);
            session.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            session.setConfig(sshConfig);
            session.setTimeout(8000);
            session.connect();
            return session;
        } catch (Exception e) {
            // BasicInfo.logger.sendException(e);
            return null;
        }
    }

    public boolean timeCheck() {
        try {
            long nowTime = System.currentTimeMillis();
            long hasLive = nowTime - createTime;
            long maxLive = BasicInfo.config.getInteger("liveTime") * 1000L;
            if (hasLive > maxLive) {
                BasicInfo.logger.sendWarn("实例" + instanceID + "存活时间已超时，即将释放！");
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
            // 腾讯云时间格式处理 (ISO8601格式: 2023-01-01T12:00:00+08:00)
            // 正确处理时区信息，避免时间偏差

            // 替换T为空格
            create = create.replace("T", " ");

            // 处理时区信息
            SimpleDateFormat format;
            if (create.contains("+")) {
                // 包含时区偏移的格式，如: 2023-01-01 12:00:00+08:00
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssXXX");
                // 恢复冒号，确保格式正确
                if (!create.substring(create.length() - 3, create.length() - 2).equals(":")) {
                    create = create.substring(0, create.length() - 2) + ":" + create.substring(create.length() - 2);
                }
            } else if (create.contains("Z")) {
                // UTC时间格式
                create = create.replace("Z", "");
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
            } else {
                // 默认格式，按UTC处理
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
            }

            createTime = format.parse(create).getTime();

            // 添加调试信息
            BasicInfo.sendDebug("实例创建时间解析: 原始=" + create + ", 解析后=" + new Date(createTime));

        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            // 如果解析失败，使用当前时间作为兜底
            createTime = System.currentTimeMillis();
            BasicInfo.sendDebug("时间解析失败，使用当前时间: " + new Date(createTime));
        }
    }

    public long getRemainTime() {
        long nowTime = System.currentTimeMillis();
        long hasLive = nowTime - createTime;
        long maxLive = BasicInfo.config.getInteger("liveTime") * 1000L;
        return maxLive - hasLive;
    }

    public void checkSessionMap() {
        Set<String> keySet = new HashSet<>(sessionMap.keySet());
        keySet.forEach(key -> {
            long maxTime = sessionMap.get(key) + BasicInfo.config.getInteger("sessionTime") * 1000L;
            if (maxTime < System.currentTimeMillis())
                sessionMap.remove(key);
        });
    }
}
