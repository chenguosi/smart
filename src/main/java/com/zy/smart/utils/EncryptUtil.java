package com.zy.smart.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

import com.zy.smart.domain.system.Tuser;

/**
 * 密码加密工具
 */
public class EncryptUtil {
    
    // 加密次数
    private static final int HASHITERATIONS = 1024;
    
    // 加密方式
    private static final String HASHALGORITHMNAME = "MD5";
    
    public static void encryptData(Tuser user) {
        String credentials = user.getPassword();
        ByteSource credentialsSalt = ByteSource.Util.bytes(user.getUserName());
        Object obj = new SimpleHash(HASHALGORITHMNAME, credentials, credentialsSalt, HASHITERATIONS);
        user.setPassword(obj.toString());
    }
}
