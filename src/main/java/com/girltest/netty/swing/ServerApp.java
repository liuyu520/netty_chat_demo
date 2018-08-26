package com.girltest.netty.swing;

import com.common.thread.ThreadPoolUtil;
import com.girltest.netty.handle.CommonChannelnitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;

/***
 * 聊天工具服务器端(socket server)<br />
 * 1,需要隐藏连接按钮<br />
 * 服务端是如何得到服务器端的socket句柄的?<br />
 * SimpleChannelInboundHandler 的channelActive 方法中<br />
 * 发送消息是在哪里触发的?<br />
 *   在 GenericChatFrame 的 layoutAction 方法中<br />
 *   快捷键:Alt+Enter:触发"Enter键发送"复选框的选中和取消选中(toggle)
 */
public class ServerApp extends GenericChatFrame {
    private static final Logger log = LoggerFactory.getLogger(ServerApp.class);

    @Override
    protected void layoutAction(JPanel rootPanel) {
        super.layoutAction(rootPanel);
        setLoc(400, 600, -210);
        try {
            setIcon("img/socket_server2.png", this.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //1. 画界面
        //隐藏连接按钮
        super.connectButton.setVisible(false);
        //2. 绑定事件

        //3. 启动socket服务
        ThreadPoolUtil.execute(new Runnable() {
            @Override
            public void run() {
                startServerBootstrap();
            }
        });
        //4.
    }

    @Override
    public String getTitle() {
        return "服务器端";
    }

    @Override
    public void connect() {
        throw new RuntimeException("socket 服务器端不支持");
    }

    public static void main(String[] args) {
        ServerApp serverApp = new ServerApp();

    }

    protected void startServerBootstrap() {
/**
 * bossGroup, 父类的事件循环组只是负责连接，获取到连接后交给 workergroup子的事件循环组，
 * 参数的获取，业务的处理等工作均是由workergroup这个子事件循环组来完成，一个事件循环组一样
 * 可以完成所有的工作，但是Netty推荐的方式是使用两个事件循环组。
 */
        EventLoopGroup bossGroup = new NioEventLoopGroup();  //创建父事件循环组
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //创建子类的事件循环组

        try {
            //创建启动服务器的对象
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            /**
             * group方法接收两个参数， 第一个为父时间循环组，第二个参数为子事件循环组
             */
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)  //bossGroup的通道，只是负责连接
                    .childHandler(new CommonChannelnitializer(callback, getTitle())); //workerGroup的处理器，
            System.out.println("启动服务 :");
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();  //绑定端口
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
