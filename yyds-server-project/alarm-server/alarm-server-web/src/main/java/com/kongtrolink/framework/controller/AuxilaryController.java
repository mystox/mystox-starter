package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.enttiy.Auxilary;
import com.kongtrolink.framework.query.AuxilaryQuery;
import com.kongtrolink.framework.service.AuxilaryService;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
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
    public JsonResult add(@RequestBody AuxilaryQuery auxilaryQuery){
        String enterpriseCode = auxilaryQuery.getEnterpriseCode();
        String serverCode = auxilaryQuery.getServerCode();
        Auxilary sourceAuxilary = auxilaryService.getByEnterServerCode(enterpriseCode, serverCode);
        if(null == sourceAuxilary){
            sourceAuxilary = new Auxilary();
            sourceAuxilary.setEnterpriseCode(enterpriseCode);
            sourceAuxilary.setServerCode(serverCode);
        }
        String proStr = auxilaryQuery.getProStr();
        String proName = auxilaryQuery.getProName();
        List<String> proStrList = sourceAuxilary.getProStrList();
        List<String> proNameList = sourceAuxilary.getProNameList();
        if(proStrList.contains(proStr)){
            return new JsonResult(Contant.OPE_ADD + Contant.RESULT_FAIL + "，属性：" + proStr + "已存在!", false);
        }
        if(proNameList.contains(proName)){
            return new JsonResult(Contant.OPE_ADD + Contant.RESULT_FAIL + "，属性名称：" + proName + "已存在!", false);
        }
        proStrList.add(proStr);
        proNameList.add(proName);
        auxilaryService.delete(sourceAuxilary.get_id());
        auxilaryService.save(sourceAuxilary);
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody AuxilaryQuery auxilaryQuery){
        return auxilaryService.delete(auxilaryQuery);
    }

    @RequestMapping("/get")
    @ResponseBody
    public JsonResult get(@RequestBody AuxilaryQuery auxilaryQuery){
        Auxilary auxilary = auxilaryService.getByEnterServerCode(auxilaryQuery.getEnterpriseCode(), auxilaryQuery.getServerCode());
        return new JsonResult(auxilary);
    }

    @RequestMapping("/update")
    @ResponseBody
    public JsonResult update(@RequestBody Auxilary auxilary){
        boolean update = auxilaryService.update(auxilary);
        if(update){
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AuxilaryQuery auxilaryQuery){
        List<Auxilary> list = auxilaryService.list(auxilaryQuery);
        int count = auxilaryService.count(auxilaryQuery);
        ListResult<Auxilary> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }
}
