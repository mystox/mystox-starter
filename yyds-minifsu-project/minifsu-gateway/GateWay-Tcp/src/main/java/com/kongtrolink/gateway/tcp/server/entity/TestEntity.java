package com.kongtrolink.gateway.tcp.server.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2019/3/19.
 */
public class TestEntity implements Serializable {
    private static final long serialVersionUID = -8947181975013980694L;

    private String name;
    private int age;

    public TestEntity() {
    }

    public TestEntity(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
