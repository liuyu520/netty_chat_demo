package com.girltest.netty.consoleapp;

import com.girltest.netty.dto.message.ChannelHandleDto;
import com.girltest.netty.handle.bytes.ConsoleServerHandler;
import com.girltest.netty.util.ServerConfigUtil;
import io.netty.channel.ChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConsole {
    private static final Logger log = LoggerFactory.getLogger(ServerConsole.class);

    public static void main(String[] args) {

    }

    protected void startServerBootstrap() {
        ChannelHandleDto channelHandleDto = new ChannelHandleDto();
        ChannelHandler channelHandler = new ConsoleServerHandler(channelHandleDto);
        ServerConfigUtil.serverStartAccept(channelHandler, 8088);

    }

}
