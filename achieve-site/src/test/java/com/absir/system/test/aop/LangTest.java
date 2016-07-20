/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月11日 上午10:12:34
 */
package com.absir.system.test.aop;

import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.lang.ILangBase;
import com.absir.aserv.lang.LangBundleImpl;
import com.absir.aserv.lang.value.Langs;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.core.base.IBase;
import com.absir.system.test.AbstractTestInject;
import org.junit.Test;

import javax.persistence.Embeddable;

public class LangTest extends AbstractTestInject {

    @Test
    public void test() throws Throwable {
        try {
            LangBean langBean = new LangBean();
            langBean.id = 3L;
            langBean.name = "测试";
            langBean.getLangEmbed().name = "测试子";
            langBean = LangBundleImpl.ME.getLangProxy("LangBean", langBean);

            CrudHandler crudHandler = new CrudHandler(null, null, null, null, langBean) {
            };
            ((ICrudBean) langBean).processCrud(Crud.CREATE, crudHandler, null);

            ((ILangBase) langBean).setLang("name", 33, "test");
            ((ILangBase) langBean.getLangEmbed()).setLang("name", 33, "test123333");
            ((ICrudBean) langBean).processCrud(Crud.CREATE, crudHandler, null);
            ((ICrudBean) langBean.getLangEmbed()).processCrud(Crud.CREATE, crudHandler, null);

            ILangBase langBase = (ILangBase) langBean.getLangEmbed();
            System.out.println(langBase.getLang("name", 33, String.class));

            langBean = new LangBean();
            langBean.id = 3L;
            langBean.name = "测试";
            langBean.name = "测试子";
            langBean = LangBundleImpl.ME.getLangProxy("LangBean", langBean);

            System.out.println(langBean.getName());
            System.out.println(langBean.getLangEmbed().getName());
            System.out.println(((ILangBase) langBean).getLang("name", 33, String.class));
            langBase = (ILangBase) langBean.getLangEmbed();
            System.out.println(langBase.getLang("name", 33, String.class));

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Embeddable
    public static class LangEmbed {

        public String name;

        @Langs
        public String getName() {
            return name;
        }
    }

    public static class LangBean extends LangEmbed implements IBase<Long> {

        public Long id;

        public LangEmbed langEmbed = new LangEmbed();

        @Override
        public Long getId() {
            return id;
        }

        public LangEmbed getLangEmbed() {
            return langEmbed;
        }
    }
}
