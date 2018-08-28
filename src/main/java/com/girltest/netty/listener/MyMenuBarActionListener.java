package com.girltest.netty.listener;

import com.girltest.netty.config.ClientConfigDto;
import com.girltest.netty.dto.message.BytesMessageItem;
import com.girltest.netty.dto.message.MessageItem;
import com.girltest.netty.swing.GenericChatFrame;
import com.girltest.netty.util.ChannelSendUtil;
import com.girltest.netty.util.PrintUtil;
import com.io.hw.file.util.FileUtils;
import com.string.widget.util.ValueWidget;
import com.swing.dialog.DialogUtil;
import com.swing.dialog.toast.ToastMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MyMenuBarActionListener implements ActionListener {
    private static final Logger log = LoggerFactory.getLogger(MyMenuBarActionListener.class);
    private GenericChatFrame chatFrame;
    protected Channel channel;
    /***
     *  TODO shiyong配置文件
     */
    private ClientConfigDto configDto = new ClientConfigDto();

    public MyMenuBarActionListener(GenericChatFrame chatFrame) {
        this.chatFrame = chatFrame;
    }

    private static void closeChannel(GenericChatFrame chatFrame) {
        if (chatFrame.getChannel() == null) {
            String msg = "您还没有连接";
            PrintUtil.print("msg :" + msg);
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
        PrintUtil.print(" :" + toUploadFile);

        if (!toUploadFile.exists()) {
            if (configDto.isForceIntegryFileSuffix()) {
                PrintUtil.print("文件不存在,请确认路径:" + toUploadFile);
                return;
            }
            String path = toUploadFile.getAbsolutePath();
            //"/Users/whuanghkl/a11.txt.jpg"->"/Users/whuanghkl/a11.txt"
            path = path.replaceAll("\\.[^.]+$", "");
            toUploadFile = new File(path);
        }
        int sizeMb = 100;
        if (FileUtils.getFileSize2(toUploadFile) > sizeMb * 1024 * 1024) {
            int result = JOptionPane.showConfirmDialog(null, "文件超过 100 MB,是否确认上传大文件 ?", "确认",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }
        }
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
        int result = JOptionPane.showConfirmDialog(null, "Are you sure to 断开并关闭 ?", "确认",
                JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
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
