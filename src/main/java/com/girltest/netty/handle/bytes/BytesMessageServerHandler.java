package com.girltest.netty.handle.bytes;

import com.common.bean.DialogBean;
import com.common.util.SystemHWUtil;
import com.girltest.netty.dto.message.BytesMessageItem;
import com.girltest.netty.enum2.EMessageType;
import com.girltest.netty.swing.callback.Callback;
import com.io.hw.file.util.FileUtils;
import com.string.widget.util.ValueWidget;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.toast.ToastMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BytesMessageServerHandler extends SimpleChannelInboundHandler<BytesMessageItem> {
    private static final Logger log = LoggerFactory.getLogger(BytesMessageServerHandler.class);
    private Callback callback;
    private String title;

    public BytesMessageServerHandler(Callback callback, String title) {
        this.callback = callback;
        this.title = title;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /***
     * 保存字节数组到本地文件 <br />
     * 如果保存失败,请确认 com/girltest/netty/handle/CommonChannelnitializer.java 中 BytesMessageDecoder 第一个参数 maxFrameLength
     * @param msg
     */
    private static void saveToFile(BytesMessageItem msg) {
        byte[] bytesData = msg.getBinaryDataNoLength();
        String filePath = "/tmp/uploaded/a.jpg";//TODO
        DialogBean dialogBean = DialogUtil.showSaveDialog(null, null, new File(filePath), null);
        if (!dialogBean.isSuccess()) {
            ToastMessage.toast("取消操作", 2000);
            return;
        }
        try {
            FileUtils.writeBytesToFile(bytesData, dialogBean.getSelectedFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BytesMessageItem msg) throws Exception {
//        System.out.println("msg :" + msg);
        byte type = msg.getType();
        switch (type) {
            case BytesMessageItem.TYPE_TRANSFER:
//                System.out.println(getTitle()+" :TYPE_TRANSFER"+msg.getData());
                if (null == callback) {
                    break;
                }
                callback.callback(new String(msg.getBinaryDataNoLength(), SystemHWUtil.CHARSET_UTF), ctx, EMessageType.messageArrivied);
                break;
            case BytesMessageItem.TYPE_DISCONNECT://关闭连接,断开连接
                dealDisconnect(ctx);
                System.out.println(getTitle() + "断开连接 :");
                break;
            case BytesMessageItem.TYPE_EXIT_SERVER://关闭连接,断开连接,退出java应用程序
                dealDisconnect(ctx);
                System.exit(0);
                break;
            case BytesMessageItem.TYPE_TRANSFER_TLV:
                //如果保存失败,请确认 com/girltest/netty/handle/CommonChannelnitializer.java 中 BytesMessageDecoder 第一个参数 maxFrameLength
                //使用线程,解决死锁问题
//                ThreadPoolUtil.execute();
                SwingUtilities.invokeLater(() -> {
                    dealTransferTlv(msg);
                    msg.setBinaryDataNoLength(null);// 便于 gc
                });
                break;
        }


        /*ByteBuf byteBuf = (ByteBuf) msg;
        String mseg = readString(byteBuf);*/
//        System.out.println("收到的消息 :" + msg);
    }

    /***
     * 如果保存失败,请确认 com/girltest/netty/handle/CommonChannelnitializer.java 中 BytesMessageDecoder 第一个参数 maxFrameLength
     * @param msg
     */
    private void dealTransferTlv(BytesMessageItem msg) {
        String dataType = msg.getDataType();
        if (ValueWidget.isNullOrEmpty(dataType)) {
            return;
        }
        switch (dataType) {
            case "pic":
                int result = JOptionPane.showConfirmDialog(null, "Are you sure to save to local file ?", "确认",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    saveToFile(msg);
                } else {
                    ToastMessage.toast("取消保存", 1000, Color.RED);
                }
                break;
        }

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
