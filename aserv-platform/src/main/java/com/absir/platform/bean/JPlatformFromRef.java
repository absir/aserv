package com.absir.platform.bean;

import com.absir.aserv.system.bean.base.JbBeanS;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * Created by absir on 2016/12/5.
 */
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JPlatformFromRef extends JbBeanS {

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ManyToOne(fetch = FetchType.EAGER)
    private JPlatformFrom platformFrom;

    public JPlatformFrom getPlatformFrom() {
        return platformFrom;
    }

    public void setPlatformFrom(JPlatformFrom platformFrom) {
        this.platformFrom = platformFrom;
    }
}
