package com.girltest.netty.swing;

import com.common.util.SystemHWUtil;
import com.girltest.netty.dto.message.BytesMessageItem;
import com.girltest.netty.dto.message.MessageItem;
import com.girltest.netty.enum2.EMessageType;
import com.girltest.netty.listener.MyMenuBarActionListener;
import com.girltest.netty.swing.callback.Callback;
import com.string.widget.util.ValueWidget;
import com.swing.component.AssistPopupTextArea;
import com.swing.component.ComponentUtil;
import com.swing.component.SmartScroller;
import com.swing.dialog.GenericFrame;
import com.swing.dialog.toast.ToastMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/***
 * ClientApp 和ServerApp 都继承GenericChatFrame<br />
 * 客户端和服务器端使用同一个界面,唯一的区别就是客户端多显示一个按钮:"链接"<br />
 * 快捷键:Alt+Enter:触发"Enter键发送"复选框的选中和取消选中(toggle)
 */
public abstract class GenericChatFrame extends GenericFrame {
    private static final Logger log = LoggerFactory.getLogger(GenericChatFrame.class);
    protected AssistPopupTextArea chatHistoryTa;
    protected AssistPopupTextArea inputTextArea;
    protected JButton sendButton;
    protected JButton connectButton;
    protected JCheckBox enter2SendCheckBox;
    protected JPanel rootPanel;
    private JScrollPane topScrollPane;
    protected int port = 8088;
    protected Channel channel;
    protected Callback callback = new Callback() {
        //        private int delta = 0;
        @Override
        public String callback(String msg, Object ctx, EMessageType type) {
            ChannelHandlerContext channelHandlerContext = (ChannelHandlerContext) ctx;
            if (null != type) {
                // 统一在"channelRegistered"中设置Channel 句柄
                switch (type) {
                    case channelActive:
                        if (null == getChannel()) {
                            setChannel(channelHandlerContext.channel());
                            System.out.println(getTitle() + " 获取对方socket句柄:" + getChannel());
                        }
                        sendButton.setEnabled(true);
                        break;
                    case channelRegistered:
                        setChannel(channelHandlerContext.channel());
                        break;
                    case messageArrivied:
                        showReceivedMsg(msg);
                        break;
                    case channelUnregistered:
                        System.out.println(getTitle() + " :设置setChannel 为null");
                        setChannel(null);
                        break;
                }

            } else {
                showReceivedMsg(msg);
               /* Container container = chatHistoryTa.getParent().getParent();
                JScrollPane scrollPane = (JScrollPane) container;
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
//                delta++;
                int max = vertical.getMaximum() *//*+ 80*delta*//*;
                vertical.setValue(max);*/

            }
            return null;
        }
    };

    private void showReceivedMsg(String msg) {
        ComponentUtil.appendResult(chatHistoryTa, msg, true);
        chatHistoryTa.updateUI();
        //使窗口处于激活状态
        GenericChatFrame.this.toFront();
        GenericChatFrame.this.requestFocus();
    }

    /***
     * 菜单
     */
    private void setMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileM = new JMenu("File");
        JMenuItem reconnectM = new JMenuItem("重新连接");
        JMenuItem closeM = new JMenuItem("断开连接");
        JMenuItem closeAndShutdownM = new JMenuItem("断开并退出服务器");
        JMenuItem uploadFileM = new JMenuItem("上传文件");

        MyMenuBarActionListener myMenuBarActionListener = new MyMenuBarActionListener(this);
        reconnectM.addActionListener(myMenuBarActionListener);
        closeM.addActionListener(myMenuBarActionListener);
        closeAndShutdownM.addActionListener(myMenuBarActionListener);
        uploadFileM.addActionListener(myMenuBarActionListener);

        fileM.add(reconnectM);
        fileM.add(closeM);
        fileM.add(closeAndShutdownM);
        fileM.add(uploadFileM);
        menuBar.add(fileM);
        setJMenuBar(menuBar);
    }

    /***
     * 在构造方法中,一定要执行launchFrame()<br />
     * 否则界面不显示
     */
    public GenericChatFrame() {
        super();
        launchFrame();//用于启动界面
    }

    /***
     * z主动关闭连接,断开连接
     */
    protected void closeConnection() {
        if (channel == null) {
            ToastMessage.toast("您还未连接", 2000, Color.red);
            return;
        }
        channel.writeAndFlush(new MessageItem().setType(MessageItem.TYPE_DISCONNECT));
    }

    @Override
    public void layout3(Container contentPane) {
        super.layout3(contentPane);
        setContentPane(this.rootPanel);
//        fullScreen();
        layoutAction(this.rootPanel);
        setTitle(getTitle());
    }

    /***
     * 子类的所有操作都放在layoutAction 中
     * @param rootPanel
     */
    protected void layoutAction(JPanel rootPanel) {
//        绑定事件
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //发送消息
                sendMsg(false);
                inputTextArea.requestFocus();
            }
        });

        inputTextArea.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent event) {
                // super.keyPressed(event);
                // System.out.println(event.getKeyCode());
                //注意:监听方法 keyTyped 中getKeyCode 为0,即未知,
                // 所以只能通过getKeyChar 来判断
                if ((event.getKeyCode() == KeyEvent.VK_ENTER || event.getKeyChar() == '\n' || event.getKeyChar() == '\r')
                        ) {
                    if (null == enter2SendCheckBox) {
                        System.exit(0);
                    }
                    if (!(event.isControlDown()
                            || event.isAltDown())) {
                        if (enter2SendCheckBox.isSelected()) {
                            sendMsg(true);
                        }
                    } else if (event.isAltDown()) {
                        System.out.println("isAltDown :");
                        enter2SendCheckBox.setSelected(!enter2SendCheckBox.isSelected());
                    }
                }/*else if(event.getKeyCode()==KeyEvent.VK_ALT){

                }*/

            }
        });

        inputTextArea.placeHolder("这里输入聊天内容,回车即可发送");

        sendButton.setEnabled(false);
        enter2SendCheckBox.setSelected(true);

        chatHistoryTa.setText(SystemHWUtil.CRLF + SystemHWUtil.CRLF);
        chatHistoryTa.placeHolder("这是聊天历史记录,不可编辑");
        DefaultCaret caret = (DefaultCaret) chatHistoryTa.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        setMenu();

        //自动回到底部
        new SmartScroller(topScrollPane);
        //启动
    }

    /***
     * 如果首尾有空格或换行,那么空格或换行会被忽略
     * @param enter
     */
    public void sendMsg(boolean enter) {
        String sendMsg = inputTextArea.getText2();
        if (null == channel) {
            ToastMessage.toast("请先连接 socket服务器", 1000, Color.red);
            inputTextArea.setText(sendMsg.trim());
            return;
        }

        //因为回车发送消息,消息后面会多一个回车'\n'
        if (enter) {
            sendMsg = sendMsg.trim();
        }
        if (ValueWidget.isNullOrEmpty(sendMsg)) {
            return;
        }
        System.out.println(getTitle() + " 发送消息 :" + sendMsg);
        ChannelFuture future = channel.writeAndFlush(BytesMessageItem.getInstance(sendMsg));
        try {
            future.await();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        if (future.isDone()) {
            inputTextArea.setText(SystemHWUtil.EMPTY);
            ComponentUtil.appendResult(chatHistoryTa, "me said:" + sendMsg, true, false);
            chatHistoryTa.updateUI();
        }
    }

    /***
     * 设置JFrame的标题
     * @return
     */
    @Override
    public abstract String getTitle();

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public AssistPopupTextArea getInputTextArea() {
        return inputTextArea;
    }

    public abstract void connect();

}
