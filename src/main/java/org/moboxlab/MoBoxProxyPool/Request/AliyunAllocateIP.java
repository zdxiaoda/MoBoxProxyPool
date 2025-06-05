package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.AllocatePublicIpAddressRequest;
import com.aliyun.ecs20140526.models.AllocatePublicIpAddressResponse;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class AliyunAllocateIP {
    public static JSONObject allocateIP(String instanceID) {
        String requestType = "分配公网IP";
        try {
            BasicInfo.sendDebug("Aliyun查询："+requestType);
            AllocatePublicIpAddressRequest request = new AllocatePublicIpAddressRequest();
            request = request.setInstanceId(instanceID);
            Client client = AliyunSDK.client;
            AllocatePublicIpAddressResponse response = client.allocatePublicIpAddress(request);
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
