/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-7 下午12:26:41
 */
package com.absir.aserv.jdbc;

import java.util.List;

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public class JdbcEntities {

    /**
     * entities
     */
    private List entities;

    /**
     * jdbcPage
     */
    private JdbcPage page;

    /**
     * @param entities
     * @param jdbcPage
     */
    public JdbcEntities(List entities, JdbcPage jdbcPage) {
        this.entities = entities;
        this.page = jdbcPage;
    }

    /**
     * @return the entities
     */
    public List getEntities() {
        return entities;
    }

    /**
     * @param entities the entities to set
     */
    public void setEntities(List entities) {
        this.entities = entities;
    }

    /**
     * @return the page
     */
    public JdbcPage getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(JdbcPage page) {
        this.page = page;
    }
}
