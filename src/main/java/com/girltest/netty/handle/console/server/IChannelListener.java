package com.girltest.netty.handle.console.server;

import io.netty.channel.Channel;

public interface IChannelListener {
    /***
     * 处理用户命令行键盘输入
     * @param msg
     */
    void consoleInputHandle(String msg);

    void setCurrentChannel(Channel currentChannel);
}
