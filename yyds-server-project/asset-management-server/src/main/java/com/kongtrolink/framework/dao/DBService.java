package com.kongtrolink.framework.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.DBResult;
import org.springframework.stereotype.Service;

@Service
public interface DBService {

    DBResult addCIType(JSONObject jsonObject);

    DBResult deleteCIType(String name);

    DBResult modifyCIType(JSONObject jsonObject);

    DBResult modifyCITypeIcon(String name, String icon);

    DBResult searchCIType(JSONObject jsonObject);

    DBResult searchCITypeByName(String name);

    DBResult bindCITypeBusinessCode(JSONObject jsonObject);

    DBResult unbindCITypeBusinessCode(JSONObject jsonObject);

    DBResult addCIConnectionType(JSONObject jsonObject);

    DBResult searchCIConnectionType();

    DBResult addCITypeConnectionRelationship(JSONObject jsonObject);

    DBResult deleteCITypeConnectionRelationship(JSONObject jsonObject);

    DBResult searchCITypeConnectionRelationship(JSONObject jsonObject);

    DBResult addCIProp(JSONObject jsonObject);

    DBResult deleteCIProp(JSONObject jsonObject);

    DBResult modifyCIProp(JSONObject jsonObject);

    DBResult searchCIProp(JSONObject jsonObject);

    DBResult addCI(JSONObject jsonObject);

    DBResult deleteCI(JSONObject jsonObject);

    DBResult modifyCI(JSONObject jsonObject);

    DBResult searchCI(JSONObject jsonObject);

    DBResult addCIRelationship(JSONObject jsonObject);

    DBResult searchCIRelationship(JSONObject jsonObject);

    DBResult deleteCIRelationship(JSONObject jsonObject);

    DBResult searchCIModel(JSONObject jsonObject);

    DBResult searchCIIds(JSONObject jsonObject);
}
