package com.girltest.netty.dto;

import com.girltest.netty.dto.upload.UploadedFileSavePathDto;
import com.girltest.netty.swing.callback.Callback;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChannelHandleDto {
    private Callback callback;
    private String title;
    private UploadedFileSavePathDto uploadedFileSavePathDto;

    public ChannelHandleDto(Callback callback) {
        this.callback = callback;
    }
}
