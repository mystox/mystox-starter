package com.kongtrolink.framework.dao.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.CICorrespondenceType;
import com.kongtrolink.framework.entity.Neo4jDBNodeType;
import com.kongtrolink.framework.entity.Neo4jDBRelationshipType;
import com.kongtrolink.framework.dao.DBService;
import com.kongtrolink.framework.utils.Neo4jUtils;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("Neo4jDBService")
public class Neo4jDBService implements DBService {

    private Logger logger = LoggerFactory.getLogger(Neo4jDBService.class);

    @Value("${server.name}_${server.version}")
    private String serverCode;

    @Value("${neo4j.username}")
    private String username;

    @Value("${neo4j.password}")
    private String password;

    @Value("${neo4j.uri}")
    private String uri;

    private Driver driver;

    /**
     * 添加CI类型及对应关联关系
     * @param jsonObject CI类型信息
     * @return 添加结果
     */
    @Override
    public boolean addCIType(JSONObject jsonObject) {
        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {
                    String title = jsonObject.getString("title");
                    String name = jsonObject.getString("name");
                    String code = jsonObject.getString("code");
                    int level = jsonObject.getInteger("level");
                    String relationship = "";

                    StatementResult statementResult;
                    ResultSummary summary;

                    if (level > 1) {
                        relationship = jsonObject.getString("relationship");
                        String cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:{Level}}) return item";
                        statementResult = transaction.run(cmd, Values.parameters("Name", relationship, "Level", level - 1));
                        if (statementResult.list().size() != 1) {
                            transaction.failure();
                            return result;
                        }
                    }

                    String cmd = "create (item:" + Neo4jDBNodeType.CIType + " {title:{Title}, name:{Name}, code:{Code}, level:{Level}, businessCodes:[]})";
                    statementResult = transaction.run(cmd, Values.parameters("Title", title, "Name", name, "Code", code, "Level", level));
                    summary = statementResult.summary();
                    if (summary.counters().nodesCreated() != 1) {
                        transaction.failure();
                        return result;
                    }

                    if (level > 1) {

                        cmd = "match (item1:" + Neo4jDBNodeType.CIType + " {name:{Name1}}), " +
                                "(item2:" + Neo4jDBNodeType.CIType + " {name:{Name2}, level:{Level}}) " +
                                "where not (item2)-[:" + Neo4jDBRelationshipType.INCLUDE + "]->(item1) " +
                                "create (item2)-[:" + Neo4jDBRelationshipType.INCLUDE + "]->(item1)";
                        statementResult = transaction.run(cmd, Values.parameters("Name1", name, "Name2", relationship, "Level", level - 1));
                        summary = statementResult.summary();
                        if (summary.counters().relationshipsCreated() != 1) {
                            transaction.failure();
                            return result;
                        }
                    }

