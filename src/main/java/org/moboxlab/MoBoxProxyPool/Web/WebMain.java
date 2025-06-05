package org.moboxlab.MoBoxProxyPool.Web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import org.moboxlab.MoBoxProxyPool.BasicInfo;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class WebMain {
    public static HttpServer server;
    public static HttpServerProvider provider;
    public static void initWeb() {
        try {
            provider = HttpServerProvider.provider();
            server = provider.createHttpServer(new InetSocketAddress(BasicInfo.config.getInteger("httpPort")),0);
            server.createContext("/API",new WebHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            BasicInfo.logger.sendInfo("WebAPI已启动于本地端口"+BasicInfo.config.getInteger("httpPort")+"！");
        } catch (Exception e) {
            BasicInfo.logger.sendException(e);
        }

    }
}
