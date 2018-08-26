package com.girltest.netty.encode.filter.impl;

import com.girltest.netty.encode.filter.IBeforeEncodeFilter;

public class BeforeEncodeFilterImpl implements IBeforeEncodeFilter {
    @Override
    public byte[] beforeEncode(byte[] bytes) {
        return bytes;
    }
}
