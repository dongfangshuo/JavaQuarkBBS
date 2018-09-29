package com.quark.porent.config;

import com.quark.common.base.AliOssClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * @Author LHR
 * Create By 2017/8/22
 */
@Configuration
@PropertySource("classpath:resource.properties")
public class PropertiesConfig {
    @Value("${oss.string.bucket}")
    private String stringBucket;
    @Value("${oss.image.bucket}")
    private String imageBucket;
    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${oss.endpoint}")
    private String endpoint;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Bean
    public AliOssClient aliOssClient(){
        return new AliOssClient(imageBucket,stringBucket,accessKeyId,accessKeySecret,endpoint);
    }
}
