package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.constant.Regex;
import com.kongtrolink.framework.scloud.entity.MaintainerEntity;
import com.kongtrolink.framework.scloud.entity.model.MaintainerModel;
import com.kongtrolink.framework.scloud.exception.ExcelParseException;
import com.kongtrolink.framework.scloud.util.ExcelUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 维护人员导入Excel 接口类
 * Created by Eric on 2020/4/16.
 */
@Service
public class MaintainerExcelService {

    private boolean[] nullable = {false, false, false, false, false, true, true, true, true, true};
    private String[] regex = {Regex.USERNAME, Regex.PERSON_NAME, Regex.CELLPHONE, Regex.EMAIL, Regex.DEFAULT,
            Regex.JOB_STATUS, Regex.DEFAULT, Regex.DEFAULT, Regex.DEFAULT, Regex.EDUCATION};

    public List<MaintainerModel> read(File file, String uniqueCode) throws ExcelParseException{
        String[][] maintainerArray = ExcelUtil.getInstance().getCellsFromFile(file);
        return analyze(maintainerArray, uniqueCode);
    }

    public List<MaintainerModel> analyze(String[][] array, String uniqueCode) throws ExcelParseException {
        ExcelUtil.getInstance().checkExcelRow(array, nullable, regex);
        ExcelUtil.getInstance().checkArrayColumnDuplicated(array, 0);   //账号重复性检测

        List<MaintainerModel> modelList = new ArrayList<>();
        int currentRowIndex = 0;
        while (currentRowIndex < array.length) {
            String[] row = array[currentRowIndex];

            MaintainerModel model = new MaintainerModel();
            model.setUsername(row[0]);
            model.setName(row[1]);
            model.setPhone(row[2]);
            model.setEmail(row[3]);
            model.setCompanyName(row[4]);
            model.setStatus(row[5]);
            model.setMajor(row[6]);
            model.setSkill(row[7]);
            model.setDuty(row[8]);
            model.setEducation(row[9]);

            modelList.add(model);
            currentRowIndex ++;
        }

        return modelList;
    }
}
