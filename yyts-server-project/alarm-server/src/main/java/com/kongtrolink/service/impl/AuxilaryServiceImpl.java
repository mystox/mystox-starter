package com.kongtrolink.service.impl;

import com.kongtrolink.dao.AuxilaryDao;
import com.kongtrolink.enttiy.Auxilary;
import com.kongtrolink.query.AuxilaryQuery;
import com.kongtrolink.service.AuxilaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**`
 * @Auther: liudd
 * @Date: 2019/9/24 10:39
 * @Description:
 */
@Service
public class AuxilaryServiceImpl implements AuxilaryService {

    @Autowired
    AuxilaryDao auxilaryDao;

    @Override
    public void save(Auxilary auxilary) {
        auxilaryDao.save(auxilary);
    }

    @Override
    public boolean delete(String auxilaryId) {
        return auxilaryDao.delete(auxilaryId);
    }

    @Override
    public boolean update(Auxilary auxilary) {
        return auxilaryDao.update(auxilary);
    }

    @Override
    public List<Auxilary> list(AuxilaryQuery auxilaryQuery) {
        return auxilaryDao.list(auxilaryQuery);
    }

    @Override
    public int count(AuxilaryQuery auxilaryQuery) {
        return auxilaryDao.count(auxilaryQuery);
    }
}