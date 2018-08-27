package com.girltest.netty.consoleapp;

import com.common.thread.ThreadPoolUtil;
import com.girltest.netty.dto.ChannelHandleDto;
import com.girltest.netty.enum2.EMessageType;
import com.girltest.netty.handle.console.ConsoleChannelnitializer;
import com.girltest.netty.handle.console.server.IChannelListener;
import com.girltest.netty.swing.callback.Callback;
import com.girltest.netty.util.ChannelSendUtil;
import com.girltest.netty.util.ServerConfigUtil;
import com.string.widget.util.ValueWidget;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConsole {
    private static final Logger log = LoggerFactory.getLogger(ServerConsole.class);
    private IChannelListener channelListener;
    public static void main(String[] args) {
        int port = 8088;
        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        ServerConsole serverConsole = new ServerConsole();
        //启动socket server服务
        serverConsole.startServerBootstrap(port);
    }

    public ServerConsole() {
        channelListener();
    }

    private void channelListener() {
        channelListener = new IChannelListener() {
            protected Channel channel;

            @Override
            public void send(String msg) {
                ChannelSendUtil.writeAndFlush(channel, msg);
            }

            @Override
            public void setCurrentChannel(Channel currentChannel) {
                this.channel = currentChannel;
            }
        };
    }

    protected void startServerBootstrap(int port) {
        ChannelHandleDto channelHandleDto = new ChannelHandleDto();
        channelHandleDto.setTitle("服务器");
        channelHandleDto.setCallback(new Callback() {
            @Override
            public String callback(String msg, Object ctx, EMessageType type) {
                print(msg);
                ChannelHandlerContext channelHandlerContext = (ChannelHandlerContext) ctx;
                if (null != type) {
                    // 统一在"channelRegistered"中设置Channel 句柄
                    switch (type) {
                        case channelActive:

                            break;
                        case channelRegistered:
                            channelListener.setCurrentChannel(channelHandlerContext.channel());
                            break;
                        case messageArrivied:
                            break;
                        case channelUnregistered:
                            channelListener.setCurrentChannel(null);
                            break;
                    }

                }
                return null;
            }
        });


        //2. 启动线程,获取命令行用户输入
        waitingUserInput();

        // 3. 启动socket server服务
        //是阻塞的,所以必须放在waitingUserInput() 后面
        ThreadPoolUtil.execute(() -> {
            ChannelHandler channelHandler = new ConsoleChannelnitializer(channelHandleDto);
            ServerConfigUtil.serverStartAccept(channelHandler, port);
        });
    }

    /**
     * 等待用户输入
     */
    private void waitingUserInput() {
        ThreadPoolUtil.execute(() -> {
            BufferedReader reader = null;
            try {
                while (true) {
                    reader =
                            new BufferedReader(new InputStreamReader(System.in));
                    // Reading data using readLine
                    String input = null;
                    try {
                        input = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    print("input:" + input);
                    if (!ValueWidget.isNullOrEmpty(input)) {
                        channelListener.send(input);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != reader) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        });
    }

    public static void print(String msg) {
        System.out.println("msg :" + msg);
    }
}
