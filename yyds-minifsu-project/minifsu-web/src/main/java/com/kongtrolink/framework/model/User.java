package com.kongtrolink.framework.model;

import java.io.Serializable;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/12 9:39
 * \* Description:
 * \
 */
public class User implements Serializable{
    private String id;
    private String username;
    private String password;

    private String currentRoleName;


    public String getCurrentRoleName() {
        return currentRoleName;
    }

    public void setCurrentRoleName(String currentRoleName) {
        this.currentRoleName = currentRoleName;
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

