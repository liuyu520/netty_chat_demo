package com.girltest.netty.encode.filter.impl;

import com.common.util.SystemHWUtil;
import com.girltest.netty.encode.filter.IAfterDecodeFilter;

public class AfterDecodeFilterImpl implements IAfterDecodeFilter {
    @Override
    public byte[] afterDecode(byte[] bytes) {
        return SystemHWUtil.mergeArray(bytes, new byte[]{97, 98});
    }
}
