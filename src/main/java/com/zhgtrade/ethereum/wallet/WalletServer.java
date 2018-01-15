package com.zhgtrade.ethereum.wallet;

import com.alibaba.druid.support.http.WebStatFilter;
import com.zhgtrade.ethereum.wallet.utils.ConfigUtils;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.Date;

/**
 * 招股金服
 * CopyRight : www.zhgtrade.com
 * Author : 林超（362228416@qq.com）
 * Date： 2017-08-09 14:18
 */
public class WalletServer {

    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure("conf/log4j.properties");

        long startTime = System.currentTimeMillis();
        System.out.printf("USE SYSTEM %s %s %s \n", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"));
        System.out.printf("USE JDK %s %s \n", System.getProperty("java.version"), System.getProperty("java.vm.specification.name"));
        int port = Integer.parseInt(ConfigUtils.getConfig("http.port"));//ETH

        if (args.length > 0) {
            port = Integer.valueOf(args[0]);
        }

        ServletContextHandler servletContext = new ServletContextHandler();
        servletContext.setContextPath("/");

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });
        HandlerCollection handlerCollection = new HandlerCollection();

        ServletContextHandler springMvcHandler = new ServletContextHandler();
        springMvcHandler.setContextPath("/");

        springMvcHandler.addServlet(new ServletHolder("dbadmin-servlet", DBAdminServlet.class), "/dbadmin");
//        springMvcHandler.addServlet(new ServletHolder("admin-servlet", AdminServlet.class), "/mytokens/admin/*");
        springMvcHandler.addServlet(new ServletHolder("rpc-servlet", RpcServlet.class), "/*");
        springMvcHandler.addServlet(new ServletHolder("druid", com.alibaba.druid.support.http.StatViewServlet.class), "/druid/*");
        // WEB监控
        FilterHolder webStatFilter = new FilterHolder(new WebStatFilter());
        webStatFilter.setInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        springMvcHandler.addFilter(webStatFilter, "/*", null);

        handlerCollection.setHandlers(new Handler[]{springMvcHandler});

        server.setHandler(handlerCollection);

        server.start();
        System.out.printf("Server started take %d ms, open your in browser http://localhost:%d/\n", (System.currentTimeMillis() - startTime), port);
        System.out.println("Start Time " + new Date());

        startSyncTask();
//        startRequestTask();
        server.join();

    }

    private static void startSyncTask() {
        Thread thread = new Thread(new BlockSyncTask());
        thread.setName("sync task");
        thread.start();
    }

    private static void startRequestTask() {
        Thread thread = new Thread(new RequestTokenTask());
        thread.setName("requestToken task");
        thread.start();
    }

//    private static void startToMainWalletTask() {
//        Thread thread = new Thread(new AggregateAmountTask());
//        thread.setName("to main wallet task");
//        thread.start();
//    }

}
