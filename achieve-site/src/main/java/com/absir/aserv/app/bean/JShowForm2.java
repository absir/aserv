package com.absir.aserv.app.bean;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaEmbedd;
import com.absir.aserv.system.bean.value.JaSubField;
import org.hibernate.annotations.Type;

import javax.persistence.Embedded;
import javax.persistence.Entity;

@MaEntity(parent = {@MaMenu("测试管理")}, name = "测试2")
@Entity
public class JShowForm2 extends JbBean {

    @JaSubField(value = "扩展数据1")
    @JaEmbedd
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public JShowForm.Element element;

    @JaSubField(value = "扩展数据2")
    @JaEmbedd
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public JShowForm.Element element2;

    @JaSubField(value = "扩展数据3")
    @Embedded
    public ElementSearch elementSearch;

    public static class ElementSearch {

        @JaEdit(groups = JaEdit.GROUP_LIST)
        public String nameSearch;

        @JaEdit(groups = JaEdit.GROUP_SEARCH)
        public String setterSearch;

    }

}
