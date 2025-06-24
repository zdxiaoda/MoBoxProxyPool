package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.tencentcloudapi.cvm.v20170312.models.DescribeInstancesRequest;
import com.tencentcloudapi.cvm.v20170312.models.DescribeInstancesResponse;
import com.tencentcloudapi.cvm.v20170312.models.Filter;
import com.tencentcloudapi.cvm.v20170312.models.Instance;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class TencentGetInstance {
    public static JSONObject getInstance(String regionName, String zoneId, String instanceType) {
        String requestType = "实例列表";
        try {
            BasicInfo.sendDebug("Tencent查询：" + requestType);
            DescribeInstancesRequest request = new DescribeInstancesRequest();

            // 设置过滤条件
            Filter[] filters = new Filter[2];

            // 按zone过滤
            filters[0] = new Filter();
            filters[0].setName("zone");
            filters[0].setValues(new String[] { zoneId });

            // 按实例类型过滤
            filters[1] = new Filter();
            filters[1].setName("instance-type");
            filters[1].setValues(new String[] { instanceType });

            request.setFilters(filters);
            request.setLimit(10L);

            DescribeInstancesResponse response = TencentSDK.cvmClient.DescribeInstances(request);
            BasicInfo.sendDebug("Tencent查询：" + requestType + "。RequestId：" + response.getRequestId());

            JSONObject result = new JSONObject();
            result.put("RequestId", response.getRequestId());
            result.put("TotalCount", response.getTotalCount());

            JSONArray instanceArray = new JSONArray();
            for (Instance instance : response.getInstanceSet()) {
                JSONObject instanceJson = new JSONObject();
                instanceJson.put("InstanceId", instance.getInstanceId());
                instanceJson.put("InstanceName", instance.getInstanceName());
                instanceJson.put("InstanceState", instance.getInstanceState());
                instanceJson.put("CreatedTime", instance.getCreatedTime());

                // 获取公网IP
                if (instance.getPublicIpAddresses() != null && instance.getPublicIpAddresses().length > 0) {
                    instanceJson.put("PublicIpAddress", instance.getPublicIpAddresses()[0]);
                }

                instanceArray.add(instanceJson);
            }
            result.put("Instances", instanceArray);

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