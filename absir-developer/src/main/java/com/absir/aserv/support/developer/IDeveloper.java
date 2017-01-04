/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-8-8 下午5:10:11
 */
package com.absir.aserv.support.developer;

import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.orm.value.JoEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Inject
public interface IDeveloper {

    public static final IDeveloper ME = BeanFactoryUtils.get(IDeveloper.class);

    public int getDeveloperNewType();

    public long getDeveloperNewTime();

    public void setDeveloperNewTime(long developerNewTime);

    public IModel getModelEntity(JoEntity joEntity);

    public List<JCrudField> getCrudFields(JoEntity joEntity);

    public String[] getGroupFields(JoEntity joEntity, String group);

    public int diy(Object render);

    public String getDeveloperPath(String includePath);

    public void copyDeveloper(File file, String filePath) throws IOException;

    public void generate(String filePath, String includePath, Object... renders) throws IOException;

    public static interface IDeploy {

    }

    public static interface ISecurity {

        public JiUserBase loginRender(Object render);
    }
}
