package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONObject;
import com.tencentcloudapi.cvm.v20170312.models.DescribeInstancesRequest;
import com.tencentcloudapi.cvm.v20170312.models.DescribeInstancesResponse;
import com.tencentcloudapi.cvm.v20170312.models.Instance;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class TencentGetInstanceInfo {
    public static JSONObject getInstanceInfo(String instanceId) {
        String requestType = "查询实例信息";
        try {
            BasicInfo.sendDebug("Tencent查询：" + requestType);
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            request.setInstanceIds(new String[] { instanceId });

            DescribeInstancesResponse response = TencentSDK.cvmClient.DescribeInstances(request);
            BasicInfo.sendDebug("Tencent查询：" + requestType + "。RequestId：" + response.getRequestId());

            if (response.getInstanceSet() == null || response.getInstanceSet().length == 0) {
                BasicInfo.sendDebug("Tencent查询：" + requestType + "。结果：实例不存在！");
                return null;
            }

            Instance instance = response.getInstanceSet()[0];
            JSONObject result = new JSONObject();
            result.put("RequestId", response.getRequestId());
            result.put("InstanceId", instance.getInstanceId());
            result.put("InstanceName", instance.getInstanceName());
            result.put("InstanceState", instance.getInstanceState());
            result.put("CreatedTime", instance.getCreatedTime());

            // 获取公网IP
            if (instance.getPublicIpAddresses() != null && instance.getPublicIpAddresses().length > 0) {
                result.put("PublicIpAddress", instance.getPublicIpAddresses()[0]);
            }

            // 检查实例计费类型
            String instanceChargeType = instance.getInstanceChargeType();
            result.put("InstanceChargeType", instanceChargeType);

            // 注：腾讯云竞价实例仍然显示为 POSTPAID_BY_HOUR 计费类型
            // 需要通过其他方式判断是否为竞价实例，例如通过实例名称标记或其他自定义标签

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