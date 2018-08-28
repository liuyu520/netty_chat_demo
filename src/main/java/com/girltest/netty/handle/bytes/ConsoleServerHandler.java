package com.girltest.netty.handle.bytes;

import com.common.util.SystemHWUtil;
import com.girltest.netty.dto.ChannelHandleDto;
import com.girltest.netty.dto.message.BytesMessageItem;
import com.girltest.netty.dto.upload.UploadedFileSavePathDto;
import com.girltest.netty.enum2.EMessageType;
import com.girltest.netty.enum2.EServerCmd;
import com.girltest.netty.swing.callback.Callback;
import com.girltest.netty.util.PrintUtil;
import com.io.hw.file.util.FileUtils;
import com.string.widget.util.ValueWidget;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ConsoleServerHandler extends SimpleChannelInboundHandler<BytesMessageItem> {
    private static final Logger log = LoggerFactory.getLogger(ConsoleServerHandler.class);
    @Getter
    @Setter
    private ChannelHandleDto channelHandleDto;

    public ConsoleServerHandler(ChannelHandleDto channelHandleDto) {
        this.channelHandleDto = channelHandleDto;
    }

    public Callback getCallback() {
        if (null == channelHandleDto) {
            return null;
        }
        return channelHandleDto.getCallback();
    }


    /***
     * 保存字节数组到本地文件 <br />
     * 如果保存失败,请确认 com/girltest/netty/handle/CommonChannelnitializer.java 中 BytesMessageDecoder 第一个参数 maxFrameLength
     * @param msg
     */
    private static void saveToFile2(BytesMessageItem msg, UploadedFileSavePathDto uploadedFileSavePathDto) throws IOException {
        byte[] bytesData = msg.getBinaryDataNoLength();
        String filePath = getInputPath(uploadedFileSavePathDto);
        if (filePath.endsWith(EServerCmd.GET_SAVED_FILE_CANCEL.getDisplayName())) {
            PrintUtil.print("取消保存");
            return;
        }
        PrintUtil.print("获得用户输入路径:" + filePath);
        uploadedFileSavePathDto.setSavedPath(null);

        // 判断用户输入的文件是否已经存在
        if (new File(filePath).exists()) {
            filePath = FileUtils.modifyFilePath(filePath, "bak").getAbsolutePath();
            PrintUtil.print("文件已经存在,所以系统自动更名为:" + filePath);
        }

            FileUtils.writeBytesToFile(bytesData, filePath);
            PrintUtil.print("保存成功,文件大小:" + FileUtils.formatFileSize2(bytesData.length));

    }

    private static void saveToFile(BytesMessageItem msg, UploadedFileSavePathDto uploadedFileSavePathDto) {
        try {
            saveToFile2(msg, uploadedFileSavePathDto);
        } catch (IOException e) {
//            e.printStackTrace();
            PrintUtil.print(e.getMessage());
            saveToFile(msg, uploadedFileSavePathDto);
        }
    }


    /***
     * 阻塞方法
     * @param uploadedFileSavePathDto
     * @return
     */
    private static String getInputPath(UploadedFileSavePathDto uploadedFileSavePathDto) {
        System.out.println("请输入保存路径 :");
        //死循环,等待用户输入
        while (ValueWidget.isNullOrEmpty(uploadedFileSavePathDto.getSavedPath())) {
            //等待用户输入保存路径
            //格式:path:/tmp/uploaded/cc32c.jpg
        }
        return uploadedFileSavePathDto.getSavedPath();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BytesMessageItem msg) throws Exception {
//        System.out.println("msg :" + msg);
        byte type = msg.getType();
        switch (type) {
            case BytesMessageItem.TYPE_TRANSFER:
//                System.out.println(getTitle()+" :TYPE_TRANSFER"+msg.getData());
                if (null == getCallback()) {
                    break;
                }
                getCallback().callback(new String(msg.getBinaryDataNoLength(), SystemHWUtil.CHARSET_UTF), ctx, EMessageType.messageArrivied);
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
                channelHandleDto.getUploadedFileSavePathDto().setSavedPath(null);
                dealTransferTlv(msg);
                msg.setBinaryDataNoLength(null);// 便于 gc
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
                saveToFile(msg, this.channelHandleDto.getUploadedFileSavePathDto());
                break;
        }

    }

    private void dealDisconnect(ChannelHandlerContext ctx) {
        ctx.channel().close();
        if (null != getCallback()) {
            getCallback().callback(null, ctx, EMessageType.TYPE_DISCONNECT);
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
        if (null != getCallback()) {
            //服务器端需要清空链接
            getCallback().callback(null, ctx, EMessageType.channelRegistered);
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
        if (null != getCallback()) {
            getCallback().callback(null, ctx, EMessageType.handlerAdded);
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
        if (null != getCallback()) {
            getCallback().callback(null, ctx, EMessageType.channelActive);
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
        if (null != getCallback() && "服务器端".equals(getTitle())) {
            //服务器端需要清空链接
            getCallback().callback(null, ctx, EMessageType.channelUnregistered);
        }
        super.channelUnregistered(ctx);
    }

    public String getTitle() {
        if (null == channelHandleDto) {
            return null;
        }
        return channelHandleDto.getTitle();
    }
}
