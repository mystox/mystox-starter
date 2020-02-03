package com.kongtrolink.framework.gateway.tower.core.entity;

/**
 * xx
 * by Mag on 2018/9/13.
 */
public class RandomKey {
    private String key;
    private long num;

    public RandomKey(String key, long num) {
        this.key = key;
        this.num = num;
    }

    public RandomKey() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "SxEn{" +
                "key='" + key + '\'' +
                ", num=" + num +
                '}';
    }
}
