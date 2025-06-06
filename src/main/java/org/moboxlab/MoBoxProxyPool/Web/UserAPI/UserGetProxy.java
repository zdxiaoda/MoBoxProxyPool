package org.moboxlab.MoBoxProxyPool.Web.UserAPI;

import com.alibaba.fastjson.JSONObject;
import org.moboxlab.MoBoxProxyPool.Cache.CacheProxy;
import org.moboxlab.MoBoxProxyPool.Object.ObjectECS;

public class UserGetProxy {
    /**
     * 基础请求格式（POST）：
     * {
     *     token: "xxx",
     *     type: "getProxy",
     *     sessionID: "xxx"
     * }
     * 基础返回格式：
     * {
     *     "status": true,
     *     "proxyHost": "xx",
     *     "proxyUser": "xx",
     *     "proxyPassword: "xx",
     *     "remain": "xx"
     *     ...
     * }
     */
    public static void getResponse(JSONObject data,JSONObject responseData) {
        ObjectECS ecs = CacheProxy.getProxy(data.getString("sessionID"));
        if (ecs == null) return;
        responseData.replace("status",true);
        responseData.put("proxyHost",ecs.IP+":"+ecs.proxyPort);
        responseData.put("proxyUser",ecs.proxyAccount);
        responseData.put("proxyPassword",ecs.proxyPassword);
        responseData.put("remain",ecs.getRemainTime());
    }
}
