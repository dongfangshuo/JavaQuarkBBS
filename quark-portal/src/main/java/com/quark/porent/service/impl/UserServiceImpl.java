package com.quark.porent.service.impl;

import com.quark.common.base.BaseServiceImpl;
import com.quark.common.dao.UserDao;
import com.quark.common.entity.User;
import com.quark.common.exception.ServiceProcessException;
import com.quark.porent.entity.QuarkResult;
import com.quark.porent.service.RedisService;
import com.quark.porent.service.UserService;
import com.quark.porent.utils.HttpClientUtils;
import com.quark.porent.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.UUID;

import static com.quark.porent.utils.Constants.OSS_IMAGE_STYLE_DEFAULT;
import static com.quark.porent.utils.Constants.OSS_IMAGE_STYLE_ICON;

/**
 * @Author LHR
 * Create By 2017/8/24
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<UserDao, User>  implements UserService {


    @Autowired
    private RedisService<Integer> redisSocketService;

    @Autowired
    private RedisService<User> redisService;

    @Value("${REDIS_USERID_KEY}")
    private String REDIS_USERID_KEY;

    @Value("${REDIS_USER_KEY}")
    private String REDIS_USER_KEY;

    @Value("${REDIS_USER_TIME}")
    private Integer REDIS_USER_TIME;



    @Override
    public boolean checkUserName(String username) {
        User user = repository.findByUsername(username);
        if (user == null) return true;
        return false;
    }

    @Override
    public boolean checkUserEmail(String email) {
        User user = repository.findByEmail(email);
        if (user == null) return true;
        return false;
    }

    @Override
    public User findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public void createUser(String email, String username, String password) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setInitTime(new Date());
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setIcon("http://img.eastshuo.com/usericon/74/20180930/69d7d04a-c068-423a-8f2d-e9db09a606c2.jpeg?"+OSS_IMAGE_STYLE_ICON);
        repository.save(user);
    }

    @Override
    public String LoginUser(User user) {
        String token = UUID.randomUUID().toString();
        redisService.cacheString(REDIS_USER_KEY + token, user, REDIS_USER_TIME);
//        redisSocketService.cacheSet(REDIS_USERID_KEY,user.getId());
//        loginId.add(user.getId());//维护一个登录用户的set
        return token;
    }

    @Override
    public User getUserByToken(String token) {
        User user = redisService.getStringAndUpDate(REDIS_USER_KEY + token, REDIS_USER_TIME);
        return user;
    }


    @Override
    public String updateUser(Integer id, String username, String signature, Integer sex) {
        User user = repository.findOne(id);
        user.setUsername(username);
        user.setSex(sex);
        user.setSignature(signature);
        repository.save(user);
        return LoginUser(user);
    }

    @Override
    public String updateUserIcon(Integer id, String icon) {
        User user = repository.findOne(id);
        user.setIcon(icon);
        repository.save(user);
        return LoginUser(user);
    }


    @Override
    public void updateUserPassword(Integer id, String oldpsd, String newpsd) {
        User user = repository.findOne(id);
        if(!user.getPassword().equals(DigestUtils.md5DigestAsHex(oldpsd.getBytes())))
            throw new ServiceProcessException("原始密码错误,请重新输入");
        user.setPassword(DigestUtils.md5DigestAsHex(newpsd.getBytes()));
        repository.save(user);
    }

    @Override
    public User getByPK(Integer id) {
        return  repository.findOne(id);
    }

}
