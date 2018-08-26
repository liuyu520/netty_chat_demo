package com.girltest.netty.dto.message;

/***
 * 消息单元
 */
public class MessageItem {
    public static final byte TYPE_HEARTBEAT = 0x07;

    public static final byte TYPE_AUTH = 0x01;

    public static final byte TYPE_ACK = 0x02;

    public static final byte TYPE_CONNECT = 0x03;

    public static final byte TYPE_DISCONNECT = 0x04;

    public static final byte TYPE_TRANSFER = 0x05;

    public static final byte TYPE_WRITE_CONTROL = 0x06;
    /***
     * 传递复杂数据
     */
    public static final byte TYPE_TRANSFER_TLV = 0x08;
    /***
     * 改变"127.0.0.1:8081"<br />
     * 报文:{"5080":"127.0.0.1:81"}
     */
    public static final byte TYPE_CHANGE_LAN_PORT = 0x09;
    /***
     * 获取服务器端全部的端口映射关系
     */
    public static final byte TYPE_fetch_PortLanInfoMapping = 0x0a;
    /***
     * 执行操作系统本地命令<br />
     * java 使用Process
     */
    public static final byte TYPE_OS_CMD = 0x0b;
    /***
     * 纯提示信息,用于排查分析错误原因<br />
     * 服务器端向代理客户端发送消息
     */
    public static final byte TYPE_TIPS_WARNING = 0x0c;
    /**
     * 关闭server的java swing应用程序
     */
    public static final byte TYPE_EXIT_SERVER = 0x0e;

    /***
     * x消息的类型:0x04:断开
     */
    protected byte type;

    private String data;

    public static MessageItem getInstance(String data) {
        MessageItem messageItem = new MessageItem();
        messageItem.setData(data);
        return messageItem;
    }

    public byte getType() {
        return type;
    }

    public MessageItem setType(byte type) {
        this.type = type;
        return this;
    }

    public String getData() {
        return data;
    }

    public MessageItem setData(String data) {
        this.data = data;
        return this;
    }
}
