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
     * token: "xxx",
     * type: "xxx",
     * ...
     * }
     * 基础返回格式：
     * {
     * "status": true,
     * ...
     * }
     */
    @Override
    public void handle(HttpExchange exchange) {
        try {
            WebBasic.initBasicResponse(exchange);
            String ip = WebBasic.getRemoteIP(exchange);
            BasicInfo.sendDebug(ip + " " + exchange.getRequestURI().toString());
            JSONObject data = WebBasic.loadRequestData(exchange);

            JSONObject response = new JSONObject();
            response.put("status", false);

            // 检查data是否为null（JSON解析失败或请求体为空）
            if (data == null) {
                BasicInfo.sendDebug("请求数据解析失败，data为null");
                response.put("error", "Invalid JSON format or empty request body");
                WebBasic.completeResponse(exchange, response, new JSONObject());
                return;
            }

            data.put("ip", ip);
            BasicInfo.sendDebug(data.toString());

            // 数据结构校验
            if (!data.containsKey("token") || !data.containsKey("type")) {
                BasicInfo.sendDebug("请求缺少必要参数: token或type");
                response.put("error", "Missing required parameters: token or type");
                WebBasic.completeResponse(exchange, response, data);
                return;
            }
            // token校验
            String receiveToken = data.getString("token");
            if (!receiveToken.equals(BasicInfo.config.getString("httpToken"))) {
                WebBasic.completeResponse(exchange, response, data);
                return;
            }
            // 类型对应操作
            switch (data.getString("type")) {
                case "getProxy":
                    UserGetProxy.getResponse(data, response);
                    break;
                default:
                    break;
            }
            WebBasic.completeResponse(exchange, response, data);
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
        }
    }
}
