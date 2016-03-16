/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-7 下午12:26:41
 */
package com.absir.aserv.jdbc;

import java.util.List;

@SuppressWarnings("rawtypes")
public class JdbcEntities {

    private List entities;

    private JdbcPage page;

    public JdbcEntities(List entities, JdbcPage jdbcPage) {
        this.entities = entities;
        this.page = jdbcPage;
    }

    public List getEntities() {
        return entities;
    }

    public void setEntities(List entities) {
        this.entities = entities;
    }

    public JdbcPage getPage() {
        return page;
    }

    public void setPage(JdbcPage page) {
        this.page = page;
    }
}
