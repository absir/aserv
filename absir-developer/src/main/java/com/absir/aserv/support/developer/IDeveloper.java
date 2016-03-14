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

/**
 * @author absir
 *
 */
@Inject
public interface IDeveloper {

    /**
     * ME
     */
    public static final IDeveloper ME = BeanFactoryUtils.get(IDeveloper.class);

    /**
     * @param joEntity
     * @return
     */
    public IModel getModelEntity(JoEntity joEntity);

    /**
     * @param joEntity
     * @return
     */
    public List<JCrudField> getCrudFields(JoEntity joEntity);

    /**
     * @param joEntity
     * @param group
     * @return
     */
    public String[] getGroupFields(JoEntity joEntity, String group);

    /**
     * @param render
     * @return
     */
    public int diy(Object render);

    /**
     * @param includePath
     * @return
     */
    public String getDeveloperPath(String includePath);

    /**
     * @param file
     * @param filePath
     */
    public void copyDeveloper(File file, String filePath) throws IOException;

    /**
     * @param filepath
     * @param includePath
     * @param includePath
     * @param renders
     * @throws IOException
     */
    public void generate(String filepath, String includePath, Object... renders) throws IOException;

    /**
     * @author absir
     */
    public static interface IDeploy {

    }

    /**
     * @author absir
     *
     */
    public static interface ISecurity {

        /**
         * @param render
         * @return
         */
        public JiUserBase loginRender(Object render);
    }
}
