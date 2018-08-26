package com.girltest.netty.encode;

import com.common.util.SystemHWUtil;
import com.girltest.netty.dto.message.MessageItem;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * lengthFieldLength:unsupported lengthFieldLength: 6 (expected: 1, 2, 3, 4, or 8)
 */
public class StringDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger log = LoggerFactory.getLogger(StringDecoder.class);

    /***
     * lengthFieldLength 只能为1,2,4,8<br />
     * unsupported lengthFieldLength: 6 (expected: 1, 2, 3, 4, or 8)
     * @param maxFrameLength
     * @param lengthFieldOffset
     * @param lengthFieldLength
     */
    public StringDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {

        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        if (lengthFieldLength == 5) {
            System.out.println(" lengthFieldLength只能为1,2,4,8,实际值:" + lengthFieldLength);
        }
    }

    @Override
    protected MessageItem decode(ChannelHandlerContext ctx, ByteBuf in2) throws Exception {
        ByteBuf in = (ByteBuf) super.decode(ctx, in2);
        if (in == null) {
            System.out.println("解码失败 :" + in2);
            return null;
        }
        byte type = in.readByte();//消息类型
        int length = in.readInt();
        byte[] content = new byte[length];
        in.readBytes(content);
        java.lang.String mesg = new java.lang.String(content, SystemHWUtil.CHARSET_UTF);
        MessageItem messageItem = new MessageItem();
        messageItem.setType(type);
        messageItem.setData(mesg);
        System.out.println("mesg :" + mesg);

        in.release();
        return messageItem;
    }
}
