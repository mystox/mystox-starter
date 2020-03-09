package com.kongtrolink.framework.dao.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.CICorrespondenceType;
import com.kongtrolink.framework.entity.DBResult;
import com.kongtrolink.framework.entity.Neo4jDBNodeType;
import com.kongtrolink.framework.entity.Neo4jDBRelationshipType;
import com.kongtrolink.framework.dao.DBService;
import com.kongtrolink.framework.utils.Neo4jUtils;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    public DBResult addCIType(JSONObject jsonObject) {
        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型创建失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI类型创建失败，无输入参数");
            return result;
        }

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {
                String title = jsonObject.getString("title");
                String name = jsonObject.getString("name");
                String code = jsonObject.getString("code");
                int level = jsonObject.getInteger("level");
                String relationship = "";

                String cmd;
                StatementResult statementResult;
                ResultSummary summary;

                if (level > 1) {
                    relationship = jsonObject.getString("relationship");
                    cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:{Level}}) return item";
                    statementResult = transaction.run(cmd, Values.parameters("Name", relationship, "Level", level - 1));
                    if (statementResult.list().size() != 1) {
                        transaction.failure();
                        result.setInfo("CI类型创建失败，未找到上级类型");
                        return result;
                    }
                }

                try {
                    cmd = "create (item:" + Neo4jDBNodeType.CIType + " {title:{Title}, name:{Name}, code:{Code}, level:{Level}, businessCodes:[], icon:'default.png'})";
                    statementResult = transaction.run(cmd, Values.parameters("Title", title, "Name", name, "Code", code, "Level", level));
                    summary = statementResult.summary();
                    if (summary.counters().nodesCreated() != 1) {
                        transaction.failure();
                        result.setInfo("CI类型创建失败，新增节点失败");
                        return result;
                    }
                } catch (ClientException e) {
                    transaction.failure();
                    if (e.getMessage().contains("already exists with label `CIType` and property")) {
                        result.setInfo("CI类型创建失败，英文名或类型编码重复");
                        return result;
                    } else {
                        throw e;
                    }
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
                        result.setInfo("CI类型创建失败，与上级节点建立连接关系失败");
                        return result;
                    }
                }

                transaction.success();
                result.setInfo("CI类型创建成功");
                result.setResult(1);
            }
        }

        return result;
    }

    /**
     * 删除CI类型
     * @param name 待删除的CI类型名称
     * @return 删除结果
     */
    @Override
    public DBResult deleteCIType(String name) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型删除失败，未知错误");

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                String cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}}) " +
                        "where not (item)-[:" + Neo4jDBRelationshipType.INCLUDE + "]->() DETACH DELETE item";
                StatementResult statementResult = transaction.run(cmd, Values.parameters("Name", name));

                ResultSummary summary = statementResult.summary();

                if (summary.counters().nodesDeleted() == 1) {
                    transaction.success();
                    result.setResult(1);
                    result.setInfo("CI类型删除成功");
                } else {
                    transaction.failure();
                    result.setInfo("CI类型删除失败，删除节点失败");
                }
            }
        }

        return result;
    }

    /**
     * 修改CI类型
     * @param jsonObject 待修改的CI类型信息
     * @return 修改结果
     */
    @Override
    public DBResult modifyCIType(JSONObject jsonObject) {
        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型修改失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI类型修改失败，无输入参数");
            return result;
        }

        openDriver();

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
                    result.setInfo("CI类型修改失败，节点信息修改失败");
                    return result;
                }

                transaction.success();
                result.setInfo("CI类型修改成功");
                result.setResult(1);
            }
        }

        return result;
    }

    /**
     * 修改CI类型对应的图标文件名称
     * @param name CI类型名称
     * @param icon 图标文件名称
     * @return 修改结果
     */
    @Override
    public DBResult modifyCITypeIcon(String name, String icon) {
        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型图标文件名称修改失败，未知错误");

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                String cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}}) " +
                        "set item.icon = {Icon}";

                StatementResult statementResult = transaction.run(cmd, Values.parameters("Name", name, "Icon", icon));
                ResultSummary summary = statementResult.summary();

                if (summary.counters().propertiesSet() != 1) {
                    transaction.failure();
                    result.setInfo("CI类型图标文件名称修改失败，节点信息修改失败");
                    return result;
                }

                transaction.success();
                result.setInfo("CI类型图标文件名称修改成功");
                result.setResult(1);
            }
        }

        return result;
    }

    /**
     * 查询CI类型信息
     * @param jsonObject 查询条件
     * @return 查询结果
     */
    @Override
    public DBResult searchCIType(JSONObject jsonObject) {
        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型查询失败，未知错误");
        result.setJsonArray(new JSONArray());

        if (jsonObject == null) {
            result.setInfo("CI类型查询失败，无输入参数");
            return result;
        }

        openDriver();

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
                    if (level > 0) {
                        searchType += " and item.level = " + level;
                    }
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

                for (Record record : recordList) {
                    JSONObject ciType = getCIType(record);

                    if (!businessCode.equals("")) {
                        JSONArray businessCodes = ciType.getJSONArray("businessCodes");
                        boolean enabled = businessCodes.contains(businessCode);

                        ciType.put("enabled", enabled);
                    } else {
                        ciType.put("enabled", true);
                    }

                    getConnectedCIType(ciType, transaction);
                    result.getJsonArray().add(ciType);
                }

                transaction.success();
                result.setResult(1);
                result.setInfo("CI类型查询成功");
            }
        }

        return result;
    }

    /**
     * 通过类型名称查询CI类型信息
     * @param name 类型名称
     * @return 查询结果
     */
    @Override
    public DBResult searchCITypeByName(String name) {
        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型查询失败，未知错误");
        result.setJsonArray(new JSONArray());

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                String searchType = "match (item:" + Neo4jDBNodeType.CIType + ") " +
                        "where item.name = '" + name + "' return item";

                StatementResult statementResult = transaction.run(searchType);
                List<Record> recordList = statementResult.list();

                for (Record record : recordList) {
                    JSONObject ciType = getCIType(record);

                    getConnectedCIType(ciType, transaction);
                    result.getJsonArray().add(ciType);
                }

                transaction.success();
                result.setResult(1);
                result.setInfo("CI类型查询成功");
            }
        }

        return result;
    }

    /**
     * 绑定CI类型与业务编码
     * @param jsonObject 绑定信息
     * @return 绑定结果
     */
    @Override
    public DBResult bindCITypeBusinessCode(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型绑定失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI类型绑定失败，无输入参数");
            return result;
        }

        openDriver();

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

                if (recordList.size() != 1) {

                    cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) " +
                            "set item.businessCodes = item.businessCodes + {BusinessCode}";
                    statementResult = transaction.run(cmd,
                            Values.parameters("Name", name, "BusinessCode", businessCode));

                    ResultSummary summary = statementResult.summary();

                    if (summary.counters().propertiesSet() != 1) {
                        transaction.failure();
                        result.setInfo("CI类型绑定失败，节点信息修改失败");
                        return result;
                    }
                }

                transaction.success();
                result.setResult(1);
                result.setInfo("CI类型绑定成功");
            }
        }

        return result;
    }

    /**
     * 解绑CI类型与业务编码
     * @param jsonObject 解绑信息
     * @return 解绑结果
     */
    @Override
    public DBResult unbindCITypeBusinessCode(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型解绑失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI类型解绑失败，无输入参数");
            return result;
        }

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {
                String name = jsonObject.getString("name");
                String enterpriseCode = jsonObject.getString("enterpriseCode");
                String serverCode = jsonObject.getString("serverCode");
                String businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);

                String cmd = "match (item:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) return item";
                StatementResult statementResult = transaction.run(cmd, Values.parameters("Name", name));
                List<Record> recordList = statementResult.list();

                if (recordList.size() != 1) {
                    transaction.failure();
                    result.setInfo("CI类型解绑失败，未找到对应CI类型信息");
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
                        result.setInfo("CI类型解绑失败，节点信息修改失败");
                        return result;
                    }
                }

                transaction.success();
                result.setResult(1);
                result.setInfo("CI类型解绑成功");
            }
        }

        return result;
    }

    /**
     * 新增CI连接关系
     * @param jsonObject CI连接关系信息
     * @return 新增结果
     */
    @Override
    public DBResult addCIConnectionType(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI连接关系新增失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI连接关系新增失败，无输入参数");
            return result;
        }

        openDriver();

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
                    result.setResult(1);
                    result.setInfo("CI连接关系新增成功");
                } else {
                    transaction.failure();
                    result.setInfo("CI连接关系新增失败，节点创建失败");
                }
            }
        }

        return result;
    }

    /**
     * 查询所有CI连接关系
     * @return 查询结果
     */
    @Override
    public DBResult searchCIConnectionType() {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI连接关系查询失败，未知错误");
        result.setJsonArray(new JSONArray());

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                String cmd = "match (item:" + Neo4jDBNodeType.CIConnectionType + ") return item";

                StatementResult statementResult = transaction.run(cmd);
                List<Record> recordList = statementResult.list();

                for (Record record : recordList) {
                    JSONObject ciConnection = getCIConnection(record);
                    result.getJsonArray().add(ciConnection);
                }

                transaction.success();
                result.setResult(1);
                result.setInfo("CI连接关系查询成功");
            }
        }

        return result;
    }

    /**
     * 新增CI类型之间的连接关联关系
     * @param jsonObject CI类型信息及连接信息
     * @return 添加结果
     */
    @Override
    public DBResult addCITypeConnectionRelationship(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型之间的连接关系新增失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI类型之间的连接关系新增失败，无输入参数");
            return result;
        }

        openDriver();

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
                    result.setInfo("CI类型之间的连接关系新增失败，无符合条件的CI类型");
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
                    result.setResult(1);
                    result.setInfo("CI类型之间的连接关系新增成功");
                } else {
                    transaction.failure();
                    result.setInfo("CI类型之间的连接关系新增失败，节点创建失败");
                }
            }
        }

        return result;
    }

    /**
     * 删除指定CI类型之间的连接关联关系
     * @param jsonObject CI类型信息及连接信息
     * @return 删除结果
     */
    @Override
    public DBResult deleteCITypeConnectionRelationship(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型之间的连接关系删除失败，未知错误");

        openDriver();

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
                    result.setResult(1);
                    result.setInfo("CI类型之间的连接关系删除成功");
                } else {
                    transaction.failure();
                    result.setInfo("CI类型之间的连接关系删除失败");
                }
            }
        }

        return result;
    }

    /**
     * 查询指定的两个CI类型之间存在的连接关联关系
     * @param jsonObject CI类型信息
     * @return 查询结果
     */
    @Override
    public DBResult searchCITypeConnectionRelationship(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI类型之间的连接关系查询失败，未知错误");
        result.setJsonArray(new JSONArray());

        if (jsonObject == null) {
            result.setInfo("CI类型之间的连接关系查询失败，无输入参数");
            return result;
        }

        openDriver();

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

                for (Record record : recordList) {
                    JSONObject ciConnection = new JSONObject();

                    ciConnection.put("name", record.values().get(0).asString());
                    ciConnection.put("type", record.values().get(1).get("type").asString());
                    ciConnection.put("correspondence", record.values().get(1).get("correspondence").asInt());

                    result.getJsonArray().add(ciConnection);
                }

                result.setResult(1);
                result.setInfo("CI类型之间的连接关系查询成功");
            }
        }

        return result;
    }

    /**
     * 新增CI属性信息
     * @param jsonObject CI属性信息
     * @return 新增结果
     */
    @Override
    public DBResult addCIProp(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI属性新增失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI属性新增失败，无输入参数");
            return result;
        }

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                String name = jsonObject.getString("name");
                JSONObject prop = jsonObject.getJSONObject("prop");
                String businessCode = getBusinessCode(prop);

                String search = "match (prop:" + Neo4jDBNodeType.CIProp + " {businessCode:{BusinessCode}}), " +
                        "(type:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) " +
                        "where (prop)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type) " +
                        "return prop";

                StatementResult statementResult = transaction.run(search,
                        Values.parameters("BusinessCode", businessCode, "Name", name));
                List<Record> recordList = statementResult.list();
                if (recordList.size() > 0) {
                    result.setInfo("CI属性新增失败，已存在CI属性节点");
                    return result;
                }

                JSONArray names = prop.getJSONArray("name");
                List<String> nameList = names.toJavaList(String.class);
                if (isCIPropRepeat(transaction, businessCode, name, nameList)) {
                    transaction.failure();
                    result.setInfo("CI属性新增失败，存在相同CI属性名称");
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
                    result.setResult(1);
                    result.setInfo("CI属性新增成功");
                } else {
                    transaction.failure();
                    result.setInfo("CI属性新增失败，节点创建失败");
                }
            }
        }

        return result;
    }

    /**
     * 删除指定的CI属性
     * @param jsonObject CI属性信息
     * @return 删除结果
     */
    @Override
    public DBResult deleteCIProp(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI属性删除失败，未知错误");

        if (jsonObject == null) {
            return result;
        }

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                String name = jsonObject.getString("name");
                String businessCode = getBusinessCode(jsonObject);

                String cmd = "match (prop:" + Neo4jDBNodeType.CIProp + " {businessCode:{BusinessCode}}), " +
                        "(type:" + Neo4jDBNodeType.CIType + " {name:{Name}, level:3}) " +
                        "where (prop)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type) " +
                        "DETACH DELETE prop";
                StatementResult statementResult = transaction.run(cmd,
                        Values.parameters("BusinessCode", businessCode, "Name", name));
                ResultSummary summary = statementResult.summary();

                if (summary.counters().nodesDeleted() == 1) {
                    transaction.success();
                    result.setResult(1);
                    result.setInfo("CI属性删除成功");
                } else {
                    transaction.failure();
                    result.setInfo("CI属性删除失败，节点删除失败");
                }
            }
        }

        return result;
    }

    /**
     * 修改指定的CI属性
     * @param jsonObject CI属性信息
     * @return 修改结果
     */
    @Override
    public DBResult modifyCIProp(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI属性修改失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI属性修改失败，无输入参数");
            return result;
        }

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                String name = jsonObject.getString("name");
                JSONObject prop = jsonObject.getJSONObject("prop");
                String businessCode = getBusinessCode(prop);

                List<String> nameList = prop.getJSONArray("name").toJavaList(String.class);
                if (isCIPropRepeat(transaction, businessCode, name, nameList)) {
                    transaction.failure();
                    result.setInfo("CI属性修改失败，存在相同CI属性名称");
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
                    result.setResult(true);
                    result.setInfo("CI属性修改成功");
                } else {
                    transaction.failure();
                    result.setInfo("CI属性修改失败，节点信息修改失败");
                }
            }
        }

        return result;
    }

    /**
     * 查询指定CI类型的CI属性
     * @param jsonObject CI类型信息
     * @return 查询结果
     */
    @Override
    public DBResult searchCIProp(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI属性查询失败，未知错误");
        result.setJsonObject(new JSONObject());

        if (jsonObject == null) {
            result.setInfo("CI属性查询失败，无输入参数");
            return result;
        }

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                String name = jsonObject.getString("name");
                String businessCode = getBusinessCode(jsonObject);

                String cmd = "match (props:" + Neo4jDBNodeType.CIProp + "), " +
                        "(type:" + Neo4jDBNodeType.CIType + " {name:{Name}}) " +
                        "where (props)-[:" + Neo4jDBRelationshipType.ATTACH + "]->(type) " +
                        "return props";
                StatementResult statementResult = transaction.run(cmd,
                        Values.parameters("Name", name));
                List<Record> recordList = statementResult.list();

                JSONArray jsonArray = new JSONArray();
                result.getJsonObject().put("name", name);
                result.getJsonObject().put("props", jsonArray);
                for (Record record : recordList) {
                    JSONObject prop = getCIProp(record);

                    if (!prop.containsKey("businessCode") || businessCode.equals("")) {
                        jsonArray.add(prop);
                    } else if (prop.getString("businessCode").equals(businessCode)) {
                        jsonArray.add(prop);
                    }
                }

                transaction.success();
                result.setResult(1);
                result.setInfo("CI属性查询成功");
            }
        }

        return result;
    }

    /**
     * 新增CI信息
     * @param jsonObject CI信息
     * @return 新增结果
     */
    @Override
    public DBResult addCI(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI信息新增失败，未知错误");
        result.setJsonObject(new JSONObject());

        if (jsonObject == null) {
            result.setInfo("CI信息新增失败，无输入参数");
            return result;
        }

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                String name = jsonObject.getString("type");
                String enterpriseCode = jsonObject.getString("enterpriseCode");
                String serverCode = jsonObject.getString("serverCode");
                String businessCode = getBusinessCode(jsonObject);

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
                    result.setInfo("CI信息新增失败，未找到对应CI类型信息");
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
                    result.setInfo("CI信息新增失败，未找到对应CI属性信息");
                    return result;
                }

                StringBuilder propStr = new StringBuilder();

                for (Record record:recordList) {
                    JSONObject prop = getCIProp(record);

                    JSONArray nameArray = prop.getJSONArray("name");
                    JSONArray typeArray = prop.getJSONArray("type");
                    for (int i = 0; i < nameArray.size(); ++i) {
                        String propName = nameArray.getString(i);
                        String propType = typeArray.getString(i);

                        if (jsonObject.containsKey(propName)) {
                            String value = getTypeValue(propType, propName, jsonObject);
                            if (value.isEmpty()) {
                                result.setInfo("CI信息新增失败，未找到" + propName + "属性类型:" + propType);
                                return result;
                            }
                            propStr.append(",").append(propName).append(":").append(value);
                        }
                    }
                }

                if (!jsonObject.containsKey("address")) {
                    propStr.append(",address:'000000'");
                }
                if (!jsonObject.containsKey("status")) {
                    propStr.append(",status:true");
                }

                String sn = jsonObject.getString("sn");
                String ciTypeCode = Neo4jUtils.getCITypeCode(code1, code2, code3);
                String ciId = Neo4jUtils.getCIId(ciTypeCode, sn, enterpriseCode, serverCode);

                try {
                    cmd = "create (ci:" + Neo4jDBNodeType.CI + " {id:{Id}" + propStr + "}) return ci.id";

                    statementResult = transaction.run(cmd, Values.parameters("Id", ciId,
                            "BusinessCode", businessCode));
                    ResultSummary summary = statementResult.summary();

                    if (summary.counters().nodesCreated() != 1) {
                        transaction.failure();
                        result.setInfo("CI信息新增失败，创建节点失败");
                        return result;
                    }
                } catch (ClientException e) {
                    transaction.failure();
                    if (e.getMessage().contains("already exists with label `CI` and property `id`")) {
                        result.setInfo("CI信息新增失败，序列号重复");
                        return result;
                    } else {
                        throw e;
                    }
                }

                recordList = statementResult.list();
                result.getJsonObject().put("id", recordList.get(0).values().get(0).asString());
                result.getJsonObject().put("sn", sn);

                transaction.success();
                result.setResult(1);
                result.setInfo("CI信息新增成功");
            }
        }

        return result;
    }

    /**
     * 删除CI信息
     * @param jsonObject CI信息
     * @return 删除结果
     */
    @Override
    public DBResult deleteCI(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI信息删除失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI信息删除失败，无输入参数");
            return result;
        }

        openDriver();

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
                    result.setInfo("CI信息删除成功");
                    result.setResult(1);
                } else {
                    transaction.failure();
                    result.setInfo("CI信息删除失败，删除节点失败");
                }
            }
        }

        return result;
    }

    /**
     * 修改CI信息
     * @param jsonObject CI信息
     * @return 修改结果
     */
    @Override
    public DBResult modifyCI(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI信息修改失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI信息修改失败，无输入参数");
            return result;
        }

        openDriver();

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

                StringBuilder propStr = new StringBuilder();
                int count = 0;
                for (int i = 0; i < nameArray.size(); ++i) {
                    String propName = nameArray.getString(i);
                    String propType = typeArray.getString(i);

                    if (isCIPropReadonly(propName)) {
                        continue;
                    }

                    if (jsonObject.containsKey(propName)) {
                        count++;
                        String value = getTypeValue(propType, propName, jsonObject);
                        if (value.isEmpty()) {
                            result.setInfo("CI信息修改失败，，未找到" + propName + "属性类型:" + propType);
                            return result;
                        }
                        propStr.append("ci.").append(propName).append(" = ").append(value).append(",");
                    }
                }

                if (count > 0) {
                    cmd = "match (ci:" + Neo4jDBNodeType.CI + " {id:{Id}}) set " + propStr.substring(0, propStr.length() - 1);
                    statementResult = transaction.run(cmd, Values.parameters("Id", id));
                    if (statementResult.summary().counters().propertiesSet() != count) {
                        transaction.failure();
                        result.setInfo("CI信息修改失败，节点信息修改失败");
                        return result;
                    }
                }

                transaction.success();
                result.setResult(1);
                result.setInfo("CI信息修改成功");
            }
        }

        return result;
    }

    /**
     * 查询CI信息
     * @param jsonObject 查询条件
     * @return 查询结果
     */
    @Override
    public DBResult searchCI(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI信息查询失败，未知错误");
        result.setJsonArray(new JSONArray());

        if (jsonObject == null) {
            result.setInfo("CI信息查询失败，无输入参数");
            return result;
        }

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                String businessCode = getBusinessCode(jsonObject);

                List<String> conditionList = new ArrayList<>();
                conditionList.add("ci.id is not null");
                if (jsonObject.containsKey("enterpriseCode") && jsonObject.containsKey("serverCode")) {
                    String enterpriseCode = jsonObject.getString("enterpriseCode");
                    String serverCode = jsonObject.getString("serverCode");

                    conditionList.add("ci.enterpriseCode = '" + enterpriseCode + "'");
                    conditionList.add("ci.serverCode = '" + serverCode + "'");
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

                StringBuilder condition = new StringBuilder();
                if (conditionList.size() > 0) {
                    condition = new StringBuilder("where " + conditionList.get(0));
                    for (int i = 1; i < conditionList.size(); ++i) {
                        condition.append(" and ").append(conditionList.get(i));
                    }
                }

                String cmd = "match (ci:" + Neo4jDBNodeType.CI + ") " + condition;
                StatementResult statementResult = transaction.run(cmd + " return count(ci)");
                List<Record> recordList = statementResult.list();

                int count = recordList.get(0).values().get(0).asInt();

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

                    result.getJsonArray().add(ci);
                }

                result.setCount(count);
                result.setResult(1);
                result.setInfo("CI信息查询成功");
            }
        }

        return result;
    }

    /**
     * 查询CI信息
     * @param jsonObject 查询条件
     * @return 查询结果
     */
    @Override
    public DBResult searchCI_V2(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI信息查询失败，未知错误");
        result.setJsonArray(new JSONArray());

        if (jsonObject == null) {
            result.setInfo("CI信息查询失败，无输入参数");
            return result;
        }

        openDriver();

        try (Session session = driver.session()) {
            try (Transaction transaction = session.beginTransaction()) {

                List<String> conditionList = new ArrayList<>();
                conditionList.add("ci.id is not null");

                String parent = "", page = "";
                for (String propName : jsonObject.keySet()) {
                    if (propName.equals("_parent")) {
                        if (jsonObject.getJSONObject("_parent").keySet().size() > 0) {
                            parent = "<-[:" + Neo4jDBRelationshipType.RELATIONSHIP + "]-(parent:CI)";
                        }
                        for (String parentPropName : jsonObject.getJSONObject("_parent").keySet()) {
                            conditionList.add("parent." + getCondition(parentPropName, jsonObject.getJSONObject("_parent").getJSONObject(parentPropName)));
                        }
                    } else if (propName.equals("_page")) {
                        int curPage = jsonObject.getJSONObject("_page").getJSONObject("curPage").getInteger("value");
                        int pageNum = jsonObject.getJSONObject("_page").getJSONObject("pageNum").getInteger("value");
                        page = " skip " + (curPage - 1) * pageNum + " limit " + pageNum;
                    } else {
                        conditionList.add("ci." + getCondition(propName, jsonObject.getJSONObject(propName)));
                    }
                }

                StringBuilder condition = new StringBuilder();
                if (conditionList.size() > 0) {
                    condition = new StringBuilder(" where " + conditionList.get(0));
                    for (int i = 1; i < conditionList.size(); ++i) {
                        condition.append(" and ").append(conditionList.get(i));
                    }
                }

                String cmd = "match (ci:" + Neo4jDBNodeType.CI + ")" + parent + condition;
                StatementResult statementResult = transaction.run(cmd + " return count(distinct (ci))");
                List<Record> recordList = statementResult.list();

                int count = recordList.get(0).values().get(0).asInt();

                cmd = "match (ci:" + Neo4jDBNodeType.CI + ")" + parent + condition +
                        " return distinct (ci) " + (parent.equals("") ? "" : ", parent") + " order by id(ci)" + page;
                statementResult = transaction.run(cmd);
                recordList = statementResult.list();

                HashMap<String, JSONObject> propMap = new HashMap<>();

                for (Record record:recordList) {
                    String type = record.values().get(0).get("type").asString();
                    String enterpriseCode = record.values().get(0).get("enterpriseCode").asString();
                    String serverCode = record.values().get(0).get("serverCode").asString();
                    String businessCode = Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);

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

                    if (!parent.equals("")) {
                        String parentType = record.values().get(1).get("type").asString();
                        String parentSn = record.values().get(1).get("sn").asString();

                        JSONObject jsonParent = new JSONObject();
                        jsonParent.put("type", parentType);
                        jsonParent.put("sn", parentSn);
                        ci.put("_parent", jsonParent);
                    }

                    result.getJsonArray().add(ci);
                }

                result.setCount(count);
                result.setResult(1);
                result.setInfo("CI信息查询成功");
            }
        }

        return result;
    }

    /**
     * 添加CI信息关联关系
     * @param jsonObject CI信息
     * @return 添加结果
     */
    @Override
    public DBResult addCIRelationship(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI关系添加失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI关系添加失败，无输入参数");
            return result;
        }

        openDriver();

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
                    result.setInfo("CI关系添加失败，未找到所属CI类型的连接关系");
                    return result;
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
                    result.setInfo("CI关系添加失败，连接关系已存在");
                    return result;
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
                        result.setInfo("CI关系添加失败，指定CI已与其他CI连接");
                        return result;
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
                    result.setInfo("CI关系添加失败，连接关系创建失败");
                    return result;
                }

                cmd = "match (ci1:" + Neo4jDBNodeType.CI + " {id:{Id1}})" +
                        "-[r:" + Neo4jDBRelationshipType.RELATIONSHIP + " {type:{Type}, status:false}]->" +
                        "(ci2:" + Neo4jDBNodeType.CI + " {id:{Id2}}) " +
                        "delete r";
                transaction.run(cmd, Values.parameters("Id1", id1,
                        "Type", relationshipType,
                        "Id2", id2));

                transaction.success();
                result.setResult(1);
                result.setInfo("CI关系添加成功");
            }
        }

        return result;
    }

    /**
     * 查询指定CI项的连接CI信息
     * @param jsonObject 查询条件
     * @return 连接CI信息
     */
    @Override
    public DBResult searchCIRelationship(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI关系查询失败，未知错误");
        result.setJsonObject(new JSONObject());

        if (jsonObject == null) {
            result.setInfo("CI关系查询失败，无输入参数");
            return result;
        }

        openDriver();

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

                result.getJsonObject().put("id", id);
                result.getJsonObject().put("parent", parent);
                result.getJsonObject().put("children", children);

                transaction.success();
                result.setResult(1);
                result.setInfo("CI关系查询成功");
            }
        }

        return result;
    }

    /**
     * 删除CI信息关联关系
     * @param jsonObject CI信息
     * @return 删除结果
     */
    @Override
    public DBResult deleteCIRelationship(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI关系删除失败，未知错误");

        if (jsonObject == null) {
            result.setInfo("CI关系删除失败，无输入参数");
            return result;
        }

        openDriver();

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
                    result.setInfo("CI关系删除成功");
                    result.setResult(1);
                } else {
                    transaction.failure();
                    result.setInfo("CI关系删除失败，节点状态修改失败");
                }
            }
        }

        return result;
    }

    /**
     * 查询CI设备型号信息
     * @param jsonObject 业务和服务信息
     * @return 查询结果
     */
    @Override
    public DBResult searchCIModel(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI设备型号查询失败，未知错误");
        result.setJsonArray(new JSONArray());

        if (jsonObject == null) {
            result.setInfo("CI设备型号查询失败，无输入参数");
            return result;
        }

        openDriver();

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
                    result.getJsonArray().add(model);
                }

                transaction.success();
                result.setResult(1);
                result.setInfo("CI设备型号查询成功");
            }
        }

        return result;
    }

    /**
     * 根据企业编码、服务编码和地区编码列表查询CI项的id列表
     * @param jsonObject 查询信息
     * @return id列表
     */
    @Override
    public DBResult searchCIIds(JSONObject jsonObject) {

        DBResult result = new DBResult();
        result.setResult(0);
        result.setInfo("CI id查询失败，未知错误");
        result.setJsonArray(new JSONArray());

        if (jsonObject == null) {
            result.setInfo("CI id查询失败，无输入参数");
            return result;
        }

        openDriver();

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
                    result.getJsonArray().add(record.values().get(0).asString());
                }

                transaction.success();
                result.setResult(1);
                result.setInfo("CI id查询成功");
            }
        }

        return result;
    }

    /**
     * 打开数据库
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
        ciType.put("icon", record.values().get(0).get("icon").asString());

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
     * 在返回记录中获取CI类型的上下级类型信息
     * @param ciType CI类型
     * @param transaction 执行事务
     */
    private void getConnectedCIType(JSONObject ciType, Transaction transaction) {

        JSONArray children = new JSONArray();
        ciType.put("parent", null);
        ciType.put("children", children);

        String searchRelationship = "match (tmp:" + Neo4jDBNodeType.CIType + " {name:'" + ciType.getString("name") + "'})" +
                "-[:" + Neo4jDBRelationshipType.INCLUDE + "]-(item:" + Neo4jDBNodeType.CIType + ") " +
                "return item";
        StatementResult statementResult = transaction.run(searchRelationship);
        List<Record> relationshipCITypeList = statementResult.list();

        for (Record record : relationshipCITypeList) {
            JSONObject relationshipCIType = getCIType(record);
            if (relationshipCIType.getInteger("level") > ciType.getInteger("level")) {
                children.add(relationshipCIType);
            } else {
                ciType.put("parent", relationshipCIType);
            }
        }
    }

    /**
     * 从参数中获取BusinessCode
     * @param jsonObject 参数
     * @return BusinessCode
     */
    private String getBusinessCode(JSONObject jsonObject) {
        if (jsonObject.containsKey("enterpriseCode") && jsonObject.containsKey("serverCode")) {
            String enterpriseCode = jsonObject.getString("enterpriseCode");
            String serverCode = jsonObject.getString("serverCode");
            return Neo4jUtils.getBusinessCode(enterpriseCode, serverCode);
        }
        return "";
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

        //保留字段
        ArrayList<String> reservedField = new ArrayList<>();
        reservedField.add("parent");    //parent字段在getCI报文中用于表示上级CI，故无法当做属性名称使用

        reservedField.retainAll(nameList);
        if (reservedField.size() > 0) {
            return true;
        }

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
            List<String> tmp = new ArrayList<>(record.values().get(0).asList(org.neo4j.driver.v1.Value::asString));
            tmp.retainAll(nameList);
            if (tmp.size() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否为只读CI属性
     * @param propName 待检测CI属性
     * @return 检测结果，true是只读属性，false不是只读属性
     */
    private boolean isCIPropReadonly(String propName) {

        //只读属性名称
        ArrayList<String> readonlyFields = new ArrayList<>();
        readonlyFields.add("id");
        readonlyFields.add("enterpriseCode");
        readonlyFields.add("serverCode");
        readonlyFields.add("type");
        readonlyFields.add("sn");
        readonlyFields.add("user");
        readonlyFields.add("createTime");

        return readonlyFields.contains(propName);
    }

    /**
     * 解析搜索条件
     * @param propName 属性名称
     * @param condition 输入条件
     * @return 查询语句
     */
    private String getCondition(String propName, JSONObject condition) {

        String result = "";

        int searchType = condition.getInteger("searchType");
        String dataType = "string";
        if (condition.containsKey("dataType")) {
            dataType = condition.getString("dataType").toLowerCase();
        }
        switch (searchType) {
            case 0:
                // 模糊搜索，默认数据类型为String
                result = propName + " =~ '.*" + condition.getString("value") + ".*'";
                break;
            case 1:
                // 精确搜索，需通过dataType字段确认数据类型，若该字段不存在，则认为是String
                switch (dataType) {
                    case "string":
                        result = propName + " = '" + condition.getString("value") + "'";
                        break;
                    case "bool":
                    case "boolean":
                        result = propName + " = " + condition.getBoolean("value");
                        break;
                    case "number":
                        result = propName + " = " + condition.getDouble("value");
                        break;
                    case "datetime":
                    case "time":
                        result = propName + " = " + condition.getLong("value");
                        break;
                }
                break;
            case 2:
                // in搜索，需通过dataType字段确认数据类型，若该字段不存在，则认为是String
                result = propName + " in " + JSONObject.toJSONString(condition.getJSONArray("value"));
                break;
        }

        return result;
    }

    /**
     * 获取指定类型的参数(该方法仅用于cypher语句的拼接)
     * @param type 参数类型
     * @param name 参数名称
     * @param jsonObject 参数
     * @return 参数
     */
    private String getTypeValue(String type, String name, JSONObject jsonObject) {

        type = type.toLowerCase();
        switch (type) {
            case "string":
                return "'" + jsonObject.getString(name) + "'";
            case "number":
                return jsonObject.getDouble(name).toString();
            case "bool":
            case "boolean":
                return jsonObject.getBoolean(name).toString();
            case "datetime":
            case "time":
                return jsonObject.getLong(name).toString();
            default:
                return "";
        }
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

            switch (types.getString(index).toLowerCase()) {
                case "string":
                    result.put(key, record.values().get(0).get(key).asString());
                    break;
                case "number":
                    result.put(key, record.values().get(0).get(key).asDouble());
                    break;
                case "bool":
                case "boolean":
                    result.put(key, record.values().get(0).get(key).asBoolean());
                    break;
                case "datetime":
                case "time":
                    result.put(key, record.values().get(0).get(key).asLong());
                    break;
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
