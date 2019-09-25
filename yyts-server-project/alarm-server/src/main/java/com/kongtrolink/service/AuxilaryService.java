package com.kongtrolink.service;

import com.kongtrolink.enttiy.Auxilary;
import com.kongtrolink.query.AuxilaryQuery;
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
}
