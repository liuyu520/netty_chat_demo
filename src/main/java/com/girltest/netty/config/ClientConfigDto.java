package com.girltest.netty.config;

import lombok.Data;

@Data
public class ClientConfigDto {
    /***
     * "/Users/whuanghkl/a11.txt.jpg"->"/Users/whuanghkl/a11.txt"
     */
    private boolean isForceIntegryFileSuffix;
    private boolean needConfirmDialogWhenClose;
    private String defaultSavedFilePath;
    private Integer port;
    private Long length;
}
