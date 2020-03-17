package com.kongtrolink.framework.reports.entity.query;

/**
 * 分页
 *  SCloud->中台资管
 * Created by Eric on 2020/3/4.
 */
public class BasicPagingQuery {

    private BasicCommonQuery curPage;   //当前页
    private BasicCommonQuery pageNum;   //每页显示条数

    public BasicCommonQuery getCurPage() {
        return curPage;
    }

    public void setCurPage(BasicCommonQuery curPage) {
        this.curPage = curPage;
    }

    public BasicCommonQuery getPageNum() {
        return pageNum;
    }

    public void setPageNum(BasicCommonQuery pageNum) {
        this.pageNum = pageNum;
    }
}
