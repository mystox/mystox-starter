package com.kongtrolink.framework.model;

import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/5/16, 14:40.
 * company: kongtrolink
 * description:
 * update record:
 */
//@Component
//@ConfigurationProperties(prefix = "compiler")
//@RefreshScope
public class CompilerConfig {
    Map<Integer, List<String>> businessScene;
    Map<Integer, List<String>> product;

    public Map<Integer, List<String>> getBusinessScene() {
        return businessScene;
    }

    public void setBusinessScene(Map<Integer, List<String>> businessScene) {
        this.businessScene = businessScene;
    }

    public Map<Integer, List<String>> getProduct() {
        return product;
    }

    public void setProduct(Map<Integer, List<String>> product) {
        this.product = product;
    }
}
