package org.moboxlab.MoBoxProxyPool.Web;

import com.alibaba.fastjson.JSONObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.moboxlab.MoBoxProxyPool.BasicInfo;
//import org.moboxlab.MoBoxProxyPool.Web.NodeAPI.*;
import org.moboxlab.MoBoxProxyPool.Web.UserAPI.*;

public class WebHandler implements HttpHandler {
    /**
     * 基础请求格式（POST）：
     * {
     *     token: "xxx",
     *     type: "xxx",
     *     ...
     * }
     * 基础返回格式：
     * {
     *     "status": true,
     *     ...
     * }
     */
    @Override
    public void handle(HttpExchange exchange) {
        try {
            WebBasic.initBasicResponse(exchange);
            String ip = WebBasic.getRemoteIP(exchange);
            BasicInfo.sendDebug(ip+" "+exchange.getRequestURI().toString());
            JSONObject data = WebBasic.loadRequestData(exchange);
            data.put("ip",ip);
            BasicInfo.sendDebug(data.toString());

            JSONObject response = new JSONObject();
            response.put("status",false);
            //数据结构校验
            if (!data.containsKey("token") || !data.containsKey("type")) {
                WebBasic.completeResponse(exchange,response,data);
                return;
            }
            //token校验
            String receiveToken = data.getString("token");
            if (!receiveToken.equals(BasicInfo.config.getString("httpToken"))) {
                WebBasic.completeResponse(exchange,response,data);
                return;
            }
            //类型对应操作
            switch (data.getString("type")) {
                case "getProxy":
                    UserGetProxy.getResponse(data,response);
                    break;
                default:
                    break;
            }
            WebBasic.completeResponse(exchange,response,data);
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
        }
    }
}
