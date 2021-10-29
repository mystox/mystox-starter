package tech.mystox.framework.foo.service.api.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.stereotype.Service;
import tech.mystox.framework.api.test.EntityService;
import tech.mystox.framework.api.test.entity.OperaParam;
import tech.mystox.framework.api.test.entity.ReturnEntity;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mystox on 2021/6/28, 15:32.
 * company:
 * description:
 * update record:
 */
@Service
public class EntityServiceImpl implements EntityService {

    @Override
    public ReturnEntity getEntity(OperaParam operaParam) {
        System.out.println(JSONObject.toJSONString(operaParam));
        ReturnEntity returnEntity = new ReturnEntity();
        returnEntity.setResult("11111111111");
        returnEntity.setCode(11);
        return returnEntity;
    }

    @Override
    public List<ReturnEntity> getEntityList(List<OperaParam> operaParam) {
        System.out.println("getEntityList: "+JSONObject.toJSONString(operaParam));
        ReturnEntity returnEntity = new ReturnEntity();
        returnEntity.setResult("11111111111");
        returnEntity.setCode(11);
        ReturnEntity returnEntity2 = new ReturnEntity();
        returnEntity2.setResult("222222222");
        returnEntity2.setCode(22);
        List<ReturnEntity> arr = new ArrayList<>();
        arr.add(returnEntity2);
        returnEntity.setList(arr);
        List<ReturnEntity> arr2 = new ArrayList<>();
        arr2.add(returnEntity);
        returnEntity2.setList(arr2);
        List<ReturnEntity> result = new ArrayList<>();
        result.add(returnEntity);
        return result;
    }

    public static void main(String[] args) {
        ReturnEntity returnEntity = new ReturnEntity();
        returnEntity.setResult("11111111111");
//        returnEntity.setCode(11);
        ReturnEntity returnEntity2 = new ReturnEntity();
        returnEntity2.setResult("222222222");
        returnEntity2.setCode(22);
        List<ReturnEntity> arr = new ArrayList<>();
//        arr.add(returnEntity2);
        returnEntity.setList(arr);
        List<ReturnEntity> arr2 = new ArrayList<>();
        arr2.add(returnEntity);
//        returnEntity2.setList(arr2);
        List<ReturnEntity> result = new ArrayList<>();
        result.add(returnEntity);
//        result.add(returnEntity2);
        String resultStr = JSONObject.toJSONString(result);
        System.out.println(resultStr);
        String msg = "[{\"code\":11,\"list\":[{\"$ref\":\"..\"}],\"result\":\"11111111111\"},{\"$ref\":\"$[0]\"}]";
//        System.out.println(msg);
        msg = resultStr;
//        List<ReturnEntity> result = new ArrayList<ReturnEntity>();
        try {
            Object parse = JSON.parse(msg);
            List<ReturnEntity> returnEntities = JSONArray.parseArray(JSONObject.toJSONString(parse), ReturnEntity.class);
            System.out.println(returnEntities);
            Method getEntityList = EntityServiceImpl.class.getMethod("getEntityList", List.class);
            Type genericReturnType = getEntityList.getGenericReturnType();
            ParserConfig globalInstance = ParserConfig.getGlobalInstance();
//            globalInstance.putDeserializer(genericR|eturnType, SerializerFeature.DisableCircularReferenceDetect);
            Object cast = JSON.parseObject(msg, genericReturnType);
            //            Object cast = TypeUtils.cast(JSON.parseObject(msg,Object.class), genericReturnType, ParserConfig.getGlobalInstance());
            System.out.println(JSONObject.toJSONString(cast));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    /*public static void main(String[] args) {
        ReturnEntity returnEntity = new ReturnEntity();
        returnEntity.setResult("11111111111");
        returnEntity.setCode(11);
        returnEntity.setReturnEntity(returnEntity);
        List<ReturnEntity> arr = new ArrayList<>();
        arr.add(returnEntity);
        returnEntity.setList(arr);
        String resultStr = JSONObject.toJSONString(returnEntity);
        System.out.println(resultStr);
        String msg = "[{\"code\":11,\"list\":[{\"$ref\":\"..\"}],\"result\":\"11111111111\"},{\"$ref\":\"$[0]\"}]";
        //        System.out.println(msg);
        msg = resultStr;
        //        List<ReturnEntity> result = new ArrayList<ReturnEntity>();
        ReturnEntity x = JSONObject.parseObject(msg, ReturnEntity.class);
        System.out.println(x);

    }*/
}
