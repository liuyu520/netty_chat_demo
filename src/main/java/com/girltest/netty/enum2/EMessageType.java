package com.girltest.netty.enum2;

public enum EMessageType {
    handlerAdded(1), channelRegistered(2), channelActive(4), messageArrivied(6), channelInactive(8), channelUnregistered(10), TYPE_DISCONNECT(12), closeServerApp(16);
    private int type;

    EMessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
