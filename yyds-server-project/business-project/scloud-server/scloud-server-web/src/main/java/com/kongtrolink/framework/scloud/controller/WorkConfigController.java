package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.WorkConfig;
import com.kongtrolink.framework.scloud.query.WorkConfigQuery;
import com.kongtrolink.framework.scloud.service.WorkConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/1 10:28
 * @Description: 告警工单配置控制层
 */
@Controller
@RequestMapping("/workConfig")
public class WorkConfigController extends BaseController {

    @Autowired
    WorkConfigService workConfigService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(@RequestBody WorkConfig workConfig){
        String uniqueCode = getUniqueCode();
        workConfig.initIntTime();
        workConfigService.add(uniqueCode, workConfig);
        return new JsonResult("添加成功", true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody WorkConfigQuery workConfigQuery){
        String uniqueCode = getUniqueCode();
        boolean delete = workConfigService.delete(uniqueCode, workConfigQuery.getId());
        if(delete){
            return new JsonResult("删除成功", true);
        }
        return new JsonResult("删除失败", false);
    }

    @RequestMapping("/udpate")
    @ResponseBody
    public JsonResult update(@RequestBody WorkConfig workConfig){
        String uniqueCode = getUniqueCode();
        boolean update = workConfigService.update(uniqueCode, workConfig);
        if(update){
            return new JsonResult("修改成功", true);
        }
        return new JsonResult("修改失败", false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody WorkConfigQuery workConfigQuery){
        String uniqueCode = getUniqueCode();
        List<WorkConfig> list = workConfigService.list(uniqueCode, workConfigQuery);
        int count = workConfigService.count(uniqueCode, workConfigQuery);
        return new JsonResult(list, count);
    }
}
