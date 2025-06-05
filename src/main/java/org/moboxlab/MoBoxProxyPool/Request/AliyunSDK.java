package org.moboxlab.MoBoxProxyPool.Request;

import com.aliyun.ecs20140526.Client;
import com.aliyun.teaopenapi.models.Config;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

public class AliyunSDK {
    public static Client client;

    public static void loadAliyunClient() {
        try {
            Config config = new Config();
            config = config.setAccessKeyId(BasicInfo.config.getString("AliyunAccessKeyID"));
            config = config.setAccessKeySecret(BasicInfo.config.getString("AliyunAccessKeySecret"));
            config.endpoint = "ecs.cn-hangzhou.aliyuncs.com";
            client = new Client(config);
            BasicInfo.logger.sendInfo("阿里云SDK初始化成功！");
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
            BasicInfo.logger.sendWarn("阿里云SDK初始化失败！");
        }
    }
}
