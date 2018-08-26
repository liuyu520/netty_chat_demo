package com.girltest.netty.handle;

import com.girltest.netty.dto.message.MessageItem;
import com.girltest.netty.enum2.EMessageType;
import com.girltest.netty.swing.callback.Callback;
import com.swing.dialog.toast.ToastMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class ChatStringServerHandler extends SimpleChannelInboundHandler<MessageItem> {
    private static final Logger log = LoggerFactory.getLogger(ChatStringServerHandler.class);
    private Callback callback;
    private String title;

    public ChatStringServerHandler(Callback callback, String title) {
        this.callback = callback;
        this.title = title;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageItem msg) throws Exception {
//        System.out.println("msg :" + msg);
        byte type = msg.getType();
        switch (type) {
            case MessageItem.TYPE_TRANSFER:
//                System.out.println(getTitle()+" :TYPE_TRANSFER"+msg.getData());
                if (null == callback) {
                    break;
                }
                callback.callback(msg.getData(), ctx, EMessageType.messageArrivied);
                break;
            case MessageItem.TYPE_DISCONNECT://关闭连接,断开连接
                dealDisconnect(ctx);
                System.out.println(getTitle() + "断开连接 :");
                break;
            case MessageItem.TYPE_EXIT_SERVER://关闭连接,断开连接,退出java应用程序
                dealDisconnect(ctx);
                System.exit(0);
                break;
        }


        /*ByteBuf byteBuf = (ByteBuf) msg;
        String mseg = readString(byteBuf);*/
//        System.out.println("收到的消息 :" + msg);
    }

    private void dealDisconnect(ChannelHandlerContext ctx) {
        ctx.channel().close();
        if (null != callback) {
            callback.callback(null, ctx, EMessageType.TYPE_DISCONNECT);
        }
    }

   /* public static String readString(ByteBuf byteBuf) throws UnsupportedEncodingException {
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        return new String(bytes, SystemHWUtil.CHARSET_UTF);
    }*/

    //通道注册成功
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(getTitle() + "channel register...");
        if (null != callback) {
            //服务器端需要清空链接
            callback.callback(null, ctx, EMessageType.channelRegistered);
        }
        super.channelRegistered(ctx);
    }

    /**
     * 自定义的Handler被添加,也就是在 CommonChannelnitializer 的initChannel方法中，
     * pipeline.addLast("testHttpServerHandler", new TestHttpServerHandler());
     * 这行代码执行的时候，该方法被触发
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println(getTitle() + "handler added..." + ctx.channel());
        if (null != callback) {
            callback.callback(null, ctx, EMessageType.handlerAdded);
        }
        super.handlerAdded(ctx);
    }

    /**
     * 通道处于活动状态，即可用状态
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(getTitle() + "channel active..." + ctx.channel());
        super.channelActive(ctx);
        if (null != callback) {
            callback.callback(null, ctx, EMessageType.channelActive);
        }
    }

    //通道处于不活动状态

    /***
     * 断开连接:1,第一步
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(getTitle() + "channel inactive...");
        super.channelInactive(ctx);
    }

    /**
     * 断开连接:2,第二步 <br />
     * 服务器端需要清空链接
     *
     * @param ctx
     * @throws Exception
     */
    //通道取消注册
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(getTitle() + "channel unregister...");
        ToastMessage.toast("断开连接", 2000, Color.RED);
        if (null != callback && "服务器端".equals(getTitle())) {
            //服务器端需要清空链接
            callback.callback(null, ctx, EMessageType.channelUnregistered);
        }
        super.channelUnregistered(ctx);
    }

    public String getTitle() {
        return title;
    }
}
