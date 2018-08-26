package com.girltest.netty.encode.bytes;

import com.common.util.SystemHWUtil;
import com.girltest.netty.dto.message.BytesMessageItem;
import com.girltest.netty.encode.filter.IBeforeEncodeFilter;
import com.girltest.netty.encode.filter.impl.BeforeEncodeFilterImpl;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class BytesMessageEncoder extends MessageToByteEncoder<BytesMessageItem> {
    private static final Logger log = LoggerFactory.getLogger(BytesMessageEncoder.class);
    private IBeforeEncodeFilter beforeEncodeFilter = new BeforeEncodeFilterImpl();
    /***
     * 如果保存失败,请确认 com/girltest/netty/handle/CommonChannelnitializer.java 中 BytesMessageDecoder 第一个参数 maxFrameLength
     * @param s
     * @param byteBuf
     * @param writeInfo
     * @return
     */
    private static int dealExtraData(BytesMessageItem s, ByteBuf byteBuf, boolean writeInfo) {
        if (s.getType() == BytesMessageItem.TYPE_TRANSFER_TLV) {
            String dataType = s.getDataType();
            byte[] dataTypeBytes = null;
            try {
                dataTypeBytes = dataType.getBytes(SystemHWUtil.CHARSET_UTF);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //int 类型占用的字节数
            int typeIntegerLength = 4;
            System.out.println("dataTypeBytes :" + dataTypeBytes.length);
            if (dataTypeBytes == null
                    || dataTypeBytes.length == 0) {
                if (writeInfo) {
                    byteBuf.writeInt(0);
                }
                return typeIntegerLength;
            } else {
                if (writeInfo) {
                    byteBuf.writeInt(dataTypeBytes.length);
                    byteBuf.writeBytes(dataTypeBytes);
                }
                return typeIntegerLength + dataTypeBytes.length;
            }
        }
        return 0;
    }

    /***
     * 如果保存失败,请确认 com/girltest/netty/handle/CommonChannelnitializer.java 中 BytesMessageDecoder 第一个参数 maxFrameLength
     * @param channelHandlerContext
     * @param s
     * @param byteBuf
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BytesMessageItem s, ByteBuf byteBuf) {
        if (0 == s.getType()) {
            s.setType(BytesMessageItem.TYPE_TRANSFER);
        }
        byteBuf.writeByte(s.getType());

        byte[] bytes = s.getBinaryDataNoLength();
        if (beforeEncodeFilter != null) {
            bytes = beforeEncodeFilter.beforeEncode(bytes);
        }

        long lengthTotal = bytes.length + dealExtraData(s, byteBuf, false);
        byteBuf.writeLong(lengthTotal + 8/*byteBuf.writeInt(bytes.length)*/);// 除总长度字段外的所有数据的字节长度
        byteBuf.writeLong(bytes.length);// 下一个数据块的字节长度
        System.out.println("BytesMessageEncoder :" + bytes.length);
        byteBuf.writeBytes(bytes);
        dealExtraData(s, byteBuf, true);
    }
}
