package org.moboxlab.MoBoxProxyPool.Cache;

import org.moboxlab.MoBoxProxyPool.BasicInfo;
import org.moboxlab.MoBoxProxyPool.Object.ObjectECS;

import java.util.HashMap;
import java.util.Map;

public class CacheECS {
    public static int targetAmount = 0;
    public static void updateAmount(int newAmount) {
        targetAmount = newAmount;
        BasicInfo.logger.sendInfo("目标IP数更新！当前目标IP数："+targetAmount);
    }
    public static Map<String,ObjectECS> ecsMap = new HashMap<>();

    public static void addECS(String ID) {
        ObjectECS ecs = new ObjectECS();
        ecs.instanceID = ID;
        ecs.createTime = System.currentTimeMillis();
        ecsMap.put(ID,ecs);
        BasicInfo.logger.sendInfo("已成功缓存ECS："+ID);
    }

    public static void removeECS(String ID) {
        ecsMap.remove(ID);
        BasicInfo.logger.sendInfo("已成功移除ECS："+ID);
    }
}
