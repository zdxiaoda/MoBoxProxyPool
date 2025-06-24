package org.moboxlab.MoBoxProxyPool.Tick;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.moboxlab.MoBoxProxyPool.BasicInfo;
import org.moboxlab.MoBoxProxyPool.Cache.CacheECS;
import org.moboxlab.MoBoxProxyPool.Cache.CacheLoad;
import org.moboxlab.MoBoxProxyPool.Request.*;
import org.moboxlab.MoBoxProxyPool.SSH.SSHMain;

import java.util.HashSet;
import java.util.Set;

public class TickMain {
    public static long mspt = 0;
    public static Thread tickThread;

    public static void runTick() {
        CacheLoad.loadCacheECS();
        tickThread = new Thread(TickMain::ticker);
        tickThread.start();

    }

    @SuppressWarnings({ "BusyWait", "InfiniteLoopStatement" })
    public static void ticker() {
        try {
            BasicInfo.logger.sendInfo("TickLoop线程已启动！");
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            BasicInfo.logger.sendException(e);
        }
        while (true) {
            long start = System.currentTimeMillis();
            try {
                BasicInfo.sendDebug("正在执行Tick......");
                // 遍历ECS缓存，检查ECS状态
                Set<String> keys = new HashSet<>(CacheECS.ecsMap.keySet());
                keys.forEach(instanceID -> {
                    JSONObject info = TencentGetInstanceInfo.getInstanceInfo(instanceID);
                    if (info == null) {
                        // 空数据则标记ECS已删除
                        CacheECS.ecsMap.remove(instanceID);
                        BasicInfo.logger.sendInfo("查询实例" + instanceID + "时返回空数据，实例已删除！");
                    } else {
                        // 有数据则ECS存活
                        String publicIp = info.getString("PublicIpAddress");
                        CacheECS.ecsMap.get(instanceID).setCreateTime(info.getString("CreatedTime"));
                        if (publicIp != null && !publicIp.isEmpty()) {
                            CacheECS.ecsMap.get(instanceID).IP = publicIp;
                        }
                        // 根据状态，进行对应操作
                        switch (CacheECS.ecsMap.get(instanceID).status) {
                            case CREATED:
                                // 已创建状态，部署脚本
                                SSHMain.asyncInit(CacheECS.ecsMap.get(instanceID));
                                break;
                            case INSTALLING:
                                // 部署中状态，不再重新部署
                                break;
                            case RUNNING:
                                // 运行中状态
                                boolean timeCheck = CacheECS.ecsMap.get(instanceID).timeCheck();
                                if (timeCheck && ((CacheECS.ecsMap.size() - 1) < CacheECS.targetAmount))
                                    createNewECS();
                                CacheECS.ecsMap.get(instanceID).checkSessionMap();
                                break;
                            case CYCLING:
                                // 即将回收状态
                                releaseECS(instanceID);
                                break;
                            default:
                                break;
                        }
                    }
                });

                // 处理实例数量调整
                int remain = CacheECS.targetAmount - CacheECS.ecsMap.size();

                if (CacheECS.targetAmount == 0 && CacheECS.ecsMap.size() > 0) {
                    // 当目标数量为0时，主动销毁所有现有实例
                    BasicInfo.logger.sendInfo("目标IP数量为0，正在销毁所有实例......");
                    BasicInfo.logger.sendInfo("待销毁实例数量：" + CacheECS.ecsMap.size());

                    Set<String> instanceIds = new HashSet<>(CacheECS.ecsMap.keySet());
                    instanceIds.forEach(instanceID -> {
                        BasicInfo.logger.sendInfo("标记实例进入回收状态：" + instanceID);
                        CacheECS.ecsMap.get(instanceID).status = BasicInfo.ECSStatus.CYCLING;
                        CacheECS.ecsMap.get(instanceID).available = false;
                    });
                } else if (remain > 0) {
                    // ECS不足时进行补足
                    BasicInfo.logger.sendInfo("检测到IP数量不足，正在自动新建ECS......");
                    BasicInfo.logger.sendInfo("缺少数量：" + remain);
                    for (int i = 0; i < remain; i++) {
                        asyncCreate();
                        Thread.sleep(800L);
                    }
                } else if (remain < 0) {
                    // ECS过多时进行回收（但不是目标为0的情况）
                    BasicInfo.logger.sendInfo("检测到IP数量过多，正在回收多余ECS......");
                    BasicInfo.logger.sendInfo("多余数量：" + (-remain));

                    Set<String> instanceIds = new HashSet<>(CacheECS.ecsMap.keySet());
                    int recycleCount = 0;
                    for (String instanceID : instanceIds) {
                        if (recycleCount >= (-remain))
                            break;
                        if (CacheECS.ecsMap.get(instanceID).status == BasicInfo.ECSStatus.RUNNING) {
                            BasicInfo.logger.sendInfo("标记多余实例进入回收状态：" + instanceID);
                            CacheECS.ecsMap.get(instanceID).status = BasicInfo.ECSStatus.CYCLING;
                            CacheECS.ecsMap.get(instanceID).available = false;
                            recycleCount++;
                        }
                    }
                }
            } catch (Exception e) {
                BasicInfo.logger.sendException(e);
                BasicInfo.logger.sendWarn("执行Tick时出现错误！");
            }
            long end = System.currentTimeMillis();
            mspt = end - start;
            try {
                Thread.sleep(20000L);
            } catch (Exception e) {
                BasicInfo.logger.sendWarn("执行Tick等待时出现错误！");
            }
        }
    }

    public static void asyncCreate() {
        Thread thread = new Thread(TickMain::createNewECS);
        thread.start();
    }

    public static void createNewECS() {
        try {
            String regionName = BasicInfo.config.getString("regionName");
            String zoneId = BasicInfo.config.getString("zoneId");
            String instanceType = BasicInfo.config.getString("instanceType");
            JSONObject create = TencentCreateInstance.createInstance(regionName, zoneId, instanceType);
            String instanceID = create.getString("InstanceId");
            BasicInfo.logger.sendInfo("实例创建成功！实例ID：" + instanceID);
            CacheECS.addECS(instanceID);
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("创建实例时出现错误！");
        }
    }

    public static void releaseECS(String instanceID) {
        BasicInfo.logger.sendInfo("正在释放实例！实例ID：" + instanceID);
        JSONObject delete = TencentDeleteInstance.deleteInstance(instanceID);
        CacheECS.removeECS(instanceID);
        BasicInfo.logger.sendInfo("实例释放成功！实例ID：" + instanceID);
    }
}
