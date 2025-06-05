package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.StartInstanceRequest;
import com.aliyun.ecs20140526.models.StartInstanceResponse;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class AliyunStartInstance {
    public static JSONObject startInstance(String instanceID) {
        String requestType = "启动实例";
        try {
            BasicInfo.sendDebug("Aliyun查询："+requestType);
            StartInstanceRequest request = new StartInstanceRequest();
            request = request.setInstanceId(instanceID);
            Client client = AliyunSDK.client;
            StartInstanceResponse response = client.startInstance(request);
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。状态码："+response.statusCode);
            if (response.statusCode == 403) {
                BasicInfo.sendDebug("Aliyun查询："+requestType+"。等待5s重试！");
                Thread.sleep(5000L);
                return startInstance(instanceID);
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
