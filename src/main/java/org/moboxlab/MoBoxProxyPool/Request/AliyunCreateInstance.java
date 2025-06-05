package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.CreateInstanceRequest;
import com.aliyun.ecs20140526.models.CreateInstanceResponse;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class AliyunCreateInstance {
    public static JSONObject createInstance(String regionID,String zoneID,String instanceType) {
        String requestType = "创建实例";
        try {
            BasicInfo.sendDebug("Aliyun查询："+requestType);
            CreateInstanceRequest request = new CreateInstanceRequest();
            //实例规格部分
            request = request.setRegionId(regionID);
            request = request.setZoneId(zoneID);
            request = request.setInstanceType(instanceType);
            //实例信息部分
            request = request.setInstanceName("MBPP");
            //磁盘部分
            CreateInstanceRequest.CreateInstanceRequestSystemDisk disk = new CreateInstanceRequest.CreateInstanceRequestSystemDisk();
            disk.setSize(20);
            request = request.setSystemDisk(disk);
            //网络部分
            request = request.setInternetChargeType("PayByTraffic");
            request = request.setInternetMaxBandwidthIn(100);
            request = request.setInternetMaxBandwidthOut(100);
            request = request.setSecurityGroupId(BasicInfo.config.getString("securityGroupID"));
            //系统部分
            request = request.setImageId("centos_7_9_x64_20G_alibase_20240628.vhd");
            request = request.setPassword(BasicInfo.config.getString("ECSAuth"));
            request = request.setSecurityEnhancementStrategy("Deactive");
            //计费部分
            request = request.setInstanceChargeType("PostPaid");
            request = request.setSpotStrategy("SpotAsPriceGo");
            request = request.setSpotDuration(1);
            request = request.setSpotInterruptionBehavior("Terminate");
            Client client = AliyunSDK.client;
            CreateInstanceResponse response = client.createInstance(request);
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。状态码："+response.statusCode);
            JSONObject result = new JSONObject(response.getBody().toMap());
            BasicInfo.sendDebug(result.toJSONString());
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。结果：成功！");
            return result;
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。结果：失败！");
            return null;
        }
    }
}
