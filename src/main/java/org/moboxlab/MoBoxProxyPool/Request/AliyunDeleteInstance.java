package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.DeleteInstanceRequest;
import com.aliyun.ecs20140526.models.DeleteInstanceResponse;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class AliyunDeleteInstance {
    public static JSONObject deleteInstance(String instanceID) {
        String requestType = "释放实例";
        try {
            BasicInfo.sendDebug("Aliyun查询："+requestType);
            DeleteInstanceRequest request = new DeleteInstanceRequest();
            request = request.setInstanceId(instanceID);
            request = request.setForce(true);
            Client client = AliyunSDK.client;
            DeleteInstanceResponse response = client.deleteInstance(request);
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
