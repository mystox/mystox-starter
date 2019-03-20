package com.kongtrolink.yyjw.model;

import java.io.Serializable;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/12 9:39
 * \* Description:
 * \
 */
public class User implements Serializable{
    private String username;
    private String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

