package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.query.AuxilaryQuery;
import com.kongtrolink.framework.scloud.service.AuxilaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Auther: liudd
 * @Date: 2020/4/3 09:31
 * @Description:
 */
@Controller
@RequestMapping("/auxilary")
public class AuxilaryController {

    @Autowired
    AuxilaryService auxilaryService;

    @RequestMapping("/get")
    @ResponseBody
    public JSONObject get(@RequestBody AuxilaryQuery auxilaryQuery){
        return auxilaryService.getByEnterServerCode(auxilaryQuery);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JSONObject delete(@RequestBody AuxilaryQuery auxilaryQuery){
        return auxilaryService.delete(auxilaryQuery);
    }
}
