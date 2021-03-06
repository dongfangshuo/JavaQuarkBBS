package com.quark.porent.utils;

import com.quark.common.base.AliOssClient;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @Author LHR
 * Create By 2017/8/26
 */
public class FileUtils {

    public static String uploadFile(String key,MultipartFile file,AliOssClient aliOssClient) throws IOException {
        if (!file.isEmpty()) {
            String type = file.getContentType();
            String suffix = "." + type.split("/")[1];
            String fileName = key+suffix;
            try(InputStream inputStream = file.getInputStream()){
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                IOUtils.copy(inputStream,arrayOutputStream);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOutputStream.toByteArray());
                aliOssClient.putImage(byteArrayInputStream,fileName);
                IOUtils.closeQuietly(arrayOutputStream);
                IOUtils.closeQuietly(byteArrayInputStream);
            }
            return fileName;
        }
        return null;
    }
}
