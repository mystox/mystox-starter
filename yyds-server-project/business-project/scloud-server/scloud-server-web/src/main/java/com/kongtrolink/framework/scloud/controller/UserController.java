package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.UserSiteEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.query.UserQuery;
import com.kongtrolink.framework.scloud.service.UserService;
import com.kongtrolink.framework.scloud.util.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统管理-用户管理-系统用户 控制器
 * Created by Eric on 2020/2/28.
 */
@Controller
@RequestMapping(value = "/user/", method = RequestMethod.POST)
public class UserController extends BaseController{

    @Autowired
    UserService userService;


    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /**
     * 增加系统用户
     */
    @RequestMapping(value = "addUser",method = RequestMethod.POST)
    public @ResponseBody JsonResult addUser(@RequestBody UserModel userModel){
        try {
            String uniqueCode = getUniqueCode();
            uniqueCode = "YYDS";
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
                return new JsonResult("删除失败",true);
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
        uniqueCode = "YYDS";
        List<JSONObject> userResult = userService.listUser(uniqueCode,userQuery);
        return new JsonResult(userResult);
    }
    /**
     * 导出系统用户
     */
    @RequestMapping(value = "exportUserList",method = RequestMethod.POST)
    public void exportUserList(@RequestBody UserQuery userQuery,HttpServletResponse response){
        try {
            List<JSONObject> userList = userService.listUser(getUniqueCode(),userQuery);
            List<UserModel> result = new ArrayList<>();
            for (JSONObject list:userList){
                UserModel user = JSON.toJavaObject(list,UserModel.class);
                result.add(user);
            }
            HSSFWorkbook workbook = userService.exportUserList(result);
            export(response,workbook,"系统用户列表");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 批量导入系统用户
     */
    @RequestMapping(value = "importUserList", method = RequestMethod.POST)
    public @ResponseBody JsonResult importUserList(@RequestBody MultipartFile file){
        if (!file.isEmpty()){
            try{
                //获取文件名
                String fileName = file.getOriginalFilename();
                //进一步判断文件是否为空（即判断其大小是否为0或其名称是否为null）验证文件名是否合格
                long size=file.getSize();
                if(fileName==null || ("").equals(fileName) && size==0 && !ExcelUtil.validateExcel(fileName)){
                    LOGGER.error("文件为空");
                }
                //获取输入流
                InputStream is = file.getInputStream();
                HSSFWorkbook workbook = new HSSFWorkbook(is);
                Sheet sheet = workbook.getSheetAt(0);
                //行数
                int rowNum = sheet.getPhysicalNumberOfRows();
                //列数
                int colNum = 0;
                if(rowNum>=1 && sheet.getRow(0) != null){//判断行数大于一
                    colNum = sheet.getRow(0).getPhysicalNumberOfCells();
                }else{
                    return null;
                }
                List<UserModel> list = new ArrayList<>();
                UserModel userModel = new UserModel();
                for (int i = 1;i < rowNum;i++){
                    Row row = sheet.getRow(i);
                    if (row == null){
                        continue;
                    }
                    for (int j = 0;j < colNum;j++){
                        Cell cell = row.getCell(j);
                        if (cell != null){
                            if (j == 0){
                                userModel.setUsername(cell.toString());
                            }else if (j == 1){
                                userModel.setCurrentRoleName(cell.toString());
                            }else if (j == 2){
                                userModel.setName(cell.toString());
                            }else if (j == 3){
                                userModel.setPhone(cell.toString());
                            }else if (j == 4){
                                userModel.setEmail(cell.toString());
                            }else if (j == 5){
                                userModel.setUserTime(cell.toString());
                            }else if (j == 6){
                                userModel.setValidTime(Long.parseLong(cell.toString()));
                            }
                            userModel.setPassword("123456");
                        }
                    }
                    list.add(userModel);
                    userService.addUser(getUniqueCode(),userModel);
                    return new JsonResult("导入成功",true);
                }
            }catch (Exception e){
                e.printStackTrace();
                return new JsonResult("导入失败", false);
            }
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
