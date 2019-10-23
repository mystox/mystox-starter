package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.core.entity.session.BaseController;
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
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 10:47
 * @Description:辅助
 */
@Controller
@RequestMapping("/auxilaryController")
public class AuxilaryController extends BaseController {

    @Autowired
    AuxilaryService auxilaryService;

    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(AuxilaryQuery auxilaryQuery){
        String enterpriseCode = auxilaryQuery.getEnterpriseCode();
        String serverCode = auxilaryQuery.getServerCode();
        Auxilary sourceAuxilary = auxilaryService.getByEnterServerCode(enterpriseCode, serverCode);
        if(null == sourceAuxilary){
            sourceAuxilary = new Auxilary();
            sourceAuxilary.setEnterpriseCode(enterpriseCode);
            sourceAuxilary.setServerCode(serverCode);
        }
        Map<String, String> proMap = sourceAuxilary.getProMap();
        proMap.put(auxilaryQuery.getProStr(), auxilaryQuery.getProName());
        auxilaryService.delete(sourceAuxilary.get_id());
        auxilaryService.save(sourceAuxilary);
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(AuxilaryQuery auxilaryQuery){
        String enterpriseCode = auxilaryQuery.getEnterpriseCode();
        String serverCode = auxilaryQuery.getServerCode();
        Auxilary auxilary = auxilaryService.getByEnterServerCode(enterpriseCode, serverCode);
        if(null == auxilary){
            return new JsonResult(Contant.DELETED + Contant.RESULT_FAIL, false);
        }
        Map<String, String> proMap = auxilary.getProMap();
        proMap.remove(auxilaryQuery.getProStr());
        boolean result;
        if(proMap.isEmpty()){
            //如果没有任何附加属性，则直接删除该记录
            result = auxilaryService.delete(auxilary.get_id());
        }else{
            result = auxilaryService.update(auxilary);
        }
        if(result){
            return new JsonResult(Contant.DELETED + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.DELETED + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/get")
    @ResponseBody
    public JsonResult get(AuxilaryQuery auxilaryQuery){
        Auxilary auxilary = auxilaryService.getByEnterServerCode(auxilaryQuery.getEnterpriseCode(), auxilaryQuery.getServerCode());
        return new JsonResult(auxilary);
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
