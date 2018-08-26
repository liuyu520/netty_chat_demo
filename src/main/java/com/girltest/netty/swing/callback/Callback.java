package com.girltest.netty.swing.callback;

import com.girltest.netty.enum2.EMessageType;

public interface Callback {
    String callback(String msg, Object ctx, EMessageType type);
}
