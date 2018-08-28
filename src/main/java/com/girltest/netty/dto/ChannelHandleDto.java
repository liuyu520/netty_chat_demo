package com.girltest.netty.dto;

import com.girltest.netty.dto.upload.UploadedFileSavePathDto;
import com.girltest.netty.swing.callback.Callback;
import lombok.Data;

@Data
public class ChannelHandleDto {
    private Callback callback;
    private String title;
    private UploadedFileSavePathDto uploadedFileSavePathDto;

}
