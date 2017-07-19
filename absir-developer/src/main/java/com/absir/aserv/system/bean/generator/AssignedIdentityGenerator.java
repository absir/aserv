package com.absir.aserv.system.bean.generator;

import com.absir.core.base.IBase;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;

/**
 * Created by absir on 19/7/17.
 */
public class AssignedIdentityGenerator extends IdentityGenerator {

    @Override
    public Serializable generate(SessionImplementor s, Object obj) {
        if (obj instanceof IBase) {
            Serializable id = ((IBase) obj).getId();
            if (id != null) {
                return id;
            }
        }

        return super.generate(s, obj);
    }
}
