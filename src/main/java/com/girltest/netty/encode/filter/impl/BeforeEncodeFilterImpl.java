package com.girltest.netty.encode.filter.impl;

import com.common.util.SystemHWUtil;
import com.girltest.netty.encode.filter.IBeforeEncodeFilter;

public class BeforeEncodeFilterImpl implements IBeforeEncodeFilter {
    @Override
    public byte[] beforeEncode(byte[] bytes) {
        try {
            bytes = SystemHWUtil.encryptDES(bytes, "111ac111".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
