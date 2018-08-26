package com.girltest.netty.encode.filter;

public interface IBeforeEncodeFilter {
    byte[] beforeEncode(byte[] bytes);
}
