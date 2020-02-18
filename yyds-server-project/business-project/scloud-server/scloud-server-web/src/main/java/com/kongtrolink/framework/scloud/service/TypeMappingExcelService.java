package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.DeviceType;
import com.kongtrolink.framework.scloud.entity.SignalType;
import com.kongtrolink.framework.scloud.exception.ExcelParseException;
import com.kongtrolink.framework.scloud.util.ExcelUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jasper, Mosaico
 */
@Service
public class TypeMappingExcelService {

    public List read(File file) throws ExcelParseException {
        String[][] typeArray = ExcelUtil.getInstance().getCellsFromFile(file);
        List<DeviceType> deviceTypeList = new ArrayList<>();

        int currentRowIndex = 0;
        while(currentRowIndex < typeArray.length){
            String[] row = typeArray[currentRowIndex];
            if (row[0] != null && row[1] != null) {

                DeviceType dt = new DeviceType();
                dt.setTypeName(row[0]);
                dt.setCode(row[1]);

                List<SignalType> signalTypeList = new ArrayList<>();
                currentRowIndex ++;
                while (currentRowIndex < typeArray.length) {
                    row = typeArray[currentRowIndex];
                    if (row[2] != null && row[3] != null) {
                        SignalType st = new SignalType();
                        st.setTypeName(row[2]);
                        st.setCode(row[3]);
                        st.setMeasurement(row[4]);
                        st.setCntbId(row[5]);
                        st.setType(row[6]);
                        st.setCommunicationError(row[7] != null);
                        signalTypeList.add(st);

                        currentRowIndex ++;
                    }else{
                        break;
                    }
                }
                dt.setSignalTypeList(signalTypeList);
                deviceTypeList.add(dt);
            }
        }
        return deviceTypeList;
    }

}
