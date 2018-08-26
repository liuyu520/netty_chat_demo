package com.girltest.netty.encode.bytes;

import com.common.util.SystemHWUtil;
import com.girltest.netty.dto.message.BytesMessageItem;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * lengthFieldLength:unsupported lengthFieldLength: 6 (expected: 1, 2, 3, 4, or 8)
 */
public class BytesMessageDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger log = LoggerFactory.getLogger(BytesMessageDecoder.class);

    /***
     * lengthFieldLength 只能为1,2,4,8<br />
     * unsupported lengthFieldLength: 6 (expected: 1, 2, 3, 4, or 8)
     * @param maxFrameLength
     * @param lengthFieldOffset
     * @param lengthFieldLength
     */
    public BytesMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {

        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        if (lengthFieldLength == 5) {
            System.out.println(" lengthFieldLength只能为1,2,4,8,实际值:" + lengthFieldLength);
        }
    }

    @Override
    protected BytesMessageItem decode(ChannelHandlerContext ctx, ByteBuf in2) throws Exception {
        ByteBuf in = (ByteBuf) super.decode(ctx, in2);
        if (in == null) {
            System.out.println("解码失败 :" + in2);
            return null;
        }
        byte type = in.readByte();//消息类型
        in.readLong();//除总长度字段外的所有数据的字节长度
        Long length = in.readLong();//下一个数据块的字节长度
        int readMaxLength = Integer.MAX_VALUE;
        byte[] content = null;
        if (length < readMaxLength) {
            content = new byte[length.intValue()];
        } else {
            String msg = "数据过大#######";
            System.out.println(msg);

        }

        in.readBytes(content);
        BytesMessageItem messageItem = new BytesMessageItem();
        messageItem.setType(type);
        messageItem.setBinaryDataNoLength(content);
        messageItem.setLength2(length);
        //如果保存失败,请确认 com/girltest/netty/handle/CommonChannelnitializer.java 中 BytesMessageDecoder 第一个参数 maxFrameLength
        if (type == BytesMessageItem.TYPE_TRANSFER_TLV) {
            int dataTypeBytesLength = in.readInt();
            System.out.println("dataTypeBytesLength :" + dataTypeBytesLength);
            if (dataTypeBytesLength > 0) {
                byte[] dataType = new byte[dataTypeBytesLength];
                in.readBytes(dataType);
                messageItem.setDataType(new String(dataType, SystemHWUtil.CHARSET_UTF));
            }
        }
        in.release();
//        in2.release();
        return messageItem;
    }
}
