package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.core.entity.session.WebPageInfo;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.UserSiteEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.query.UserQuery;
import com.kongtrolink.framework.scloud.service.UserExcelService;
import com.kongtrolink.framework.scloud.service.UserService;
import com.kongtrolink.framework.scloud.util.ExcelUtil;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.kongtrolink.framework.scloud.controller.base.ExportController.export;

/**
 * 系统管理-用户管理-系统用户 控制器
 * Created by Eric on 2020/2/28.
 */
@RestController
@RequestMapping(value = "/user/", method = RequestMethod.POST)
public class UserController extends BaseController{

    @Autowired
    UserService userService;
    @Autowired
    UserExcelService userExcelService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /**
     * 增加系统用户
     */
    @RequestMapping(value = "addUser",method = RequestMethod.POST)
    public @ResponseBody JsonResult addUser(@RequestBody UserModel userModel){
        try {
            String uniqueCode = getUniqueCode();
//            uniqueCode = "YYDS";
            return userService.addUser(uniqueCode,userModel);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("添加失败",false);
        }
    }

    /**
     * 修改系统用户
     */
    @RequestMapping(value = "modifyUser",method = RequestMethod.POST)
    public @ResponseBody JsonResult modifyUser(@RequestBody UserModel userModel){
        boolean modifyResult = userService.modifyUser(getUniqueCode(),userModel);
        try {
            if (modifyResult){
                return new JsonResult("修改成功",true);
            }else {
                return new JsonResult("修改失败",false);
            }
        }catch (Exception e){
            return new JsonResult("修改异常",false);
        }
    }

    /**
     * 删除系统用户
     */
    @RequestMapping(value = "deleteUser",method = RequestMethod.POST)
    public @ResponseBody JsonResult deleteUser(@RequestBody UserModel userModel){
        try {
            if (userModel.getUserId() != null && userModel.getUserId() != ""){
                userService.deleteUser(getUniqueCode(),userModel);
                return new JsonResult("删除成功",true);
            }else {
                return new JsonResult("删除失败",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("删除失败",true);
        }
    }

    /**
     * 用户列表
     */
    @RequestMapping(value = "listUser", method = RequestMethod.POST)
    public  @ResponseBody JsonResult listUser(@RequestBody UserQuery userQuery){

        String uniqueCode = getUniqueCode();
//        uniqueCode = "YYDS";
        WebPageInfo currentService = getCurrentService();
        String serverCode = currentService.getServerCode();
        List<JSONObject> userResult = userService.listUser(uniqueCode,userQuery,serverCode);
        return new JsonResult(userResult);
    }
    /**
     * 导出系统用户
     */
    @RequestMapping(value = "exportUserList",method = RequestMethod.POST)
    public void exportUserList(@RequestBody UserQuery userQuery,HttpServletResponse response){
        try {
            List<JSONObject> userList = userService.listUser(getUniqueCode(),userQuery,"");
            List<UserModel> result = new ArrayList<>();
            for (JSONObject list:userList){
                UserModel user = JSON.toJavaObject(list,UserModel.class);
                result.add(user);
            }
            HSSFWorkbook workbook = userService.exportUserList(userList);
            export(response,workbook,"系统用户列表");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 批量导入系统用户
     */
    @RequestMapping(value = "importUserList", method = RequestMethod.POST)
    public @ResponseBody JsonResult importUserList(@RequestParam("file") MultipartFile file) throws IOException, ParseException {
        CommonsMultipartFile cmf = (CommonsMultipartFile) file;
        DiskFileItem dfi = (DiskFileItem) cmf.getFileItem();
        File f = dfi.getStoreLocation();
        List<UserModel> list = null;
        try {
            list = userExcelService.read(f, getUniqueCode());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new JsonResult(e.getMessage(), false);
        }
        if (list == null || list.size() <= 0) {
            return new JsonResult("导入失败",false);
        }
        return userService.importUserList(getUniqueCode(),list);
    }
    /**
     * 批量删除系统用户
     */
    @RequestMapping(value = "deleteUserList",method = RequestMethod.POST)
    public @ResponseBody JsonResult deleteUserList(@RequestBody UserQuery userQuery){
        try {
            List<String> ids = userQuery.getUserIds();
            if (ids != null){
                for (String id:ids){
                    UserModel user = new UserModel();
                    user.setUserId(id);
                    userService.deleteUser(getUniqueCode(),user);
                }
            }
            return new JsonResult("删除成功",true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("删除失败",false);
        }
    }

    /**
     * 修改系统用户或维护用户 管辖站点
     */
    @RequestMapping(value = "modifyUserSite", method = RequestMethod.POST)
    public @ResponseBody JsonResult modifyUserSite(@RequestBody List<UserSiteEntity> userSiteEntityList){
        try{
//            String uniqueCode = getUniqueCode();
            userService.modifyUserSite(getUniqueCode(), userSiteEntityList);
            return new JsonResult("修改管辖站点成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("修改管辖站点失败", false);
        }
    }

    /**
     * 获取系统用户或维护用户 管辖站点
     */
    @RequestMapping(value = "getUserSite", method = RequestMethod.POST)
    public @ResponseBody JsonResult getUserSite(@RequestBody UserSiteEntity userSiteEntity){
        try{
//            String uniqueCode = getUniqueCode();
            String userId = userSiteEntity.getUserId();
            List<UserSiteEntity> list = userService.getUserSite(getUniqueCode(), userId);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取管辖站点失败", false);
        }
    }

    //-----------------[Warning]获取、添加、修改、删除系统用户 目前前端直接调取云管接口，以下接口暂时注释掉-----------------

    /*
    //获取系统用户列表
    @RequestMapping(value = "getUserList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getUserList(@RequestBody UserQuery userQuery){
        try{
            //String uniqueCode = getUniqueCode();
            List<UserModel> list = new ArrayList<>();
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取系统用户列表异常", false);
        }
    }

    //添加系统用户
    @RequestMapping(value = "addUser", method = RequestMethod.POST)
    public @ResponseBody JsonResult addUser(@RequestBody UserModel userModel){
        try{
            //String uniqueCode = getUniqueCode();

            return new JsonResult("添加成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("添加失败", false);
        }
    }


    //修改系统用户
    @RequestMapping(value = "modifyUser", method = RequestMethod.POST)
    public @ResponseBody JsonResult modifyUser(@RequestBody UserModel userModel){
        try{
            //String uniqueCode = getUniqueCode();

            return new JsonResult("修改用户信息成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("修改用户信息失败", false);
        }
    }


    //删除系统用户
    @RequestMapping(value = "deleteUser", method = RequestMethod.POST)
    public @ResponseBody JsonResult deleteUser(@RequestBody UserEntity userEntity){
        try{
            //String uniqueCode = getUniqueCode();

            return new JsonResult("删除成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("删除失败", false);
        }
    }
    */

}
