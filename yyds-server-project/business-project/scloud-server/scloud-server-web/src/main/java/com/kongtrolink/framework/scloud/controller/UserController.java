package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.UserEntity;
import com.kongtrolink.framework.scloud.entity.UserSiteEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.query.UserQuery;
import com.kongtrolink.framework.scloud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
     * 获取系统用户列表
     */
    @RequestMapping(value = "getUserList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getUserList(@RequestBody UserQuery userQuery){
        try{
            String uniqueCode = getUniqueCode();
            List<UserModel> list = new ArrayList<>();
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取系统用户列表异常", false);
        }
    }

    /**
     * 导出用户列表
     */
    @RequestMapping(value = "exportUserList", method = RequestMethod.POST)
    public void exportUserList(@RequestBody UserQuery userQuery, HttpServletResponse response){
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加用户
     */
    @RequestMapping(value = "addUser", method = RequestMethod.POST)
    public @ResponseBody JsonResult addUser(@RequestBody UserModel userModel){
        try{
            String uniqueCode = getUniqueCode();
            return new JsonResult("添加成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("添加失败", false);
        }
    }

    /**
     * 批量导入用户
     */
    @RequestMapping(value = "importUserList", method = RequestMethod.POST)
    public @ResponseBody JsonResult importUserList(@RequestBody MultipartFile multipartFile){
        try{
            String uniqueCode = getUniqueCode();
            return new JsonResult(null);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("导入失败", false);
        }
    }

    /**
     * 修改用户
     */
    @RequestMapping(value = "modifyUser", method = RequestMethod.POST)
    public @ResponseBody JsonResult modifyUser(@RequestBody UserModel userModel){
        try{
            String uniqueCode = getUniqueCode();
            return new JsonResult("修改用户信息成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("修改用户信息失败", false);
        }
    }

    /**
     * 删除用户
     */
    @RequestMapping(value = "deleteUser", method = RequestMethod.POST)
    public @ResponseBody JsonResult deleteUser(@RequestBody UserEntity userEntity){
        try{
            String uniqueCode = getUniqueCode();

            return new JsonResult("删除成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("删除失败", false);
        }
    }

    /**
     * 修改用户管辖站点
     */
    @RequestMapping(value = "modifyUserSite", method = RequestMethod.POST)
    public @ResponseBody JsonResult modifyUserSite(@RequestBody UserSiteEntity userSiteEntity){
        try{
            String uniqueCode = getUniqueCode();
            return new JsonResult("修改成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("修改失败", false);
        }
    }

    /**
     * 获取用户管辖站点
     */
    @RequestMapping(value = "getUserSite", method = RequestMethod.POST)
    public @ResponseBody JsonResult getUserSite(@RequestBody Integer userId){
        try{
            String uniqueCode = getUniqueCode();
            return new JsonResult(null);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取用户管辖站点失败", false);
        }
    }
}
