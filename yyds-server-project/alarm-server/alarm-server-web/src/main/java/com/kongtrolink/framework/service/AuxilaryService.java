package com.kongtrolink.framework.service;

import com.kongtrolink.framework.enttiy.Auxilary;
import com.kongtrolink.framework.query.AuxilaryQuery;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/24 10:37
 * @Description:
 */
public interface AuxilaryService {

    void save(Auxilary auxilary);

    boolean delete(String auxilaryId);

    boolean update(Auxilary auxilary);

    List<Auxilary> list(AuxilaryQuery auxilaryQuery);

    int count(AuxilaryQuery auxilaryQuery);

    Auxilary getByEnterServerCode(String enterpriseCode, String serverCode);
}
