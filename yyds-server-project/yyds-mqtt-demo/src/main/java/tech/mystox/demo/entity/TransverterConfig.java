package tech.mystox.demo.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/10/18, 19:07.
 * company: mystox
 * description:
 * update record:
 */
public class TransverterConfig {
    private Map<String,List<String>> transverter;

    public Map<String, List<String>> getTransverter() {
        return transverter;
    }

    public void setTransverter(Map<String, List<String>> transverter) {
        this.transverter = transverter;
    }
}
