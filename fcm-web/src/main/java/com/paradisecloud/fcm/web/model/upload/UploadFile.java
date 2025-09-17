package com.paradisecloud.fcm.web.model.upload;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author nj
 * @date 2022/11/2 17:20
 */
public class UploadFile {


    private Long id;
    private MultipartFile uploadFile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MultipartFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(MultipartFile uploadFile) {
        this.uploadFile = uploadFile;
    }
}
