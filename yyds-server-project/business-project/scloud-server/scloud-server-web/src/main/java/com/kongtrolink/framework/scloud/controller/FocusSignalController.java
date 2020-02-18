package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.entity.FocusSignalEntity;
import com.kongtrolink.framework.scloud.query.FocusSignalQuery;
import com.kongtrolink.framework.scloud.service.FocusSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "/focusSignal", method = RequestMethod.POST)
public class FocusSignalController {

    @Autowired
    FocusSignalService focusSignalService;
    /**
     * 保存信息
     */
    @RequestMapping(value = "/save")
    public @ResponseBody JsonResult  saveFocusSignal(@RequestBody FocusSignalEntity focusSignalEntity) {
       try{
           focusSignalService.saveFocusSignal(focusSignalEntity);
           return new JsonResult("保存成功");
       }catch (Exception e){
           e.printStackTrace();
       }
        return new JsonResult("保存失败",false);
    }

    /**
     * 根据登录用户 取得关注点列表 - 分页
     *
     * @param focusSignalQuery 查询条件
     * @return 列表
     */
    @RequestMapping(value = "/getList")
    public @ResponseBody JsonResult  getList(@RequestBody FocusSignalQuery focusSignalQuery) {
       try{
            List<FocusSignalEntity> list = focusSignalService.getList(focusSignalQuery);
            int count = focusSignalService.getListCount(focusSignalQuery);
            ListResult value = new ListResult(list,count);
            return new JsonResult(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);
    }


    /**
     * 删除 关注点
     *
     */
    @RequestMapping(value = "/del")
    public @ResponseBody JsonResult  delFocusSignal(@RequestBody FocusSignalQuery focusSignalQuery) {
        try{
            focusSignalService.delFocusSignal(focusSignalQuery.getId());
            return new JsonResult("取消关注成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("取消关注失败",false);
    }
}
