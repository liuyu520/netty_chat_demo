package com.girltest.netty.encode.filter;

public interface IAfterDecodeFilter {
    byte[] afterDecode(byte[] bytes);
}
