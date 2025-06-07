package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ecs20140526.Client;
import com.aliyun.ecs20140526.models.DescribeInstancesRequest;
import com.aliyun.ecs20140526.models.DescribeInstancesResponse;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class AliyunGetInstance {
    public static JSONObject getInstance(String regionID,String zoneID,String instanceType) {
        String requestType = "实例列表";
        try {
            BasicInfo.sendDebug("Aliyun查询："+requestType);
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            request = request.setRegionId(regionID);
            request = request.setZoneId(zoneID);
            request = request.setInstanceType(instanceType);
            request = request.setMaxResults(10);
            Client client = AliyunSDK.client;
            DescribeInstancesResponse response = client.describeInstances(request);
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。状态码："+response.statusCode);
            JSONObject result = new JSONObject(response.getBody().toMap());
            BasicInfo.sendDebug(result.toJSONString());
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。结果：成功！");
            if (!result.getString("NextToken").equals("")) {
                JSONObject nextPage = getInstanceNext(result.getString("NextToken"),regionID,zoneID,instanceType);
                if (nextPage != null) {
                    JSONArray array = result.getJSONObject("Instances").getJSONArray("Instance");
                    array.addAll(nextPage.getJSONObject("Instances").getJSONArray("Instance"));
                    JSONObject temp = new JSONObject();
                    temp.put("Instance",array);
                    result.replace("Instances",temp);
                }
            }
            return result;
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。结果：失败！");
            return null;
        }
    }

    public static JSONObject getInstanceNext(String token,String regionID,String zoneID,String instanceType) {
        String requestType = "实例列表下一页";
        try {
            BasicInfo.sendDebug("Aliyun查询："+requestType);
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            request = request.setRegionId(regionID);
            request = request.setZoneId(zoneID);
            request = request.setInstanceType(instanceType);
            request = request.setNextToken(token);
            request = request.setMaxResults(10);
            Client client = AliyunSDK.client;
            DescribeInstancesResponse response = client.describeInstances(request);
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。状态码："+response.statusCode);
            JSONObject result = new JSONObject(response.getBody().toMap());
            BasicInfo.sendDebug(result.toJSONString());
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。结果：成功！");
            if (!result.getString("NextToken").equals("")) {
                JSONObject nextPage = getInstanceNext(result.getString("NextToken"),regionID,zoneID,instanceType);
                if (nextPage != null) {
                    JSONArray array = result.getJSONObject("Instances").getJSONArray("Instance");
                    array.addAll(nextPage.getJSONObject("Instances").getJSONArray("Instance"));
                    JSONObject temp = new JSONObject();
                    temp.put("Instance",array);
                    result.replace("Instances",temp);
                }
            }
            return result;
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.sendDebug("Aliyun查询："+requestType+"。结果：失败！");
            return null;
        }
    }
}
