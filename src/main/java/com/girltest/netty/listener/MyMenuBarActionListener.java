package com.girltest.netty.listener;

import com.girltest.netty.dto.message.BytesMessageItem;
import com.girltest.netty.dto.message.MessageItem;
import com.girltest.netty.swing.GenericChatFrame;
import com.girltest.netty.util.ChannelSendUtil;
import com.io.hw.file.util.FileUtils;
import com.string.widget.util.ValueWidget;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.toast.ToastMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MyMenuBarActionListener implements ActionListener {
    private static final Logger log = LoggerFactory.getLogger(MyMenuBarActionListener.class);
    private GenericChatFrame chatFrame;
    protected Channel channel;

    public MyMenuBarActionListener(GenericChatFrame chatFrame) {
        this.chatFrame = chatFrame;
    }

    private static void closeChannel(GenericChatFrame chatFrame) {
        if (chatFrame.getChannel() == null) {
            String msg = "您还没有连接";
            System.out.println("msg :" + msg);
            log.error(msg);
            ToastMessage.toast(msg, 1000, Color.RED);
            return;
        }
        ChannelFuture future = ChannelSendUtil.writeAndFlush(chatFrame.getChannel(), BytesMessageItem.getInstance("断开").setType(BytesMessageItem.TYPE_DISCONNECT));
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        chatFrame.getChannel().closeFuture();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (ValueWidget.isNullOrEmpty(command)) {
            return;
        }
        if (command.equals("重新连接")) {
//            GUIUtil23.alert("暂未实现");
            chatFrame.connect();
            enableChatInputTxbox(true);
        } else if (command.equals("断开连接")) {
            closeChannel(chatFrame);
            chatFrame.setChannel(null);
            enableChatInputTxbox(false);
            /*try {
                future.await();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    ToastMessage.toastRight("端口连接", 2000);
                }
            });*/
        } else if (command.equals("断开并退出服务器")) {
            disconnectAndExit();
        } else if (command.equals("上传文件")) {
            uploadFileAction();
        }
    }

    /***
     * 上传文件
     */
    private void uploadFileAction() {
        java.io.File toUploadFile = DialogUtil.chooseFileDialog(null, " 上传文件", chatFrame, "jpg");
        if (null == toUploadFile) {
            ToastMessage.toast("取消上传", 1000, Color.RED);
            return;
        }
        System.out.println(" :" + toUploadFile);
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
            ChannelSendUtil.writeAndFlush(chatFrame.getChannel(), bytesMessageItem);
        }
    }

    private void disconnectAndExit() {
        ChannelSendUtil.writeAndFlush(chatFrame.getChannel(), BytesMessageItem.getInstance("断开").setType(BytesMessageItem.TYPE_EXIT_SERVER));
        closeChannel(chatFrame);
        chatFrame.setChannel(null);
        enableChatInputTxbox(false);
        System.exit(0);
    }

    private void enableChatInputTxbox(boolean enable) {
        chatFrame.getInputTextArea().setEnabled(enable);
    }
}
