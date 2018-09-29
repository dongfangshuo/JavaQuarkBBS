package com.quark.common.base;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * oss 工具类
 *
 * 
 */
public class AliOssClient {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private  String imgBucketName = null;
    private  String strBucketName = null;
    //
    private OSSClient instance = null;
    
    public AliOssClient(String imgBucketName, String strBucketName, String accessKeyId,String accessKeySecret,  String _endpoint){
        this.imgBucketName = imgBucketName;
        this.strBucketName = strBucketName;
        ClientConfiguration conf = new ClientConfiguration();
        conf.setMaxConnections(2048);
        instance = new OSSClient(_endpoint, accessKeyId, accessKeySecret,conf);
    }


    private OSSClient getOSSClient() {
        return instance;
    }

    /**
     * 接受图片流并保存到oss，最好封装一层服务来调用<br>
     * 名字前加上自己业务的前缀比如用户加“user/”<br>
     * 自己保证名字的唯一性和后缀的正确性<br>
     * @param content
     * @param picName 图片在oss存储的key值
     * @throws IOException
     */
    public boolean putImage(InputStream content, String picName) {
        try {
            if (StringUtils.isBlank(picName) || content == null || content.available() < 10 ) {
                return false;
            }
            OSSClient client = getOSSClient();
            ObjectMetadata meta = new ObjectMetadata();
            // 必须设置ContentLength
            meta.setContentLength(content.available());
            // 上传Object.
            client.putObject(imgBucketName, picName, content, meta);
            
            return true;
        } catch (Exception e) {
            logger.error("oss放图片异常", e);
            return false;
        } finally {
            if (content != null) {
                try {
                    content.close();
                } catch (IOException e) {
                    logger.error("关闭图片流异常", e);
                }
            }
        }
    }
    



    public void downloadImage(String key,File targetFile){
        if(StringUtils.isBlank(key) || targetFile == null){
            throw new IllegalArgumentException();
        }

        OSSClient ossClient = getOSSClient();
        ossClient.getObject(new GetObjectRequest(imgBucketName,key),targetFile);
    }
    
    public void downloadImage(String key,String style,File targetFile){
        if(StringUtils.isBlank(key) || targetFile == null){
            throw new IllegalArgumentException();
        }
        OSSClient ossClient = getOSSClient();
        GetObjectRequest request = new GetObjectRequest(imgBucketName,key);
        request.setProcess(style);
        ossClient.getObject(request,targetFile);
    }

    /**
     * 将String类型数据保存到OSS
     * 
     * @param content
     * @param key 描述内容在oss的key值
     * @throws IOException
     */
    public  boolean putString(String content, String key){
        if (StringUtils.isBlank(key) || content == null) {
            return false;
        }
        ByteArrayInputStream stringInputStream = null;
        try {
            OSSClient client = getOSSClient();
            ObjectMetadata meta = new ObjectMetadata();
            // 上传Object.
            stringInputStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
            // 必须设置ContentLength
            meta.setContentLength(stringInputStream.available());
            client.putObject(strBucketName, key, stringInputStream, meta);
            return true;
        } catch (Exception e) {
            logger.error("oss放字符串信息异常", e);
            return false;
        } finally {
            if (stringInputStream != null) {
                try {
                    stringInputStream.close();
                } catch (IOException e) {
                    logger.error("关闭字符流异常", e);
                }
            }
        }
    }

    /**
     * 根据key返回在OSS中的内容
     * 
     * @param key
     * @return
     * @throws IOException
     */
    public String getString(String key)  {
        String result = null;
        InputStream objectContent = null;
        try {
            OSSClient client = getOSSClient();
            OSSObject object = client.getObject(strBucketName, key);
            objectContent = object.getObjectContent();
            result = IOUtils.toString(objectContent, "UTF-8");
            return result;
        } catch (Exception e) {
            logger.error("oss获取字符串信息异常,key:"+key, e);
            return result;
        } finally {
            if (objectContent != null) {
                try {
                    objectContent.close();
                } catch (IOException e) {
                    logger.error("关闭输入流异常", e);
                }
            }
        }
    }
    

    /**
     * 根据key删除oss上的图片
     * 
     * @param key
     */
    public boolean deleteImg(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        try {
            OSSClient client = getOSSClient();
            client.deleteObject(imgBucketName, key);
            return true;
        } catch (Exception e) {
            logger.error("删除oss信息异常" + key, e);
            return false;
        }
    }

    /**
     * 图片重命名
     * @param oldName
     * @param newName
     */
    public boolean reNameImg(String oldName, String newName) {
        if (StringUtils.isBlank(oldName) || StringUtils.isBlank(newName)) {
            return false;
        }
        InputStream inputStream = null;
        try {
            OSSClient client = getOSSClient();
            if (StringUtils.equals(oldName, newName)) {
                OSSObject ossObject = client.getObject(imgBucketName, oldName);
                inputStream = ossObject.getObjectContent();
                return true;
            }
            client.copyObject(imgBucketName, oldName, imgBucketName, newName);
            client.deleteObject(imgBucketName, oldName);
            return true;
        } catch (Exception e) {
            logger.error("oss图片操作异常" , e);
            return false;
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("关闭输入流异常", e);
                }
            }
        }
      
    }
    
    public boolean copyObject(String oldName, String newName, String oldBucket, String newBucket) {
        if (StringUtils.isBlank(oldName) || StringUtils.isBlank(newName) || StringUtils.isBlank(oldBucket)
                || StringUtils.isBlank(newBucket)) {
            return false;
        }
        InputStream inputStream = null;
        try {
            OSSClient client = getOSSClient();
            if (StringUtils.equals(oldName, newName) && StringUtils.equals(oldBucket, newBucket)) {
                OSSObject object =      client.getObject(oldBucket, oldName);
                inputStream = object.getObjectContent();
                return true;
            }
            client.copyObject(oldBucket, oldName, newBucket, newName);
            return true;
        } catch (Exception e) {
            logger.error("oss复制图片操作异常", e);
            return false;
        }finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("关闭输入流异常", e);
                }
            }
        }
    }

    public boolean createBucket(String bucketName) {
        try {
            OSSClient client = getOSSClient();
            // 新建一个Bucket
            client.createBucket(bucketName);
            client.setBucketAcl(bucketName, CannedAccessControlList.Private);
            return true;
        } catch (Exception e) {
            logger.error("oss创建bucket异常", e);
            return false;
        }
    }


    

    public static void main(String[] args) throws FileNotFoundException {
    }

}
