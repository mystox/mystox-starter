/** *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ****************************************************** */
package com.kongtrolink.framework.scloud.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author chendg
 */
public enum StationType {
    
    A(1, "A类机房"), B(2, "B类机房"), C(3, "C类机房"), D(4, "D类机房");
    
    private final int key;
    private final String value;

    private StationType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
    
    public static String toValue(int key) {
        for (StationType item : StationType.values()) {
            if (item.key == key) {
                return item.value;
            }
        }
        return null;
    }
    
    public static List<Map<String, Object>> toList(){
        List<Map<String, Object>> list = new ArrayList<>();
        for (StationType item : StationType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("key", item.key);
            map.put("value", item.value);
            list.add(map);
        }
        return list;
    }

}