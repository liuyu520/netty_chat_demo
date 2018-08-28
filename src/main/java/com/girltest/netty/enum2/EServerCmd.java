package com.girltest.netty.enum2;

import com.common.enu.IEnum;

/***
 *  服务器端的命令 <br />
 *
 */
public enum EServerCmd implements IEnum {

    RE_CONNECT(1, "reconnect"),
    GET_SAVED_FILE(2, "path")/* path:/tmp/uploaded/cc32c.jpg */,;
    private Integer code;

    private String displayName;

    EServerCmd(Integer code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }
}
