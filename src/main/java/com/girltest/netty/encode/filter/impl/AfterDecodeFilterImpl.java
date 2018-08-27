package com.girltest.netty.encode.filter.impl;

import com.common.util.SystemHWUtil;
import com.girltest.netty.encode.filter.IAfterDecodeFilter;

public class AfterDecodeFilterImpl implements IAfterDecodeFilter {
    @Override
    public byte[] afterDecode(byte[] bytes) {
        try {
            bytes = SystemHWUtil.decryptDES(bytes, "111ac111".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
