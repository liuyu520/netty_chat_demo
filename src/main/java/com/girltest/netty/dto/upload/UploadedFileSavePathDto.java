package com.girltest.netty.dto.upload;

public class UploadedFileSavePathDto {
    private String savedPath;

    public static UploadedFileSavePathDto getInstance() {
        return new UploadedFileSavePathDto();
    }

    public synchronized String getSavedPath() {
        return savedPath;
    }

    public synchronized void setSavedPath(String savedPath) {
        this.savedPath = savedPath;
    }
}
