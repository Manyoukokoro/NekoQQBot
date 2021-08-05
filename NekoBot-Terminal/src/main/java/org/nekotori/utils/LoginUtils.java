package org.nekotori.utils;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Scanner;

/**
 * @author: JayDeng
 * @date: 05/08/2021 15:32
 * @description:
 * @version: {@link }
 */
public class LoginUtils {

    public static  Long getUserId(){
        Long userId = null;
        while (ObjectUtils.isEmpty(userId)){
            System.out.println("请输入号码");
            String idStr = new Scanner(System.in).nextLine();
            userId = Long.parseLong(idStr);
        }
        return userId;
    }
    public static  String getPassword(){
        String userPwd = null;
        while (StringUtils.isEmpty(userPwd)){
            System.out.println("请输入密码");
            userPwd = new Scanner(System.in).nextLine();
        }
        return userPwd;
    }
}
    