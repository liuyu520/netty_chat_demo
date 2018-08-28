package com.girltest.netty.handle.bytes;

import com.girltest.netty.dto.ChannelHandleDto;
import com.girltest.netty.dto.message.BytesMessageItem;
import com.girltest.netty.dto.upload.UploadedFileSavePathDto;
import com.girltest.netty.enum2.EServerCmd;
import com.girltest.netty.util.PrintUtil;
import com.io.hw.file.util.FileUtils;
import com.string.widget.util.ValueWidget;
import lombok.Data;

import java.io.File;
import java.io.IOException;

@Data
public class UploadFileSavingHandle implements IBusinessHandle {
    private ChannelHandleDto channelHandleDto;

    @Override
    public void handle(BytesMessageItem msg) {
        dealTransferTlv(msg);
    }

    /***
     * 如果保存失败,请确认 com/girltest/netty/handle/CommonChannelnitializer.java 中 BytesMessageDecoder 第一个参数 maxFrameLength
     * @param msg
     */
    private void dealTransferTlv(BytesMessageItem msg) {
        String dataType = msg.getDataType();
        if (ValueWidget.isNullOrEmpty(dataType)) {
            return;
        }
        switch (dataType) {
            case "pic":
                saveToFile(msg, this.channelHandleDto.getUploadedFileSavePathDto());
                break;
        }

    }

    /***
     * 保存字节数组到本地文件 <br />
     * 如果保存失败,请确认 com/girltest/netty/handle/CommonChannelnitializer.java 中 BytesMessageDecoder 第一个参数 maxFrameLength
     * @param msg
     */
    private static void saveToFileThrowException(BytesMessageItem msg, UploadedFileSavePathDto uploadedFileSavePathDto) throws IOException {
        byte[] bytesData = msg.getBinaryDataNoLength();
        //下面的方法会阻塞,等待用户输入
        String filePath = getInputPath(uploadedFileSavePathDto);
        //path:xxxcancel 表示取消保存
        if (filePath.endsWith(EServerCmd.GET_SAVED_FILE_CANCEL.getDisplayName())) {
            PrintUtil.print("取消保存");
            return;
        }
        PrintUtil.print("获得用户输入路径:" + filePath);
        uploadedFileSavePathDto.setSavedPath(null);

        // 判断用户输入的文件是否已经存在
        if (new File(filePath).exists()) {
            //最终文件名称:/tmp/uploaded/cc32c_bak.jpg
            File savedFile = FileUtils.modifyFilePath(filePath, "bak");
            PrintUtil.print("文件已经存在,所以系统自动更名为:" + savedFile);
            FileUtils.writeBytesToFile(bytesData, savedFile);
        } else {
            FileUtils.writeBytesToFile(bytesData, filePath);
        }

        PrintUtil.print("保存成功,文件大小:" + FileUtils.formatFileSize2(bytesData.length));

    }

    private static void saveToFile(BytesMessageItem msg, UploadedFileSavePathDto uploadedFileSavePathDto) {
        try {
            saveToFileThrowException(msg, uploadedFileSavePathDto);
        } catch (IOException e) {
//            e.printStackTrace();
            PrintUtil.print(e.getMessage());
            saveToFile(msg, uploadedFileSavePathDto);
        }
    }


    /***
     * 阻塞方法 ,等待用户输入
     * @param uploadedFileSavePathDto
     * @return
     */
    private static String getInputPath(UploadedFileSavePathDto uploadedFileSavePathDto) {
        PrintUtil.print("请输入保存路径 :");
        //死循环,等待用户输入
        while (ValueWidget.isNullOrEmpty(uploadedFileSavePathDto.getSavedPath())) {
            //等待用户输入保存路径
            //格式:path:/tmp/uploaded/cc32c.jpg
        }
        return uploadedFileSavePathDto.getSavedPath();
    }

    @Override
    public void setChannelHandleDto(ChannelHandleDto channelHandleDto) {
        this.channelHandleDto = channelHandleDto;
    }
}
