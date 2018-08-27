package com.girltest.netty.swing;

import com.common.thread.ThreadPoolUtil;
import com.girltest.netty.handle.CommonChannelnitializer;
import com.girltest.netty.util.ServerConfigUtil;
import io.netty.channel.ChannelHandler;
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
        ChannelHandler channelHandler = new CommonChannelnitializer(callback, getTitle());
        ServerConfigUtil.serverStartAccept(channelHandler, port);
    }


}
