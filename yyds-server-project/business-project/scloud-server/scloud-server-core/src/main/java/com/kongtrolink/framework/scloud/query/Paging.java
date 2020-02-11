package com.kongtrolink.framework.scloud.query;

import java.io.Serializable;

/**
 * 分页基础类
 * Created by Eric on 2020/2/11.
 */
public class Paging implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4251834541155529894L;
    private int currentPage = 1;
    private int pageSize = 20;

    public Paging() {
    }

    public Paging(int currentPage, int pageSize) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
