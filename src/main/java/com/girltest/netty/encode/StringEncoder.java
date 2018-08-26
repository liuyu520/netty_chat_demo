package com.girltest.netty.encode;

import com.common.util.SystemHWUtil;
import com.girltest.netty.dto.message.MessageItem;
import com.string.widget.util.ValueWidget;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringEncoder extends MessageToByteEncoder<MessageItem> {
    private static final Logger log = LoggerFactory.getLogger(StringEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageItem s, ByteBuf byteBuf) throws Exception {
        if (0 == s.getType()) {
            s.setType(MessageItem.TYPE_TRANSFER);
        }
        byteBuf.writeByte(s.getType());
        String data = s.getData();
        if (ValueWidget.isNullOrEmpty(data)) {
            String errorMessage = "data MUST NOT be empty,消息体不允许为空";
            System.out.println("errorMessage :" + errorMessage);
            throw new RuntimeException(errorMessage);
        }
        byte[] bytes = data.getBytes(SystemHWUtil.CHARSET_UTF);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
