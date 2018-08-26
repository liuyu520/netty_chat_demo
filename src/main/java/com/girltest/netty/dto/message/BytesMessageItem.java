package com.girltest.netty.dto.message;

import com.common.util.SystemHWUtil;
import lombok.Data;

import java.io.UnsupportedEncodingException;

@Data
public class BytesMessageItem extends MessageItem {

    private byte[] binaryDataNoLength;
    /**
     * binaryDataNoLength 的长度
     */
    private long length2;
    /***
     * "pic",""
     */
    private String dataType;

    public static BytesMessageItem getInstance(String data) {
        BytesMessageItem messageItem = new BytesMessageItem();
        try {
            messageItem.setBinaryDataNoLength(data.getBytes(SystemHWUtil.CHARSET_UTF));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (null != messageItem.getBinaryDataNoLength()) {
            messageItem.setLength2(messageItem.getBinaryDataNoLength().length);
        }
        return messageItem;
    }
}
