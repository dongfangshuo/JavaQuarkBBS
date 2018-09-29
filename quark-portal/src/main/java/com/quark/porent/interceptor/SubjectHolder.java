package com.quark.porent.interceptor;

import com.quark.common.entity.User;

/**
 * Created by gaopengju on 2018/9/16.
 */
public final class SubjectHolder {
    private static ThreadLocal<User> threadLocal = new ThreadLocal();

    public static User get(){
        return threadLocal.get();
    }

    public static void put(User user){
        threadLocal.set(user);
    }

}
