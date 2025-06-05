package org.moboxlab.MoBoxProxyPool.Cache;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.moboxlab.MoBoxProxyPool.BasicInfo;
import org.moboxlab.MoBoxProxyPool.Request.AliyunGetInstance;

import java.util.HashMap;

public class CacheLoad {
    @SuppressWarnings("unchecked")
    public static void loadCacheECS() {
        BasicInfo.logger.sendInfo("正在加载已存在的ECS实例......");
        String rid = BasicInfo.config.getString("regionID");
        String zid = BasicInfo.config.getString("zoneID");
        String tid = BasicInfo.config.getString("instanceType");
        JSONObject result = AliyunGetInstance.getInstance(rid,zid,tid);
        BasicInfo.logger.sendInfo("查询到"+result.getInteger("TotalCount")+"个实例！");
        JSONArray array = result.getJSONObject("Instances").getJSONArray("Instance");
        array.forEach(o -> {
            JSONObject instance = new JSONObject((HashMap)o);
            String instanceID = instance.getString("InstanceId");
            String status = instance.getString("Status");
            BasicInfo.logger.sendInfo("实例ID："+instanceID+"，状态："+status);
            CacheECS.addECS(instanceID);
        });
    }
}
