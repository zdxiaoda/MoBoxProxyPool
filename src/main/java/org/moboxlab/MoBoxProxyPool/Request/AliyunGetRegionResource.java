package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.DescribeAvailableResourceRequest;
import com.aliyun.ecs20140526.models.DescribeAvailableResourceResponse;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class AliyunGetRegionResource {
    public static JSONObject getRegionResource(String regionID,String zoneID,String instanceType) {
        String requestType = "区域实例余量";
        try {
            BasicInfo.sendDebug("Aliyun查询："+requestType);
            DescribeAvailableResourceRequest request = new DescribeAvailableResourceRequest();
            request = request.setRegionId(regionID);
            request = request.setZoneId(zoneID);
            request = request.setDestinationResource("InstanceType");
            request = request.setInstanceType(instanceType);
            Client client = AliyunSDK.client;
            DescribeAvailableResourceResponse response = client.describeAvailableResource(request);
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
