package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONObject;
import com.tencentcloudapi.cvm.v20170312.models.TerminateInstancesRequest;
import com.tencentcloudapi.cvm.v20170312.models.TerminateInstancesResponse;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class TencentDeleteInstance {
    public static JSONObject deleteInstance(String instanceId) {
        String requestType = "释放实例";
        try {
            BasicInfo.sendDebug("Tencent查询：" + requestType);
            TerminateInstancesRequest request = new TerminateInstancesRequest();
            request.setInstanceIds(new String[] { instanceId });

            TerminateInstancesResponse response = TencentSDK.cvmClient.TerminateInstances(request);
            BasicInfo.sendDebug("Tencent查询：" + requestType + "。RequestId：" + response.getRequestId());

            JSONObject result = new JSONObject();
            result.put("RequestId", response.getRequestId());

            BasicInfo.sendDebug(result.toJSONString());
            BasicInfo.sendDebug("Tencent查询：" + requestType + "。结果：成功！");
            return result;
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.sendDebug("Tencent查询：" + requestType + "。结果：失败！");
            return null;
        }
    }
}