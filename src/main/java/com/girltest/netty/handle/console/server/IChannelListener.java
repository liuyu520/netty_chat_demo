package com.girltest.netty.handle.console.server;

import io.netty.channel.Channel;

public interface IChannelListener {
    void send(String msg);

    void setCurrentChannel(Channel currentChannel);
}
