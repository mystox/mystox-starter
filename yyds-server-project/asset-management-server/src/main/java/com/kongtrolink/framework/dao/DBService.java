package com.kongtrolink.framework.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

@Service
public interface DBService {

    boolean addCIType(JSONObject jsonObject);

    boolean deleteCIType(String name);

    boolean modifyCIType(JSONObject jsonObject);

    JSONArray searchCIType(JSONObject jsonObject);

    JSONArray searchCITypeByName(String name);

    boolean bindCITypeBusinessCode(JSONObject jsonObject);

    boolean unbindCITypeBusinessCode(JSONObject jsonObject);

    boolean addCIConnectionType(JSONObject jsonObject);

    JSONArray searchCIConnectionType();

    boolean addCITypeConnectionRelationship(JSONObject jsonObject);

    boolean deleteCITypeConnectionRelationship(JSONObject jsonObject);

    JSONArray searchCITypeConnectionRelationship(JSONObject jsonObject);

    boolean addCIProp(JSONObject jsonObject);

    boolean deleteCIProp(JSONObject jsonObject);

    boolean modifyCIProp(JSONObject jsonObject);

    JSONObject searchCIProp(JSONObject jsonObject);

    String addCI(JSONObject jsonObject);

    boolean deleteCI(JSONObject jsonObject);

    boolean modifyCI(JSONObject jsonObject);

    JSONObject searchCI(JSONObject jsonObject);

    boolean addCIRelationship(JSONObject jsonObject);

    JSONObject searchCIRelationship(JSONObject jsonObject);

    boolean deleteCIRelationship(JSONObject jsonObject);

    JSONArray searchCIModel(JSONObject jsonObject);

    JSONArray searchCIIds(JSONObject jsonObject);
}