                    transaction.success();
                    result = true;
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 删除CI类型
     * @param name 待删除的CI类型名称
     * @return 删除结果
     */
    @Override
    public boolean deleteCIType(String name) {

        boolean result = false;

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}}) " +
                            "where not (item)-[:" + Neo4jDBRelationshipType.INCLUDE + "]->() DETACH DELETE item";
                    StatementResult statementResult = transaction.run(cmd, Values.parameters("Name", name));

                    ResultSummary summary = statementResult.summary();

                    result = (summary.counters().nodesDeleted() == 1);

                    if (result) {
                        transaction.success();
                    } else {
                        transaction.failure();
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 修改CI类型
     * @param jsonObject 待修改的CI类型信息
     * @return 修改结果
     */
    @Override
    public boolean modifyCIType(JSONObject jsonObject) {
        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {
                    String name = jsonObject.getString("name");
                    String title = jsonObject.getString("title");

                    String cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}}) " +
                            "set item.title = {Title}";
                    StatementResult statementResult = transaction.run(cmd, Values.parameters("Name", name, "Title", title));
                    ResultSummary summary = statementResult.summary();

                    if (summary.counters().propertiesSet() != 1) {
                        transaction.failure();
                        return result;
                    }

                    transaction.success();
                    result = true;
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 查询CI类型信息
     * @param jsonObject 查询条件
     * @return 查询结果
     */
    @Override
    public JSONArray searchCIType(JSONObject jsonObject) {
        JSONArray result = new JSONArray();

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String title = jsonObject.getString("title");
                    String name = jsonObject.getString("name");
                    String code = jsonObject.getString("code");

                    String searchType = "match (item:" + Neo4jDBNodeType.CIType + ") " +
                            "where item.name =~ '.*" + name + ".*' and item.title =~ '.*" + title + ".*' and " +
                            "item.code =~ '.*" + code + ".*'";
                    if (jsonObject.containsKey("level")) {
                        int level = jsonObject.getInteger("level");
                        searchType += " and item.level = " + level;
                    }
                    String businessCode = "";
                    if (jsonObject.containsKey("enterpriseCode")
                            && jsonObject.containsKey("serverCode")) {
                        String enterpriseCode = jsonObject.getString("enterpriseCode");
                        String serverCode = jsonObject.getString("serverCode");
                        businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);
                    }
                    searchType += " return item";

                    StatementResult statementResult = transaction.run(searchType);
                    List<Record> recordList = statementResult.list();

                    for (int i = 0; i < recordList.size(); ++i) {
                        JSONObject ciType = getCIType(recordList.get(i));

                        if (!businessCode.equals("")) {
                            JSONArray businessCodes = ciType.getJSONArray("businessCodes");
                            boolean enabled = businessCodes.contains(businessCode);

                            ciType.put("enabled", enabled);
                        } else {
                            ciType.put("enabled", true);
                        }

                        JSONArray children = new JSONArray();
                        ciType.put("parent", null);
                        ciType.put("children", children);

                        String searchRelationship = "match (tmp:" + Neo4jDBNodeType.CIType + " {name:'" + ciType.getString("name") + "'})" +
                                "-[:" + Neo4jDBRelationshipType.INCLUDE + "]-(item:" + Neo4jDBNodeType.CIType + ") " +
                                "return item";
                        statementResult = transaction.run(searchRelationship);
                        List<Record> relationshipCITypeList = statementResult.list();
                        for (int j = 0; j < relationshipCITypeList.size(); ++j) {
                            JSONObject relationshipCIType = getCIType(relationshipCITypeList.get(j));
                            if (relationshipCIType.getInteger("level") > ciType.getInteger("level")) {
                                children.add(relationshipCIType);
                            } else {
                                ciType.put("parent", relationshipCIType);
                            }
                        }
                        result.add(ciType);
                    }

                    transaction.success();
                }
            }
        } catch (Exception e) {
            result = null;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 通过类型名称查询CI类型信息
     * @param name 类型名称
     * @return 查询结果
     */
    @Override
    public JSONArray searchCITypeByName(String name) {
        JSONArray result = new JSONArray();

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String searchType = "match (item:" + Neo4jDBNodeType.CIType + ") " +
                            "where item.name = '" + name + "' return item";

                    StatementResult statementResult = transaction.run(searchType);
                    List<Record> recordList = statementResult.list();

                    for (int i = 0; i < recordList.size(); ++i) {
                        JSONObject ciType = getCIType(recordList.get(i));

                        JSONArray children = new JSONArray();
                        ciType.put("parent", null);
                        ciType.put("children", children);

                        String searchRelationship = "match (tmp:" + Neo4jDBNodeType.CIType + " {name:'" + ciType.getString("name") + "'})" +
                                "-[:" + Neo4jDBRelationshipType.INCLUDE + "]-(item:" + Neo4jDBNodeType.CIType + ") " +
                                "return item";
                        statementResult = transaction.run(searchRelationship);
                        List<Record> relationshipCITypeList = statementResult.list();
                        for (int j = 0; j < relationshipCITypeList.size(); ++j) {
                            JSONObject relationshipCIType = getCIType(relationshipCITypeList.get(j));
                            if (relationshipCIType.getInteger("level") > ciType.getInteger("level")) {
                                children.add(relationshipCIType);
                            } else {
                                ciType.put("parent", relationshipCIType);
                            }
                        }
                        result.add(ciType);
                    }

                    transaction.success();
                }
            }
        } catch (Exception e) {
            result = null;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 绑定CI类型与业务编码
     * @param jsonObject 绑定信息
     * @return 绑定结果
     */
    @Override
    public boolean bindCITypeBusinessCode(JSONObject jsonObject) {

        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {
                    String name = jsonObject.getString("name");
                    String enterpriseCode = jsonObject.getString("enterpriseCode");
                    String serverCode = jsonObject.getString("serverCode");
                    String businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);

                    String cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) " +
                            "where {BusinessCode} in item.businessCodes return item";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("Name", name, "BusinessCode", businessCode));
                    List<Record> recordList = statementResult.list();

                    if (recordList.size() == 1) {
                        transaction.success();
                        result = true;
                        return result;
                    }

                    cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) " +
                            "set item.businessCodes = item.businessCodes + {BusinessCode}";
                    statementResult = transaction.run(cmd,
                            Values.parameters("Name", name, "BusinessCode", businessCode));

                    ResultSummary summary = statementResult.summary();

                    if (summary.counters().propertiesSet() != 1) {
                        transaction.failure();
                        return result;
                    }

                    transaction.success();
                    result = true;
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 解绑CI类型与业务编码
     * @param jsonObject 解绑信息
     * @return 解绑结果
     */
    @Override
    public boolean unbindCITypeBusinessCode(JSONObject jsonObject) {
        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {
                    String name = jsonObject.getString("name");
                    String enterpriseCode = jsonObject.getString("enterpriseCode");
                    String serverCode = jsonObject.getString("serverCode");
                    String businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);

                    String cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) return item";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("Name", name));
                    List<Record> recordList = statementResult.list();

                    if (recordList.size() != 1) {
                        transaction.failure();
                        return result;
                    }

                    JSONObject ciType = getCIType(recordList.get(0));
                    JSONArray businessCodes = ciType.getJSONArray("businessCodes");
                    if (businessCodes.contains(businessCode)) {
                        businessCodes.remove(businessCode);

                        cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) " +
                                "set item.businessCodes = {BusinessCodes}";
                        statementResult = transaction.run(cmd,
                                Values.parameters("Name", name, "BusinessCodes", businessCodes));

                        ResultSummary summary = statementResult.summary();

                        if (summary.counters().propertiesSet() != 1) {
                            transaction.failure();
                            return result;
                        }
                    }

                    transaction.success();
                    result = true;
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 新增CI连接关系
     * @param jsonObject CI连接关系信息
     * @return 新增结果
     */
    @Override
    public boolean addCIConnectionType(JSONObject jsonObject) {

        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {
                    String title = jsonObject.getString("title");
                    String name = jsonObject.getString("name");
                    boolean ascription = jsonObject.getBoolean("ascription");

                    String cmd = "create (item:" + Neo4jDBNodeType.CIConnectionType + " {title:{Title}, name:{Name}, ascription:{Ascription}})";
                    StatementResult statementResult = transaction.run(cmd, Values.parameters("Title", title, "Name", name, "Ascription", ascription));
                    ResultSummary summary = statementResult.summary();
                    if (summary.counters().nodesCreated() == 1) {
                        transaction.success();
                        result = true;
                    } else {
                        transaction.failure();
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 查询所有CI连接关系
     * @return 查询结果
     */
    @Override
    public JSONArray searchCIConnectionType() {

        JSONArray result = new JSONArray();

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String cmd = "match (item:" + Neo4jDBNodeType.CIConnectionType + ") return item";

                    StatementResult statementResult = transaction.run(cmd);
                    List<Record> recordList = statementResult.list();

                    for (int i = 0; i < recordList.size(); ++i) {
                        JSONObject ciConnection = getCIConnection(recordList.get(i));
                        result.add(ciConnection);
                    }
                }
            }
        } catch (Exception e) {
            result = null;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 新增CI类型之间的连接关联关系
     * @param jsonObject CI类型信息及连接信息
     * @return 添加结果
     */
    @Override
    public boolean addCITypeConnectionRelationship(JSONObject jsonObject) {
        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String parent = jsonObject.getString("parent");
                    String child = jsonObject.getString("child");
                    String ciConnectionType = jsonObject.getString("type");
                    CICorrespondenceType correspondence = CICorrespondenceType.values()[jsonObject.getInteger("correspondence")];

                    String cmd = "match (parent:" + Neo4jDBNodeType.CIType + " {name:{Parent}, level: 3}), " +
                            "(child:" + Neo4jDBNodeType.CIType + " {name:{Child}, level: 3}) " +
                            "where not (parent)-[:" + Neo4jDBRelationshipType.RELATIONSHIP + " {type:{type}}]->(child) " +
                            "return parent, child";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("Parent", parent, "Child", child, "type", ciConnectionType));

                    if (statementResult.list().size() == 0) {
                        return result;
                    }

                    cmd = "match (parent:" + Neo4jDBNodeType.CIType.toString() + " {name:{Parent}, level: 3}), " +
                            "(child:" + Neo4jDBNodeType.CIType.toString() + " {name:{Child}, level: 3}) " +
                            "create (parent)-[:" + Neo4jDBRelationshipType.RELATIONSHIP.toString() + " " +
                            "{type:{type}, correspondence:{correspondence}}]->(child)";
                    statementResult = transaction.run(cmd,
                            Values.parameters("Parent", parent, "Child", child,
                                    "type", ciConnectionType, "correspondence", correspondence.ordinal()));

                    ResultSummary summary = statementResult.summary();
                    if (summary.counters().relationshipsCreated() == 1 && summary.counters().propertiesSet() == 2) {
                        transaction.success();
                        result = true;
                    } else {
                        transaction.failure();
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 删除指定CI类型之间的连接关联关系
     * @param jsonObject CI类型信息及连接信息
     * @return 删除结果
     */
    @Override
    public boolean deleteCITypeConnectionRelationship(JSONObject jsonObject) {
        boolean result = false;

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String parent = jsonObject.getString("parent");
                    String child = jsonObject.getString("child");
                    String ciConnectionType = jsonObject.getString("type");

                    String cmd = "match (parent:" + Neo4jDBNodeType.CIType + " {name:{Parent}, level: 3}), " +
                            "(child:" + Neo4jDBNodeType.CIType + " {name:{Child}, level: 3}), " +
                            "(parent)-[r:" + Neo4jDBRelationshipType.RELATIONSHIP + " {type:{type}}]->(child) " +
                            "delete r";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("Parent", parent, "Child", child, "type", ciConnectionType));

                    ResultSummary summary = statementResult.summary();
                    if (summary.counters().relationshipsDeleted() == 1) {
                        transaction.success();
                        result = true;
                    } else {
                        transaction.failure();
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 查询指定的两个CI类型之间存在的连接关联关系
     * @param jsonObject CI类型信息
     * @return 查询结果
     */
    @Override
    public JSONArray searchCITypeConnectionRelationship(JSONObject jsonObject) {

        JSONArray result = new JSONArray();

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String name1 = jsonObject.getString("parent");
                    String name2 = ".*";
                    if (jsonObject.containsKey("child")) {
                        name2 = jsonObject.getString("child");
                    }

                    String cmd = "match (item1:" + Neo4jDBNodeType.CIType + " {name:{Name1}, level: 3})" +
                            "-[r:" + Neo4jDBRelationshipType.RELATIONSHIP + "]->" +
                            "(item2:" + Neo4jDBNodeType.CIType + " {level: 3}) " +
                            "where item2.name =~ '" + name2 + "' " +
                            "return item2.name, r";

                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("Name1", name1));
                    List<Record> recordList = statementResult.list();

                    for (int i = 0; i < recordList.size(); ++i) {
                        JSONObject ciConnection = new JSONObject();

                        ciConnection.put("name", recordList.get(i).values().get(0).asString());
                        ciConnection.put("type", recordList.get(i).values().get(1).get("type").asString());
                        ciConnection.put("correspondence", recordList.get(i).values().get(1).get("correspondence").asInt());

                        result.add(ciConnection);
                    }
                }
            }
        } catch (Exception e) {
            result = null;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 新增CI属性信息
     * @param jsonObject CI属性信息
     * @return 新增结果
     */
    @Override
    public boolean addCIProp(JSONObject jsonObject) {

        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String name = jsonObject.getString("name");
                    JSONObject prop = jsonObject.getJSONObject("prop");
                    JSONArray names = prop.getJSONArray("name");
                    List<String> nameList = names.toJavaList(String.class);
                    String businessCode = "";
                    if (prop.containsKey("enterpriseCode") && prop.containsKey("serverCode")) {
                        String enterpriseCode = prop.getString("enterpriseCode");
                        String serverCode = prop.getString("serverCode");
                        businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);
                    }

                    String search = "match (prop:" + Neo4jDBNodeType.CIProp + " {businessCode:{BusinessCode}}), " +
                            "(type:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) " +
                            "where (prop)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type) " +
                            "return prop";

                    StatementResult statementResult = transaction.run(search,
                            Values.parameters("BusinessCode", businessCode, "Name", name));
                    List<Record> recordList = statementResult.list();
                    if (recordList.size() > 0) {
                        return result;
                    }

                    if (isCIPropRepeat(transaction, businessCode, name, nameList)) {
                        transaction.failure();
                        return result;
                    }

                    String add = "match (type:" + Neo4jDBNodeType.CIType + " {name:{TypeName}, level:3}) " +
                            "create (prop:" + Neo4jDBNodeType.CIProp + " {title:{Title}, name:{Name}, type:{Type}, businessCode:{BusinessCode}}) " +
                            "create (prop)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type)";
                    statementResult = transaction.run(add,
                            Values.parameters("TypeName", name,
                                    "Title", prop.getJSONArray("title"),
                                    "Name", prop.getJSONArray("name"),
                                    "Type", prop.getJSONArray("type"),
                                    "BusinessCode", businessCode));
                    ResultSummary summary = statementResult.summary();
                    if (summary.counters().nodesCreated() == 1 && summary.counters().relationshipsCreated() == 1) {
                        transaction.success();
                        result = true;
                    } else {
                        transaction.failure();
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 删除指定的CI属性
     * @param jsonObject CI属性信息
     * @return 删除结果
     */
    @Override
    public boolean deleteCIProp(JSONObject jsonObject) {

        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String name = jsonObject.getString("name");
                    String enterpriseCode = jsonObject.getString("enterpriseCode");
                    String serverCode = jsonObject.getString("serverCode");
                    String businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);

                    String cmd = "match (prop:" + Neo4jDBNodeType.CIProp + " {businessCode:{BusinessCode}}), " +
                            "(type:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) " +
                            "where (prop)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type) " +
                            "DETACH DELETE prop";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("BusinessCode", businessCode, "Name", name));
                    ResultSummary summary = statementResult.summary();

                    if (summary.counters().nodesDeleted() == 1) {
                        transaction.success();
                        result = true;
                    } else {
                        transaction.failure();
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 修改指定的CI属性
     * @param jsonObject CI属性信息
     * @return 修改结果
     */
    @Override
    public boolean modifyCIProp(JSONObject jsonObject) {

        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String name = jsonObject.getString("name");
                    JSONObject prop = jsonObject.getJSONObject("prop");
                    JSONArray names = prop.getJSONArray("name");
                    List<String> nameList = names.toJavaList(String.class);
                    String businessCode = "";
                    if (prop.containsKey("enterpriseCode") && prop.containsKey("serverCode")) {
                        String enterpriseCode = prop.getString("enterpriseCode");
                        String serverCode = prop.getString("serverCode");
                        businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);
                    }

                    if (isCIPropRepeat(transaction, businessCode, name, nameList)) {
                        transaction.failure();
                        return result;
                    }

                    String cmd = "match (prop:" + Neo4jDBNodeType.CIProp + " {businessCode:{BusinessCode}}), " +
                            "(type:" + Neo4jDBNodeType.CIType + " {name:{TypeName}, level:3}) " +
                            "where (prop)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type)" +
                            "set prop.name = {Name}, prop.title = {Title}, prop.type = {Type}";
                    StatementResult statementResult = transaction.run(cmd, Values.parameters("BusinessCode", businessCode,
                            "TypeName", name,
                            "Title", prop.getJSONArray("title"),
                            "Name", prop.getJSONArray("name"),
                            "Type", prop.getJSONArray("type")));
                    ResultSummary summary = statementResult.summary();
                    if (summary.counters().propertiesSet() == 3) {
                        transaction.success();
                        result = true;
                    } else {
                        transaction.failure();
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 查询指定CI类型的CI属性
     * @param jsonObject CI类型信息
     * @return 查询结果
     */
    @Override
    public JSONObject searchCIProp(JSONObject jsonObject) {

        JSONObject result = new JSONObject();

        if (jsonObject == null) {
            return null;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String name = jsonObject.getString("name");
                    String businessCode = "";
                    if (jsonObject.containsKey("enterpriseCode") && jsonObject.containsKey("serverCode")) {
                        String enterpriseCode = jsonObject.getString("enterpriseCode");
                        String serverCode = jsonObject.getString("serverCode");
                        businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);
                    }

                    String cmd = "match (props:" + Neo4jDBNodeType.CIProp + "), " +
                            "(type:" + Neo4jDBNodeType.CIType + " {name:{Name}}) " +
                            "where (props)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type) " +
                            "return props";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("Name", name));
                    List<Record> recordList = statementResult.list();

                    JSONArray jsonArray = new JSONArray();
                    result.put("name", name);
                    result.put("props", jsonArray);
                    for (int i = 0; i < recordList.size(); ++i) {
                        JSONObject prop = getCIProp(recordList.get(i));

                        if (!prop.containsKey("businessCode") || businessCode.equals("")) {
                            jsonArray.add(prop);
                        } else if (prop.getString("businessCode").equals(businessCode)) {
                            jsonArray.add(prop);
                        }
                    }
                }
            }
        } catch (Exception e) {
            result = null;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 新增CI信息
     * @param jsonObject CI信息
     * @return 新增结果
     */
    @Override
    public String addCI(JSONObject jsonObject) {

        String result = "";

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String name = jsonObject.getString("type");
                    String enterpriseCode = jsonObject.getString("enterpriseCode");
                    String serverCode = jsonObject.getString("serverCode");
                    String businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);

                    jsonObject.put("createTime", System.currentTimeMillis());
                    jsonObject.put("modifyTime", System.currentTimeMillis());

                    String cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3})" +
                            "<-[:" + Neo4jDBRelationshipType.INCLUDE + " *1..3]-" +
                            "(parent:" + Neo4jDBNodeType.CIType + ") return item, parent";
                    StatementResult statementResult = transaction.run(cmd, Values.parameters("Name", name));
                    List<Record> recordList = statementResult.list();

                    String code1 = "", code2 = "", code3 = "";
                    for (Record record:recordList) {
                        for (org.neo4j.driver.v1.Value value:record.values()) {
                            int level = value.get("level").asInt();
                            switch (level) {
                                case 1:
                                    code1 = value.get("code").asString();
                                    break;
                                case 2:
                                    code2 = value.get("code").asString();
                                    break;
                                case 3:
                                    code3 = value.get("code").asString();
                                    break;
                            }
                        }
                    }

                    if (code1.equals("") || code2.equals("") || code3.equals("")) {
                        return result;
                    }

                    cmd = "match (type:"+ Neo4jDBNodeType.CIType + " {name:{Name}, level:3}), " +
                            "(prop:" + Neo4jDBNodeType.CIProp + ") " +
                            "where (prop)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type) and " +
                            "(prop.businessCode = '' or prop.businessCode = '" + businessCode + "')" +
                            "return prop";
                    statementResult = transaction.run(cmd, Values.parameters("Name", name));
                    recordList = statementResult.list();
                    if (recordList.size() < 1) {
                        return result;
                    }

                    String propStr = "";

                    for (Record record:recordList) {
                        JSONObject prop = getCIProp(record);

                        JSONArray nameArray = prop.getJSONArray("name");
                        JSONArray typeArray = prop.getJSONArray("type");
                        for (int i = 0; i < nameArray.size(); ++i) {
                            String propName = nameArray.getString(i);
                            String propType = typeArray.getString(i);

                            if (jsonObject.containsKey(propName)) {
                                if (propType.equals("string")) {
                                    propStr += "," + propName + ":'" + jsonObject.getString(propName) + "'";
                                } else if (propType.equals("number")) {
                                    propStr += "," + propName + ":" + jsonObject.getLong(propName);
                                } else if (propType.equals("bool")) {
                                    propStr += "," + propName + ":" + jsonObject.getBoolean(propName);
                                }
                            }
                        }
                    }

                    if (!jsonObject.containsKey("address")) {
                        propStr += ",address:'000000'";
                    }
                    if (!jsonObject.containsKey("status")) {
                        propStr += ",status:true";
                    }

                    String sn = jsonObject.getString("sn");
                    String ciTypeCode = Neo4jUtils.getCITypeCode(code1, code2, code3);
                    String ciId = Neo4jUtils.getCIId(ciTypeCode, sn, enterpriseCode, serverCode);

                    cmd = "create (ci:" + Neo4jDBNodeType.CI + " {id:{Id}" + propStr + "}) return ci.id";

                    statementResult = transaction.run(cmd, Values.parameters("Id", ciId,
                            "BusinessCode", businessCode));
                    ResultSummary summary = statementResult.summary();

                    if (summary.counters().nodesCreated() != 1) {
                        transaction.failure();
                        return result;
                    }

                    recordList = statementResult.list();
                    result = recordList.get(0).values().get(0).asString();

                    transaction.success();
                }
            }
        } catch (Exception e) {
            result = "";
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 删除CI信息
     * @param jsonObject CI信息
     * @return 删除结果
     */
    @Override
    public boolean deleteCI(JSONObject jsonObject) {

        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String id = jsonObject.getString("id");
                    String cmd = "match (ci:" + Neo4jDBNodeType.CI + " {id:{Id}}) " +
                            "where not ()<-[:" + Neo4jDBRelationshipType.RELATIONSHIP + "]-(ci) " +
                            "detach delete ci";
                    StatementResult statementResult = transaction.run(cmd, Values.parameters("Id", id));
                    ResultSummary summary = statementResult.summary();

                    if (summary.counters().nodesDeleted() > 0) {
                        transaction.success();
                        result = true;
                    } else {
                        transaction.failure();
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 修改CI信息
     * @param jsonObject CI信息
     * @return 修改结果
     */
    @Override
    public boolean modifyCI(JSONObject jsonObject) {

        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String id = jsonObject.getString("id");
                    jsonObject.put("modifyTime", System.currentTimeMillis());

                    String typeCode = Neo4jUtils.getTypeCode(id);
                    String businessCode = Neo4jUtils.getBusinessCode(id);

                    JSONObject basicProp = null, extProp = null;

                    String cmd = "match (prop:" + Neo4jDBNodeType.CIProp + ")-[:" + Neo4jDBRelationshipType.ATTACH +" ]->" +
                            "(:" + Neo4jDBNodeType.CIType + " {code:'" + typeCode + "'}) " +
                            "where prop.businessCode = '' or prop.businessCode = '" + businessCode + "' return prop";
                    StatementResult statementResult = transaction.run(cmd);
                    List<Record> recordList = statementResult.list();

                    for (Record record:recordList) {
                        JSONObject prop = getCIProp(record);
                        if (prop.containsKey("businessCode")) {
                            extProp = prop;
                        } else {
                            basicProp = prop;
                        }
                    }

                    JSONArray nameArray = new JSONArray();
                    JSONArray typeArray = new JSONArray();
                    if (basicProp != null) {
                        nameArray.addAll(basicProp.getJSONArray("name"));
                        typeArray.addAll(basicProp.getJSONArray("type"));
                    }
                    if (extProp != null) {
                        nameArray.addAll(extProp.getJSONArray("name"));
                        typeArray.addAll(extProp.getJSONArray("type"));
                    }

                    String propStr = "";
                    int count = 0;
                    for (int i = 0; i < nameArray.size(); ++i) {
                        String propName = nameArray.getString(i);
                        String propType = typeArray.getString(i);

                        if (propName.equals("createTime") || propName.equals("id") ||
                                propName.equals("enterpriseCode")  || propName.equals("serverCode") ||
                                propName.equals("sn")  || propName.equals("user") ||
                                propName.equals("gatewayServerCode") || propName.equals("type")) {
                            continue;
                        }

                        if (jsonObject.containsKey(propName)) {
                            count++;
                            if (propType.equals("string")) {
                                propStr += "ci." + propName + " = '" + jsonObject.getString(propName) + "',";
                            } else if (propType.equals("number")) {
                                propStr += "ci." + propName + " = " + jsonObject.getLong(propName) + ",";
                            } else if (propType.equals("bool")) {
                                propStr += "ci." + propName + " = " + jsonObject.getBoolean(propName) + ",";
                            }
                        }
                    }

                    if (count > 0) {
                        cmd = "match (ci:" + Neo4jDBNodeType.CI + " {id:{Id}}) set " + propStr.substring(0, propStr.length() - 1);
                        statementResult = transaction.run(cmd, Values.parameters("Id", id));
                        if (statementResult.summary().counters().propertiesSet() != count) {
                            transaction.failure();
                            return result;
                        } else {
                            result = true;
                        }
                    } else {
                        result = true;
                    }

                    transaction.success();
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 查询CI信息
     * @param jsonObject 查询条件
     * @return 查询结果
     */
    @Override
    public JSONObject searchCI(JSONObject jsonObject) {

        JSONObject result = new JSONObject();

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String businessCode = "";

                    List<String> conditionList = new ArrayList<>();
                    conditionList.add("ci.id is not null");
                    if (jsonObject.containsKey("enterpriseCode") && jsonObject.containsKey("serverCode")) {
                        String enterpriseCode = jsonObject.getString("enterpriseCode");
                        String serverCode = jsonObject.getString("serverCode");

                        conditionList.add("ci.enterpriseCode = '" + enterpriseCode + "'");
                        conditionList.add("ci.serverCode = '" + serverCode + "'");
                        businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);
                    }
                    if (jsonObject.containsKey("type")) {
                        conditionList.add("ci.type = '" + jsonObject.getString("type") + "'");
                    }
                    if (jsonObject.containsKey("id")) {
                        conditionList.add("ci.id =~ '.*" + jsonObject.getString("id") + ".*'");
                    }
                    if (jsonObject.containsKey("ids")) {
                        conditionList.add("ci.id in " + JSONObject.toJSONString(jsonObject.getJSONArray("ids")));
                    }
                    if (jsonObject.containsKey("sn")) {
                        conditionList.add("ci.sn =~ '.*" + jsonObject.getString("sn") + ".*'");
                    }
                    if (jsonObject.containsKey("sns")) {
                        conditionList.add("ci.sn in " + JSONObject.toJSONString(jsonObject.getJSONArray("sns")));
                    }
                    if (jsonObject.containsKey("addressCodes")) {
                        conditionList.add("ci.address in " + JSONObject.toJSONString(jsonObject.getJSONArray("addressCodes")));
                    }
                    if (jsonObject.containsKey("status")) {
                        conditionList.add("ci.status = " + jsonObject.getBoolean("status"));
                    }
                    if (jsonObject.containsKey("startCreateTime")) {
                        conditionList.add("ci.createTime > " + jsonObject.getLong("startCreateTime"));
                    }
                    if (jsonObject.containsKey("endCreateTime")) {
                        conditionList.add("ci.createTime < " + jsonObject.getLong("endCreateTime"));
                    }
                    if (jsonObject.containsKey("startModifyTime")) {
                        conditionList.add("ci.modifyTime > " + jsonObject.getLong("startModifyTime"));
                    }
                    if (jsonObject.containsKey("endModifyTime")) {
                        conditionList.add("ci.modifyTime < " + jsonObject.getLong("endModifyTime"));
                    }

                    String condition = "";
                    if (conditionList.size() > 0) {
                        condition = "where " + conditionList.get(0);
                        for (int i = 1; i < conditionList.size(); ++i) {
                            condition += " and " + conditionList.get(i);
                        }
                    }

                    String cmd = "match (ci:" + Neo4jDBNodeType.CI + ") " + condition;
                    StatementResult statementResult = transaction.run(cmd + " return count(ci)");
                    List<Record> recordList = statementResult.list();

                    int count = recordList.get(0).values().get(0).asInt();
                    result.put("count", count);

                    cmd = "match (ci:" + Neo4jDBNodeType.CI + ") " + condition +
                            " return ci order by id(ci)";
                    if (jsonObject.containsKey("curPage") && jsonObject.containsKey("pageNum")) {
                        int curPage = jsonObject.getInteger("curPage");
                        int pageNum = jsonObject.getInteger("pageNum");
                        cmd += " skip " + (curPage - 1) * pageNum + " limit " + pageNum;
                    }
                    statementResult = transaction.run(cmd);
                    recordList = statementResult.list();

                    HashMap<String, JSONObject> propMap = new HashMap<>();

                    JSONArray jsonArray = new JSONArray();
                    for (Record record:recordList) {
                        String type = record.values().get(0).get("type").asString();

                        if (!propMap.containsKey(type)) {
                            String searchProp = "match (:" + Neo4jDBNodeType.CIType + " {name:{Name}})" +
                                    "<-[:" + Neo4jDBRelationshipType.ATTACH + "]-" +
                                    "(prop:" + Neo4jDBNodeType.CIProp + ") " +
                                    "where prop.businessCode = '' or prop.businessCode = '" + businessCode + "' " +
                                    "return prop";
                            statementResult = transaction.run(searchProp,
                                    Values.parameters("Name", type));
                            List<Record> propList = statementResult.list();

                            JSONArray nameArray = new JSONArray();
                            JSONArray typeArray = new JSONArray();
                            for (Record propRecord : propList) {
                                JSONObject prop = getCIProp(propRecord);
                                nameArray.addAll(prop.getJSONArray("name"));
                                typeArray.addAll(prop.getJSONArray("type"));
                            }
                            JSONObject prop = new JSONObject();
                            prop.put("name", nameArray);
                            prop.put("type", typeArray);
                            propMap.put(type, prop);
                        }

                        if (!propMap.containsKey(type)) {
                            continue;
                        }

                        JSONObject ci = getCI(record, propMap.get(type));

                        jsonArray.add(ci);
                    }

                    result.put("count", count);
                    result.put("infos", jsonArray);
                }
            }
        } catch (Exception e) {
            result = null;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 添加CI信息关联关系
     * @param jsonObject CI信息
     * @return 添加结果
     */
    @Override
    public boolean addCIRelationship(JSONObject jsonObject) {

        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String id1 = jsonObject.getString("id1");
                    String id2 = jsonObject.getString("id2");
                    String relationshipType = jsonObject.getString("type");

                    String ciType1 = Neo4jUtils.getTypeCode(id1);
                    String ciType2 = Neo4jUtils.getTypeCode(id2);

                    JSONObject relationship = null;

                    String cmd = "match (:" + Neo4jDBNodeType.CIType + " {code:{Code1}, level:3})" +
                            "-[r:" + Neo4jDBRelationshipType.RELATIONSHIP + "]->" +
                            "(:" + Neo4jDBNodeType.CIType + " {code:{Code2}, level:3}) " +
                            "return r";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("Code1", ciType1, "Code2", ciType2));
                    List<Record> recordList = statementResult.list();

                    for (Record record:recordList) {
                        JSONObject tmp = getCITypeRelationship(record);
                        if (tmp.get("type").toString().equals(relationshipType)) {
                            relationship = tmp;
                            break;
                        }
                    }

                    if (relationship == null) {
                        statementResult = transaction.run(cmd,
                                Values.parameters("Code1", ciType2, "Code2", ciType1));
                        recordList = statementResult.list();

                        for (Record record:recordList) {
                            JSONObject tmp = getCITypeRelationship(record);
                            if (tmp.get("type").toString().equals(relationshipType)) {
                                relationship = tmp;
                                String temp = id1;
                                id1 = id2;
                                id2 = temp;
                                break;
                            }
                        }
                    }

                    if (relationship == null) {
                        transaction.failure();
                        return false;
                    }

                    cmd = "match (ci1:" + Neo4jDBNodeType.CI + " {id:{Id1}})" +
                            "-[r:" + Neo4jDBRelationshipType.RELATIONSHIP + " {type:{Type}, status:true}]->" +
                            "(ci2:" + Neo4jDBNodeType.CI + " {id:{Id2}}) " +
                            "return r";
                    statementResult = transaction.run(cmd,
                            Values.parameters("Type", relationshipType, "Id1", id1, "Id2", id2));
                    recordList = statementResult.list();
                    if (recordList.size() > 0) {
                        transaction.failure();
                        return true;
                    }

                    CICorrespondenceType ciCorrespondenceType = CICorrespondenceType.values()[relationship.getInteger("correspondence")];
                    if (ciCorrespondenceType == CICorrespondenceType.One_To_One) {
                        cmd = "match (ci1:" + Neo4jDBNodeType.CI + " )" +
                                "-[r:" + Neo4jDBRelationshipType.RELATIONSHIP + " {type:{Type}, status:true}]->" +
                                "(ci2:" + Neo4jDBNodeType.CI + ") " +
                                "where ci1.id = '" + id1 + "' or ci2.id = '" + id2 + "' " +
                                "return r";
                        statementResult = transaction.run(cmd,
                                Values.parameters("Type", relationshipType));
                        recordList = statementResult.list();

                        if (recordList.size() > 0) {
                            transaction.failure();
                            return false;
                        }
                    }

                    cmd = "match (ci1:" + Neo4jDBNodeType.CI + " {id:{Id1}}), " +
                            "(ci2:" + Neo4jDBNodeType.CI + " {id:{Id2}}) " +
                            "create (ci1)-[:" + Neo4jDBRelationshipType.RELATIONSHIP + " {type:{Type}, status:true}]->(ci2)";
                    statementResult = transaction.run(cmd,
                            Values.parameters("Id1", id1, "Id2", id2, "Type", relationshipType));
                    ResultSummary summary = statementResult.summary();

                    if (summary.counters().relationshipsCreated() != 1) {
                        transaction.failure();
                        return false;
                    }

                    cmd = "match (ci1:" + Neo4jDBNodeType.CI + " {id:{Id1}})" +
                            "-[r:" + Neo4jDBRelationshipType.RELATIONSHIP + " {type:{Type}, status:false}]->" +
                            "(ci2:" + Neo4jDBNodeType.CI + " {id:{Id2}}) " +
                            "delete r";
                    transaction.run(cmd, Values.parameters("Id1", id1,
                            "Type", relationshipType,
                            "Id2", id2));

                    transaction.success();
                    result = true;
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 查询指定CI项的连接CI信息
     * @param jsonObject 查询条件
     * @return 连接CI信息
     */
    @Override
    public JSONObject searchCIRelationship(JSONObject jsonObject) {

        JSONObject result = new JSONObject();

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String id = jsonObject.getString("id");
                    String enterpriseCode = jsonObject.getString("enterpriseCode");
                    String serverCode = jsonObject.getString("serverCode");

                    String cmd = "match (ci:" + Neo4jDBNodeType.CI + " {id:{Id},enterpriseCode:{EnterpriseCode},serverCode:{ServerCode}})" +
                            "-[r:" + Neo4jDBRelationshipType.RELATIONSHIP + " {status:true}]->" +
                            "(children:" + Neo4jDBNodeType.CI + " {enterpriseCode:{EnterpriseCode},serverCode:{ServerCode}})";
                    if (jsonObject.containsKey("addressCodes")) {
                        cmd += " where ci.address in " + JSONObject.toJSONString(jsonObject.getJSONArray("addressCodes")) + " " +
                                "children.address in " + JSONObject.toJSONString(jsonObject.getJSONArray("addressCodes"));
                    }
                    cmd += " return children, r";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("Id", id,
                                    "EnterpriseCode", enterpriseCode,
                                    "ServerCode", serverCode));
                    List<Record> recordList = statementResult.list();

                    JSONArray children = new JSONArray();
                    for (Record record:recordList) {
                        children.add(getCIRelationship(record));
                    }

                    cmd = "match (ci:" + Neo4jDBNodeType.CI + " {id:{Id},enterpriseCode:{EnterpriseCode},serverCode:{ServerCode}})" +
                            "<-[r:" + Neo4jDBRelationshipType.RELATIONSHIP + " {status:true}]-" +
                            "(children:" + Neo4jDBNodeType.CI + " {enterpriseCode:{EnterpriseCode},serverCode:{ServerCode}})";
                    if (jsonObject.containsKey("addressCodes")) {
                        cmd += " where ci.address in " + JSONObject.toJSONString(jsonObject.getJSONArray("addressCodes")) + " " +
                                "children.address in " + JSONObject.toJSONString(jsonObject.getJSONArray("addressCodes"));
                    }
                    cmd += " return children, r";
                    statementResult = transaction.run(cmd,
                            Values.parameters("Id", id,
                                    "EnterpriseCode", enterpriseCode,
                                    "ServerCode", serverCode));
                    recordList = statementResult.list();

                    JSONArray parent = new JSONArray();
                    for (Record record:recordList) {
                        parent.add(getCIRelationship(record));
                    }

                    result.put("id", id);
                    result.put("parent", parent);
                    result.put("children", children);

                    transaction.success();
                }
            }
        } catch (Exception e) {
            result = null;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 删除CI信息关联关系
     * @param jsonObject CI信息
     * @return 删除结果
     */
    @Override
    public boolean deleteCIRelationship(JSONObject jsonObject) {

        boolean result = false;

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String id1 = jsonObject.getString("id1");
                    String id2 = jsonObject.getString("id2");
                    String relationshipType = jsonObject.getString("type");

                    String cmd = "match (:" + Neo4jDBNodeType.CI + " {id:{Id1}})" +
                            "-[r:" + Neo4jDBRelationshipType.RELATIONSHIP + " {type:{Type}}]-" +
                            "(:" + Neo4jDBNodeType.CI + " {id:{Id2}}) " +
                            "set r.status = false";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("Id1", id1, "Id2", id2, "Type", relationshipType));
                    ResultSummary summary = statementResult.summary();

                    if (summary.counters().propertiesSet() == 1) {
                        transaction.success();
                        return true;
                    } else {
                        transaction.failure();
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            result = false;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 查询CI设备型号信息
     * @param jsonObject 业务和服务信息
     * @return 查询结果
     */
    @Override
    public JSONArray searchCIModel(JSONObject jsonObject) {

        JSONArray result = new JSONArray();

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String enterpriseCode = jsonObject.getString("enterpriseCode");
                    String serverCode = jsonObject.getString("serverCode");

                    String cmd = "match (ci:" + Neo4jDBNodeType.CI + " " +
                            "{enterpriseCode:{EnterpriseCode}, serverCode:{ServerCode}}) " +
                            "return distinct  ci.type, ci.model";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("EnterpriseCode", enterpriseCode, "ServerCode", serverCode));
                    List<Record> recordList = statementResult.list();

                    for (Record record:recordList) {
                        JSONObject model = new JSONObject();
                        model.put("type", record.values().get(0).asString());
                        model.put("model", record.values().get(1).asString());
                        result.add(model);
                    }

                    transaction.success();
                }
            }
        } catch (Exception e) {
            result = null;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 根据企业编码、服务编码和地区编码列表查询CI项的id列表
     * @param jsonObject 查询信息
     * @return id列表
     */
    @Override
    public JSONArray searchCIIds(JSONObject jsonObject) {

        JSONArray result = new JSONArray();

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String enterpriseCode = jsonObject.getString("enterpriseCode");
                    String serverCode = jsonObject.getString("serverCode");
                    JSONArray addressCodes = jsonObject.getJSONArray("addressCodes");

                    String cmd = "match (ci:" + Neo4jDBNodeType.CI + " " +
                            "{enterpriseCode:{EnterpriseCode}, serverCode:{ServerCode}}) " +
                            "where ci.address in " + JSONObject.toJSONString(addressCodes) + " return ci.id";
                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("EnterpriseCode", enterpriseCode, "ServerCode", serverCode));
                    List<Record> recordList = statementResult.list();

                    for (Record record:recordList) {
                        result.add(record.values().get(0).asString());
                    }

                    transaction.success();
                }
            }
        } catch (Exception e) {
            result = null;
            logger.error(JSONObject.toJSONString(e), serverCode);
        }

        return result;
    }

    /**
     * 打开数据库
     * @return 数据库链接
     */
    private void openDriver() {
        if (driver == null) {
            driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
        }
    }

    /**
     * 在返回记录中获取CIType
     * @param record 返回记录
     * @return CIType信息
     */
    private JSONObject getCIType(Record record) {
        JSONObject ciType = new JSONObject();

        ciType.put("title", record.values().get(0).get("title").asString());
        ciType.put("name", record.values().get(0).get("name").asString());
        ciType.put("code", record.values().get(0).get("code").asString());
        ciType.put("level", record.values().get(0).get("level").asInt());
        ciType.put("businessCodes", JSONObject.parseArray(JSONObject.toJSONString(record.values().get(0).get("businessCodes").asList())));

        return ciType;
    }

    /**
     * 在返回记录中获取CIConnection信息
     * @param record 返回记录
     * @return CIConnection信息
     */
    private JSONObject getCIConnection(Record record) {
        JSONObject ciConnection = new JSONObject();

        ciConnection.put("title", record.values().get(0).get("title").asString());
        ciConnection.put("name", record.values().get(0).get("name").asString());
        ciConnection.put("ascription", record.values().get(0).get("ascription").asBoolean());

        return ciConnection;
    }

    /**
     * 在返回记录中获取CIProp信息
     * @param record 返回记录
     * @return CIProp信息
     */
    private JSONObject getCIProp(Record record) {
        JSONObject ciProp = new JSONObject();

        ciProp.put("title", JSONObject.parseArray(JSONObject.toJSONString(record.values().get(0).get("title").asList())));
        ciProp.put("name", JSONObject.parseArray(JSONObject.toJSONString(record.values().get(0).get("name").asList())));
        ciProp.put("type", JSONObject.parseArray(JSONObject.toJSONString(record.values().get(0).get("type").asList())));

        String businessCode = record.values().get(0).get("businessCode").asString();
        if (!businessCode.equals("")) {
            List<String> list = Neo4jUtils.getEnterpriseCodeAndServerCode(businessCode);
            ciProp.put("businessCode", businessCode);
            ciProp.put("enterpriseCode", list.get(0));
            ciProp.put("serverCode", list.get(1));
        }

        return ciProp;
    }

    /**
     * 在返回记录中获取CI类型连接关系
     * @param record 返回记录
     * @return CI类型连接关系
     */
    private JSONObject getCITypeRelationship(Record record) {
        JSONObject ciRelationship = new JSONObject();

        ciRelationship.put("type", record.values().get(0).get("type").asString());
        ciRelationship.put("correspondence", record.values().get(0).get("correspondence").asInt());

        return ciRelationship;
    }

    /**
     * 检测CI属性name是否重复
     * @param transaction 事务
     * @param businessCode 业务编码
     * @param typeName CI类型
     * @param nameList 待检测CI属性列表
     * @return 检测结果，true重复，false不重复
     */
    private boolean isCIPropRepeat(Transaction transaction, String businessCode, String typeName, List<String> nameList) {

        String cmd;
        if (businessCode.equals("")) {
            cmd = "match (props:" + Neo4jDBNodeType.CIProp + "), " +
                    "(type:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) " +
                    "where props.businessCode <> '' and " +
                    "(props)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type)" +
                    "return props.name";
        } else {
            cmd = "match (props:" + Neo4jDBNodeType.CIProp + " {businessCode:''}), " +
                    "(type:" + Neo4jDBNodeType.CIType + " {name:{Name}}) " +
                    "where (props)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type)" +
                    "return props.name";
        }

        StatementResult statementResult = transaction.run(cmd, Values.parameters("Name", typeName));
        List<Record> recordList = statementResult.list();

        for (Record record:recordList) {
            List<String> tmp = new ArrayList<>(record.values().get(0).asList(item -> item.asString()));
            tmp.retainAll(nameList);
            if (tmp.size() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 在返回记录中获取CI信息
     * @param record 返回记录
     * @param prop 属性信息
     * @return CI信息
     */
    private JSONObject getCI(Record record, JSONObject prop) {
        JSONObject result = new JSONObject();

        JSONArray names = prop.getJSONArray("name");
        JSONArray types = prop.getJSONArray("type");

        for (String key:record.values().get(0).keys()) {
            if (!names.contains(key)) {
                continue;
            }

            int index = names.indexOf(key);

            if (types.getString(index).equals("string")) {
                result.put(key, record.values().get(0).get(key).asString());
            } else if (types.getString(index).equals("number")) {
                result.put(key, record.values().get(0).get(key).asLong());
            } else if (types.getString(index).equals("bool")) {
                result.put(key, record.values().get(0).get(key).asBoolean());
            }
        }

        return result;
    }

    /**
     * 在返回记录中获取CI连接信息
     * @param record 返回记录
     * @return CI连接关系
     */
    private JSONObject getCIRelationship(Record record) {

        JSONObject ciRelationship = new JSONObject();

        ciRelationship.put("id", record.values().get(0).get("id").asString());
        ciRelationship.put("type", record.values().get(0).get("type").asString());
        ciRelationship.put("relationshipType", record.values().get(1).get("type").asString());

        return ciRelationship;
    }
}
