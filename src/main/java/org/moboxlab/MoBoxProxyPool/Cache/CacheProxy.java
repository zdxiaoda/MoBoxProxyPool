package org.moboxlab.MoBoxProxyPool.Cache;

import org.moboxlab.MoBoxProxyPool.BasicInfo;
import org.moboxlab.MoBoxProxyPool.Object.ObjectECS;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CacheProxy {
    public static Random random = new Random();
    public static ObjectECS getProxy(String sessionID) {
        try {
            List<String> pool = getAvailablePool();
            pool = getSessionFreePool(pool,sessionID);
            BasicInfo.sendDebug(String.valueOf(pool.size()));
            if (pool.size() != 0) return getRandomECS(pool,sessionID);
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
        }
        return getRandomECS(getAvailablePool(),sessionID);
    }

    public static List<String> getAvailablePool() {
        List<String> resultPool = new ArrayList<>();
        List<String> cachePool = new ArrayList<>(CacheECS.ecsMap.keySet());
        cachePool.forEach(id -> {
            if (CacheECS.ecsMap.get(id).available) resultPool.add(id);
        });
        return resultPool;
    }

    public static List<String> getSessionFreePool(List<String> cachePool, String sessionID) {
        List<String> resultPool = new ArrayList<>();
        cachePool.forEach(id -> {
            if (!CacheECS.ecsMap.get(id).sessionSet.contains(sessionID)) resultPool.add(id);
        });
        return resultPool;
    }

    public static ObjectECS getRandomECS(List<String> pool,String sessionID) {
        int loc = random.nextInt(pool.size());
        ObjectECS ecs = CacheECS.ecsMap.get(pool.get(loc));
        ecs.sessionSet.add(sessionID);
        return ecs;
    }
}
