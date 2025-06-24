package org.moboxlab.MoBoxProxyPool.Request;

import com.alibaba.fastjson.JSONObject;
import com.tencentcloudapi.cvm.v20170312.models.RunInstancesRequest;
import com.tencentcloudapi.cvm.v20170312.models.RunInstancesResponse;
import com.tencentcloudapi.cvm.v20170312.models.InstanceChargePrepaid;
import com.tencentcloudapi.cvm.v20170312.models.VirtualPrivateCloud;
import com.tencentcloudapi.cvm.v20170312.models.InternetAccessible;

import com.tencentcloudapi.cvm.v20170312.models.LoginSettings;
import com.tencentcloudapi.cvm.v20170312.models.InstanceMarketOptionsRequest;
import com.tencentcloudapi.cvm.v20170312.models.SpotMarketOptions;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class TencentCreateInstance {
    public static JSONObject createInstance(String regionName, String zoneId, String instanceType) {
        String requestType = "创建实例";
        try {
            BasicInfo.sendDebug("Tencent查询：" + requestType);
            RunInstancesRequest request = new RunInstancesRequest();

            // 基础配置
            request.setImageId("img-6n21msk1"); // CentOS 7.6 64位
            request.setInstanceType(instanceType);
            request.setPlacement(new com.tencentcloudapi.cvm.v20170312.models.Placement());
            request.getPlacement().setZone(zoneId);

            // VPC配置
            VirtualPrivateCloud vpc = new VirtualPrivateCloud();
            vpc.setVpcId(BasicInfo.config.getString("vpcId"));
            vpc.setSubnetId(BasicInfo.config.getString("subnetId"));
            request.setVirtualPrivateCloud(vpc);

            // 安全组配置
            request.setSecurityGroupIds(BasicInfo.config.getStringList("securityGroupIds").toArray(new String[0]));

            // 不设置系统盘配置，使用腾讯云默认值
            BasicInfo.sendDebug("使用腾讯云默认系统盘配置");

            // 网络计费类型
            InternetAccessible internetAccessible = new InternetAccessible();
            internetAccessible.setInternetChargeType("TRAFFIC_POSTPAID_BY_HOUR");
            internetAccessible.setInternetMaxBandwidthOut(5L);
            internetAccessible.setPublicIpAssigned(true);
            request.setInternetAccessible(internetAccessible);

            // 实例计费类型
            if (BasicInfo.config.getBoolean("enableSpotInstance")) {
                request.setInstanceChargeType("SPOTPAID");
            } else {
                request.setInstanceChargeType("POSTPAID_BY_HOUR");
            }

            // 竞价实例配置
            if (BasicInfo.config.getBoolean("enableSpotInstance")) {
                BasicInfo.sendDebug("启用竞价实例模式");
                InstanceMarketOptionsRequest marketOptions = new InstanceMarketOptionsRequest();

                // 设置竞价实例参数
                SpotMarketOptions spotOptions = new SpotMarketOptions();

                // 获取最高出价配置
                Double spotMaxPrice = BasicInfo.config.getDouble("spotMaxPrice");
                if (spotMaxPrice != null && spotMaxPrice > 0) {
                    spotOptions.setMaxPrice(String.valueOf(spotMaxPrice));
                    BasicInfo.sendDebug("竞价实例最高出价: " + spotMaxPrice + " 元/小时");
                } else {
                    // 使用配置的默认最高限制价格，如果配置不存在则使用兜底值
                    Double defaultMaxPrice = null;
                    try {
                        defaultMaxPrice = BasicInfo.config.getDouble("spotDefaultMaxPrice");
                    } catch (Exception e) {
                        BasicInfo.sendDebug("读取 spotDefaultMaxPrice 配置失败，使用默认值");
                    }

                    if (defaultMaxPrice == null || defaultMaxPrice <= 0) {
                        defaultMaxPrice = 1.0; // 硬编码兜底值
                    }
                    spotOptions.setMaxPrice(String.valueOf(defaultMaxPrice));
                    BasicInfo.sendDebug("竞价实例使用市场价，最高限制: " + defaultMaxPrice + " 元/小时");
                }

                // 设置竞价请求类型：one-time（一次性）或 persistent（持续性）
                spotOptions.setSpotInstanceType("one-time");

                marketOptions.setSpotOptions(spotOptions);
                marketOptions.setMarketType("spot");
                request.setInstanceMarketOptions(marketOptions);
            }

            // 登录设置
            LoginSettings loginSettings = new LoginSettings();
            loginSettings.setPassword(BasicInfo.config.getString("CVMAuth"));
            request.setLoginSettings(loginSettings);

            // 实例名称
            if (BasicInfo.config.getBoolean("enableSpotInstance")) {
                request.setInstanceName("MBPP-SPOT");
            } else {
                request.setInstanceName("MBPP");
            }

            // 实例数量
            request.setInstanceCount(1L);

            RunInstancesResponse response = TencentSDK.cvmClient.RunInstances(request);
            BasicInfo.sendDebug("Tencent查询：" + requestType + "。RequestId：" + response.getRequestId());

            JSONObject result = new JSONObject();
            result.put("RequestId", response.getRequestId());
            if (response.getInstanceIdSet().length > 0) {
                result.put("InstanceId", response.getInstanceIdSet()[0]);
            }

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