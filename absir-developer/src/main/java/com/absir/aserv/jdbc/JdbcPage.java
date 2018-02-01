/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-27 下午7:12:08
 */
package com.absir.aserv.jdbc;

public class JdbcPage {

    public static final int PAGE_SIZE = 20;

    public static final int MIN_PAGE_SIZE = 2;

    public static final int MAX_PAGE_SIZE = 1024;

    private int pageIndex;

    private int pageSize;

    private int pageCount;

    private int totalCount;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        if (pageSize == 0) {
            pageSize = PAGE_SIZE;
        }

        if (pageSize < MIN_PAGE_SIZE) {
            pageSize = MIN_PAGE_SIZE;

        } else if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setDefaultPageSize(int _pageSize) {
        if (pageSize == 0) {
            pageSize = _pageSize;
        }
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        pageCount = (int) Math.ceil((double) totalCount / getPageSize());
        if (pageIndex > pageCount) {
            pageIndex = pageCount;
        }

        if (pageIndex < 1) {
            pageIndex = 1;
        }

        this.totalCount = totalCount;
    }

    public int getFirstResult() {
        return pageSize * (pageIndex - 1);
    }
}
