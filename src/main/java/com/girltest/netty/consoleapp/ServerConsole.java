package com.girltest.netty.consoleapp;

import com.girltest.netty.dto.ChannelHandleDto;
import com.girltest.netty.dto.upload.UploadedFileSavePathDto;
import com.girltest.netty.enum2.EMessageType;
import com.girltest.netty.enum2.EServerCmd;
import com.girltest.netty.handle.console.ConsoleChannelnitializer;
import com.girltest.netty.handle.console.server.IChannelListener;
import com.girltest.netty.listener.WaitingForUserInputListener;
import com.girltest.netty.swing.callback.Callback;
import com.girltest.netty.util.ChannelSendUtil;
import com.girltest.netty.util.PrintUtil;
import com.girltest.netty.util.ServerConfigUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConsole {
    static ServerConsole serverConsole = null;
    static int port = 8088;
    private static final Logger log = LoggerFactory.getLogger(ServerConsole.class);
    private IChannelListener channelListener;
    private static UploadedFileSavePathDto uploadedFileSavePathDto = UploadedFileSavePathDto.getInstance();
    private WaitingForUserInputListener waitingForUserInputListener = new WaitingForUserInputListener();
    public static void main(String[] args) {

        if (args != null && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        serverConsole = new ServerConsole();
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
            public void consoleInputHandle(String msg) {
                if (EServerCmd.RE_CONNECT.getDisplayName().equals(msg)) {
                    reconnect();
                    return;
                } else if (EServerCmd.SERVER_EXIT.getDisplayName().equals(msg)) {
                    if (null != channel) {
                        channel.closeFuture();
                    }
                    PrintUtil.print("服务器退出.");
                    System.exit(0);
                } else if (msg.startsWith(EServerCmd.GET_SAVED_FILE.getDisplayName())) {
                    uploadedFileSavePathDto.setSavedPath(msg.replace(EServerCmd.GET_SAVED_FILE.getDisplayName() + ":", ""));
                }

//                System.out.println("channel :" + channel);
                ChannelSendUtil.writeAndFlush(channel, msg);
            }

            @Override
            public void setCurrentChannel(Channel currentChannel) {
                this.channel = currentChannel;
            }
        };
    }

    private void reconnect() {
        String msg2 = "重新连接";
        System.out.println("msg2 :" + msg2);
        serverConsole.startServerBootstrap(port);
    }

    protected void startServerBootstrap(int port) {
        ChannelHandleDto channelHandleDto = new ChannelHandleDto();
        channelHandleDto.setTitle("服务器");
        channelHandleDto.setUploadedFileSavePathDto(uploadedFileSavePathDto);
        channelHandleDto.setCallback(new Callback() {
            @Override
            public String callback(String msg, Object ctx, EMessageType type) {
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
                            PrintUtil.print("收到的 :" + msg);
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
        waitingForUserInputListener.setChannelListener(channelListener);
        waitingForUserInputListener.execute();

        // 3. 启动socket server服务
        //是阻塞的,所以必须放在waitingUserInput() 后面
//        ThreadPoolUtil.execute(() -> {
        ChannelHandler channelHandler = new ConsoleChannelnitializer(channelHandleDto);
        ServerConfigUtil.serverStartAccept(channelHandler, port);
//        });
    }


}
