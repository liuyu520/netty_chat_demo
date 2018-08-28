package com.girltest.netty.handle.bytes;

import com.girltest.netty.dto.ChannelHandleDto;
import com.girltest.netty.dto.message.BytesMessageItem;

public interface IBusinessHandle {
    void handle(BytesMessageItem msg);

    void setChannelHandleDto(ChannelHandleDto channelHandleDto);
}
