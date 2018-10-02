package com.quark.porent.controller;

import com.quark.common.base.AliOssClient;
import com.quark.common.dto.QuarkResult;
import com.quark.common.dto.UploadResult;
import com.quark.common.entity.User;
import com.quark.common.exception.ServiceProcessException;
import com.quark.porent.interceptor.SubjectHolder;
import com.quark.porent.service.UserService;
import com.quark.porent.utils.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.Subject;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import static com.quark.porent.utils.Constants.OSS_IMAGE_STYLE_DEFAULT;
import static com.quark.porent.utils.Constants.OSS_IMAGE_STYLE_ICON;

/**
 * @Author LHR
 * Create By 2017/8/26
 */
@Api(value = "文件上传接口",description = "图片上传")
@RestController
@RequestMapping("/upload")
public class UploadController {
    @Value("${image.host}")
    private String imgHost;

    @Autowired
    private UserService userService;
    @Autowired
    private AliOssClient aliOssClient;

    @ApiOperation("图片上传接口")
    @PostMapping("/api/image")
    public QuarkResult upload(@RequestParam("file") MultipartFile file) {
        QuarkResult result = null;
        if (!file.isEmpty()) {
            try {
                User user = SubjectHolder.get();
                String key = String.format("edit/%s/%s/%s",user.getId(), DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()),UUID.randomUUID().toString());
                String s = FileUtils.uploadFile(key,file,aliOssClient);
                result = QuarkResult.ok(new UploadResult.Data(imgHost+"/"+s+OSS_IMAGE_STYLE_DEFAULT)) ;
                return result;
            } catch (IOException e) {
                e.printStackTrace();
                result = QuarkResult.error("上传图片失败");
            }
            return result;
        }
        result = QuarkResult.error("文件不存在");
        return result;
    }

    @ApiOperation("用户头像上传接口")
    @PostMapping("/api/usericon/{token}")
    public QuarkResult iconUpload(@PathVariable("token") String token,@RequestParam("file") MultipartFile file){
        QuarkResult result = null;
        if (!file.isEmpty()) {
            try {
                User user = SubjectHolder.get();
                String key = String.format("usericon/%s/%s/%s",user.getId(), DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now()),UUID.randomUUID().toString());
                String s = FileUtils.uploadFile(key,file,aliOssClient);
                s = imgHost +"/"+ s+OSS_IMAGE_STYLE_ICON;
                userService.updateUserIcon(user.getId(),s);
                user.setIcon(s);
                return QuarkResult.ok(new UploadResult.Data(s));

            } catch (IOException e) {
                e.printStackTrace();
                return QuarkResult.error("上传图片失败");
            }catch (ServiceProcessException e1){
                e1.printStackTrace();
                return QuarkResult.error(e1.getMessage());
            }
        }
        return QuarkResult.error("文件不存在");
    }



}
