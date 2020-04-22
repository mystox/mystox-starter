package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.constant.UserRegex;
import com.kongtrolink.framework.scloud.entity.model.MaintainerModel;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.exception.ExcelParseException;
import com.kongtrolink.framework.scloud.util.ExcelUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述:系统用户导入
 * @Author: ypt
 * @Date: 2020/4/21 16:58
 */
@Service
public class UserExcelService {

    private boolean[] nullable = {false, false, false, false, false, true, true};
    private String[] regex = {UserRegex.USERNAME, UserRegex.DEFAULT, UserRegex.PERSON_NAME, UserRegex.CELLPHONE,
            UserRegex.EMAIL, UserRegex.USER_TIME, UserRegex.VALID_TIME};

    public List<UserModel> read(File file,String uniqueCode) throws ExcelParseException, ParseException {
        String[][] userArray =  ExcelUtil.getInstance().getCellsFromFile(file);
        return analyze(userArray,uniqueCode);
    }

    public List<UserModel> analyze(String[][] array, String uniqueCode) throws ExcelParseException, ParseException {
        ExcelUtil.getInstance().checkExcelRow(array, nullable, regex);
        ExcelUtil.getInstance().checkArrayColumnDuplicated(array, 0);   //账号重复性检测

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        List<UserModel> userList = new ArrayList<>();
        int currentRowIndex = 0;
        while (currentRowIndex < array.length) {
            String[] row = array[currentRowIndex];

            UserModel user = new UserModel();
            user.setUsername(row[0]);
            user.setCurrentRoleName(row[1]);
            user.setName(row[2]);
            user.setPhone(row[3]);
            user.setEmail(row[4]);
            user.setUserTime(row[5] == null?"长期":row[5]);
            user.setValidTime(row[6] == null?null:sd.parse(row[6]));

            userList.add(user);
            currentRowIndex ++;
        }

        return userList;
    }
}
