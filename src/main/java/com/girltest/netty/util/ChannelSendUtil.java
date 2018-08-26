package com.girltest.netty.util;

import com.girltest.netty.dto.message.BytesMessageItem;
import com.girltest.netty.dto.message.MessageItem;
import com.swing.dialog.toast.ToastMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.awt.*;

public class ChannelSendUtil {
    public static ChannelFuture writeAndFlush(Channel channel, Object o) {
        if (null == channel) {
            ToastMessage.toast("还未连接", 1000, Color.RED);
            return null;
        }
        if (String.class.isInstance(o)) {
            o = BytesMessageItem.getInstance((String) o).setType(MessageItem.TYPE_TRANSFER);
        }
        return channel.writeAndFlush(o);
    }
}
