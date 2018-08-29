package com.girltest.netty.util;

import com.girltest.netty.dto.message.BytesMessageItem;
import com.girltest.netty.dto.message.MessageItem;
import com.io.hw.file.util.FileUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ChannelSendUtil {
    private static final Logger log = LoggerFactory.getLogger(ChannelSendUtil.class);
    public static ChannelFuture writeAndFlush(Channel channel, Object o) {
        if (null == channel) {
//            ToastMessage.toast("还未连接", 1000, Color.RED);
            return null;
        }
        if (String.class.isInstance(o)) {
            o = BytesMessageItem.getInstance((String) o).setType(MessageItem.TYPE_TRANSFER);
        }
        return channel.writeAndFlush(o);
    }

    public static void transferBinaryFile(Channel channel, File toUploadFile) {
        byte[] bytes = null;
        try {
            bytes = FileUtils.getBytes4File(toUploadFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != bytes) {
            BytesMessageItem bytesMessageItem = new BytesMessageItem();
            bytesMessageItem.setBinaryDataNoLength(bytes);
            bytesMessageItem.setLength2(bytes.length);
            bytesMessageItem.setType(MessageItem.TYPE_TRANSFER_TLV);
            bytesMessageItem.setDataType("pic");
            ChannelSendUtil.writeAndFlush(channel, bytesMessageItem);
        }
    }

}
