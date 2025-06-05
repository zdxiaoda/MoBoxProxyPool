package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.DescribeInstanceAttributeRequest;
import com.aliyun.ecs20140526.models.DescribeInstanceAttributeResponse;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class AliyunGetInstanceInfo {
    public static JSONObject getInstanceInfo(String instanceID) {
        String requestType = "实例信息";
        try {
            BasicInfo.sendDebug("Aliyun查询："+requestType);
            DescribeInstanceAttributeRequest request = new DescribeInstanceAttributeRequest();
            request = request.setInstanceId(instanceID);
            Client client = AliyunSDK.client;
            DescribeInstanceAttributeResponse response = client.describeInstanceAttribute(request);
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。状态码："+response.statusCode);
            if (response.statusCode == 404) {
                BasicInfo.sendDebug("Aliyun查询："+requestType+"。结果：404！");
                return null;
            }
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
