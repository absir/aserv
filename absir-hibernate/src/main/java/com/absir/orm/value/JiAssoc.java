/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-24 下午3:00:55
 */
package com.absir.orm.value;

import javax.persistence.Id;
import java.io.Serializable;

public interface JiAssoc {

    /**
     * 主键ID
     */
    @Id
    public Long getId();

    /**
     * 获取关联实体主键
     */
    public Serializable getAssocId();
}
