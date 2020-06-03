package tech.mystox.framework.core;

import tech.mystox.framework.entity.RegisterSub;

import java.util.List;

/**
 * Created by mystoxlol on 2019/8/28, 18:24.
 * company: mystox
 * description:
 * update record:
 */
public interface ServiceScanner {
    List<RegisterSub> getSubList();

    boolean addSub(RegisterSub registerSub);

    boolean deleteSub(String operaCode);
}
