package com.kongtrolink.framework.base;

import java.util.ArrayList;

/**
 * @Auther: liudd
 * @Date: 2019/11/14 09:26
 * @Description:固定大小列表，主要用于判定历史告警表那些包含key索引
 * 考虑到经常判定是否包含元素，所以使用ArrayList
 */
public class FixSizeArrayList<T> extends ArrayList<T>{

    private int capacity;

    public FixSizeArrayList(int capacity){
        super();
        if(capacity <=0){
            capacity = 15;
        }
        this.capacity = capacity;
    }

    @Override
    public boolean add(T t) {
        if(super.size() + 1 > capacity){
            //移除第一个
            super.remove(0);
        }
        return super.add(t);
    }
}
