package org.moboxlab.MoBoxProxyPool.Request;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.cvm.v20170312.CvmClient;
import com.tencentcloudapi.vpc.v20170312.VpcClient;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class TencentSDK {
    public static CvmClient cvmClient;
    public static VpcClient vpcClient;

    public static void loadTencentClient() {
        try {
            Credential cred = new Credential(
                    BasicInfo.config.getString("TencentSecretId"),
                    BasicInfo.config.getString("TencentSecretKey"));

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("cvm.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            cvmClient = new CvmClient(cred, BasicInfo.config.getString("regionName"), clientProfile);

            // VPC客户端
            HttpProfile vpcHttpProfile = new HttpProfile();
            vpcHttpProfile.setEndpoint("vpc.tencentcloudapi.com");

            ClientProfile vpcClientProfile = new ClientProfile();
            vpcClientProfile.setHttpProfile(vpcHttpProfile);

            vpcClient = new VpcClient(cred, BasicInfo.config.getString("regionName"), vpcClientProfile);

            BasicInfo.logger.sendInfo("腾讯云SDK初始化成功！");
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("腾讯云SDK初始化失败！");
        }
    }
}