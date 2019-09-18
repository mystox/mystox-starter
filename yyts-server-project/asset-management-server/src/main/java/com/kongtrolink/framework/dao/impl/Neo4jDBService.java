package com.kongtrolink.framework.dao.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.CICorrespondenceType;
import com.kongtrolink.framework.entity.Neo4jDBNodeType;
import com.kongtrolink.framework.entity.Neo4jDBRelationshipType;
import com.kongtrolink.framework.dao.DBService;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("Neo4jDBService")
public class Neo4jDBService implements DBService {

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

        if (driver == null) {
            driver = openDriver();
        }

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
                        String cmd = "match (item:" + Neo4jDBNodeType.CIType.toString() + " {name:{Name}, level:{Level}}) return item";
                        statementResult = transaction.run(cmd, Values.parameters("Name", relationship, "Level", level - 1));
                        if (statementResult.list().size() != 1) {
                            transaction.failure();
                            return result;
                        }
                    }

                    String cmd = "create (item:" + Neo4jDBNodeType.CIType.toString() + " {title:{Title}, name:{Name}, code:{Code}, level:{Level}})";
                    statementResult = transaction.run(cmd, Values.parameters("Title", title, "Name", name, "Code", code, "Level", level));
                    summary = statementResult.summary();
                    if (summary.counters().nodesCreated() != 1) {
                        transaction.failure();
                        return result;
                    }

                    if (level > 1) {

                        cmd = "match (item1:" + Neo4jDBNodeType.CIType.toString() + " {name:{Name1}}), " +
                                "(item2:" + Neo4jDBNodeType.CIType.toString() + " {name:{Name2}, level:{Level}}) " +
                                "where not (item2)-[:" + Neo4jDBRelationshipType.INCLUDE.toString() + "]->(item1) " +
                                "create (item2)-[:" + Neo4jDBRelationshipType.INCLUDE.toString() + "]->(item1)";
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
            System.out.println(JSONObject.toJSONString(e));
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

        if (driver == null) {
            driver = openDriver();
        }

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String cmd = "match (item:" + Neo4jDBNodeType.CIType.toString() + " {name:{Name}}) " +
                            "where not (item)-[:" + Neo4jDBRelationshipType.INCLUDE.toString() + "]->() DETACH DELETE item";
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
            System.out.println(JSONObject.toJSONString(e));
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

        if (driver == null) {
            driver = openDriver();
        }

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {
                    String name = jsonObject.getString("name");
                    String title = jsonObject.getString("title");

                    String cmd = "match (item:" + Neo4jDBNodeType.CIType.toString() + " {name:{Name}}) " +
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
            System.out.println(JSONObject.toJSONString(e));
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

        if (driver == null) {
            driver = openDriver();
        }

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String title = jsonObject.getString("title");
                    String name = jsonObject.getString("name");
                    String code = jsonObject.getString("code");
                    int level = 0;
                    if (jsonObject.containsKey("level")) {
                        level = jsonObject.getInteger("level");
                    }

                    String searchType = "match (item:" + Neo4jDBNodeType.CIType.toString() + ") " +
                            "where item.name =~ '.*" + name + ".*' and item.title =~ '.*" + title + ".*' and " +
                            "item.code =~ '.*" + code + ".*'";
                    if (level > 0) {
                        searchType += " and item.level = " + level;
                    }
                    searchType += " return item";

                    StatementResult statementResult = transaction.run(searchType);
                    List<Record> recordList = statementResult.list();

                    for (int i = 0; i < recordList.size(); ++i) {
                        JSONObject ciType = getCIType(recordList.get(i));

                        JSONArray children = new JSONArray();
                        ciType.put("parent", null);
                        ciType.put("children", children);

                        String searchRelationship = "match (tmp:" + Neo4jDBNodeType.CIType.toString() + " {name:'" + ciType.getString("name") + "'})" +
                                "-[:" + Neo4jDBRelationshipType.INCLUDE.toString() + "]-(item:" + Neo4jDBNodeType.CIType.toString() + ") " +
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
            System.out.println(JSONObject.toJSONString(e));
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

        if (driver == null) {
            driver = openDriver();
        }

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {
                    String title = jsonObject.getString("title");
                    String name = jsonObject.getString("name");
                    boolean ascription = jsonObject.getBoolean("ascription");

                    String cmd = "create (item:" + Neo4jDBNodeType.CIConnectionType.toString() + " {title:{Title}, name:{Name}, ascription:{Ascription}})";
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
            System.out.println(JSONObject.toJSONString(e));
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

        if (driver == null) {
            driver = openDriver();
        }

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String cmd = "match (item:" + Neo4jDBNodeType.CIConnectionType.toString() + ") return item";

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
            System.out.println(JSONObject.toJSONString(e));
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

        if (driver == null) {
            driver = openDriver();
        }

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String parent = jsonObject.getString("parent");
                    String child = jsonObject.getString("child");
                    String ciConnectionType = jsonObject.getString("type");
                    CICorrespondenceType correspondence = CICorrespondenceType.values()[jsonObject.getInteger("correspondence")];

                    String cmd = "match (parent:" + Neo4jDBNodeType.CIType.toString() + " {name:{Parent}, level: 3}), " +
                            "(child:" + Neo4jDBNodeType.CIType.toString() + " {name:{Child}, level: 3}) " +
                            "where not (parent)-[:" + Neo4jDBRelationshipType.RELATIONSHIP.toString() + " {type:{type}}]->(child) " +
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
            System.out.println(JSONObject.toJSONString(e));
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

        if (driver == null) {
            driver = openDriver();
        }

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String parent = jsonObject.getString("parent");
                    String child = jsonObject.getString("child");
                    String ciConnectionType = jsonObject.getString("type");

                    String cmd = "match (parent:" + Neo4jDBNodeType.CIType.toString() + " {name:{Parent}, level: 3}), " +
                            "(child:" + Neo4jDBNodeType.CIType.toString() + " {name:{Child}, level: 3}), " +
                            "(parent)-[r:" + Neo4jDBRelationshipType.RELATIONSHIP.toString() + " {type:{type}}]->(child) " +
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
            System.out.println(JSONObject.toJSONString(e));
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

        if (driver == null) {
            driver = openDriver();
        }

        try {
            try (Session session = driver.session()) {
                try (Transaction transaction = session.beginTransaction()) {

                    String name1 = jsonObject.getString("name1");
                    String name2 = jsonObject.getString("name2");

                    String cmd = "match (item1:" + Neo4jDBNodeType.CIType.toString() + " {name:{Name1}, level: 3}), " +
                            "(item2:" + Neo4jDBNodeType.CIType.toString() + " {name:{Name2}, level: 3}), " +
                            "(item1)-[r:" + Neo4jDBRelationshipType.RELATIONSHIP.toString() + "]-(item2) " +
                            "return r";

                    StatementResult statementResult = transaction.run(cmd,
                            Values.parameters("Name1", name1, "Name2", name2));
                    List<Record> recordList = statementResult.list();

                    for (int i = 0; i < recordList.size(); ++i) {
                        JSONObject ciConnection = new JSONObject();

                        ciConnection.put("name", recordList.get(i).values().get(0).get("type").asString());
                        ciConnection.put("correspondence", recordList.get(i).values().get(0).get("correspondence").asInt());

                        result.add(ciConnection);
                    }
                }
            }
        } catch (Exception e) {
            result = null;
            System.out.println(JSONObject.toJSONString(e));
        }

        return result;
    }

    /**
     * 打开数据库
     * @return 数据库链接
     */
    private Driver openDriver() {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
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
}
