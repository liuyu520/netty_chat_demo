package com.girltest.netty.swing;

import com.common.thread.ThreadPoolUtil;
import com.girltest.netty.handle.CommonChannelnitializer;
import com.girltest.netty.util.ChannelSendUtil;
import com.string.widget.util.ValueWidget;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/***
 * 聊天工具服务器端(socket server)<br />
 * 客户端是如何得到服务器端的socket句柄的?<br />
 * connect 的监听器ChannelFutureListener 中<br />
 * 发送消息是在哪里触发的?<br />
 * 在 GenericChatFrame 的 layoutAction 方法中<br />
 * 快捷键:Alt+Enter:触发"Enter键发送"复选框的选中和取消选中(toggle)
 */
public class ClientApp extends GenericChatFrame {


    @Override
    protected void layoutAction(JPanel rootPanel) {
        super.layoutAction(rootPanel);
        setLoc(400, 600, 210);
        try {
            setIcon("img/socket_Client.png", this.getClass());
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (null != getChannel()) {
                    int result = JOptionPane.showConfirmDialog(null, "Are you sure to RE connect ?", "确认",
                            JOptionPane.OK_CANCEL_OPTION);
                    if (result != JOptionPane.OK_OPTION) {
//                        ToastMessage.toast("取消", 2000);
                        inputTextArea.requestFocus();
                        return;
                    }
                }
                System.out.println("开始连接服务器 :");
                ThreadPoolUtil.execute(() -> {
                    connect();
                });
                connectButton.setEnabled(false);
                //connectButton 20秒之后,恢复可用状态
                ThreadPoolUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(20 * 1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        connectButton.setEnabled(true);
                    }
                });
            }
        });
    }

    @Override
    public String getTitle() {
        return "客户端";
    }

    public static void main(String[] args) {
        ClientApp serverApp = new ClientApp();

    }

    @Override
    public void connect() {
        NioEventLoopGroup workerGroup;

        Bootstrap bootstrap;

        workerGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(/*new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new StringDecoder(1024 * 1024, 0, 4));
                //用于接收proxy服务器发送过来的消息
                ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String o) throws Exception {
                        *//*ByteBuf byteBuf = (ByteBuf) o;
                        String mseg = StringHttpServerHandler.readString(byteBuf);*//*
                        System.out.println("客户端收到 mseg :" + o);
                    }
                });

            }
        }*/new CommonChannelnitializer(callback, getTitle()));

        bootstrap.connect("localhost", port)//客户端尝试连接服务器
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {//连接成功
//                        setChannel(future.channel());
                        ChannelSendUtil.writeAndFlush(getChannel(), "这是客户端");
//                        ToastMessage.toast("连接成功", 1000);
                        System.out.println("客户端连接成功 :");
                        sendButton.setEnabled(true);
                        getInputTextArea().setEnabled(true);
                        if (!ValueWidget.isNullOrEmpty(getInputTextArea().getText2())) {
                            sendMsg(false);
                        }
                    }

                });
    }
}
