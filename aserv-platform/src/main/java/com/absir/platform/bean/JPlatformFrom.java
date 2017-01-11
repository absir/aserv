package com.absir.platform.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JiBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import tplatform.DPlatformFrom;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by absir on 2016/12/5.
 */
@MaEntity(parent = {@MaMenu("平台管理")}, name = "来源")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
public class JPlatformFrom extends DPlatformFrom implements JiBean {

    @JaEdit(groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_SUGGEST})
    @JaLang("纪录编号")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void putSerializableId(Serializable id) {
        setId((Long) id);
    }

}
