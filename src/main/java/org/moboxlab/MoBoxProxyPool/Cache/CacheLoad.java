package org.moboxlab.MoBoxProxyPool.Cache;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.moboxlab.MoBoxProxyPool.BasicInfo;
import org.moboxlab.MoBoxProxyPool.Request.TencentGetInstance;

import java.util.HashMap;

public class CacheLoad {
    @SuppressWarnings("unchecked")
    public static void loadCacheECS() {
        BasicInfo.logger.sendInfo("正在加载已存在的CVM实例......");
        String regionName = BasicInfo.config.getString("regionName");
        String zoneId = BasicInfo.config.getString("zoneId");
        String instanceType = BasicInfo.config.getString("instanceType");
        JSONObject result = TencentGetInstance.getInstance(regionName, zoneId, instanceType);
        if (result != null) {
            BasicInfo.logger.sendInfo("查询到" + result.getInteger("TotalCount") + "个实例！");
            JSONArray array = result.getJSONArray("Instances");
            if (array != null) {
                array.forEach(o -> {
                    JSONObject instance = new JSONObject((HashMap) o);
                    String instanceID = instance.getString("InstanceId");
                    String status = instance.getString("InstanceState");
                    BasicInfo.logger.sendInfo("实例ID：" + instanceID + "，状态：" + status);
                    CacheECS.addECS(instanceID);
                });
            }
        } else {
            BasicInfo.logger.sendInfo("查询实例列表失败，将从空缓存开始！");
        }
    }
}
