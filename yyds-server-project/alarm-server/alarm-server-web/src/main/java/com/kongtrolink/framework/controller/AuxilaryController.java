package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.Auxilary;
import com.kongtrolink.framework.query.AuxilaryQuery;
import com.kongtrolink.framework.service.AuxilaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 10:47
 * @Description:辅助
 */
@Controller
@RequestMapping("/auxilaryController")
public class AuxilaryController extends BaseController{

    @Autowired
    AuxilaryService auxilaryService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(Auxilary auxilary){
        auxilaryService.save(auxilary);
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(AuxilaryQuery auxilaryQuery){
        boolean delete = auxilaryService.delete(auxilaryQuery.get_id());
        if(delete){
            return new JsonResult(Contant.DELETED + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.DELETED + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonResult update(Auxilary auxilary){
        boolean update = auxilaryService.update(auxilary);
        if(update){
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(AuxilaryQuery auxilaryQuery){
        List<Auxilary> list = auxilaryService.list(auxilaryQuery);
        int count = auxilaryService.count(auxilaryQuery);
        ListResult<Auxilary> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }
}
