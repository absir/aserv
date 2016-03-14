package com.absir.aserv.system.bean;

import com.absir.aserv.crud.ICrudSubmit;
import com.absir.aserv.facade.DMessage;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaSubField;
import com.absir.orm.value.JaClasses;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Map;

/**
 * Created by absir on 16/1/20.
 */
@MaEntity(parent = {@MaMenu("测试管理")}, name = "测试")
@Entity
public class JModelDemo extends JbBean implements ICrudSubmit<JModelDemo.DemoOpition> {

    @JaEdit(types = "image")
    public String image;

    @JaEdit(types = "file")
    public String file;

    @JaEdit(types = "text")
    public String text;

    @JaEdit(types = "html")
    public String html;


    @JaSubField("test1")
    @JaEdit(types = "html")
    public String html1;

    @JaEdit(types = "image", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    public String image1;

    @JaEdit(types = "file", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    public String file1;

    @JaEdit(types = "text", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    public String text1;

    @OneToMany(mappedBy = "demo")
    @JaEdit(types = "subtable")
    public List<JModelDemoMapped> mappeds;

    @JaSubField(value = "test2", capition = "ffffc")
    @JaEdit(types = "html")
    public String html2;

    @JaEdit(types = "image")
    public String image2;

    @JaEdit(types = "file")
    public String file2;

    @JaEdit(types = "text")
    public String text2;


    @JaLang("扩展")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public List<String> meta;

    @JaLang("扩展")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public List<Element> meta2;

    @JaLang("扩展纪录")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public Map<String, String> metaMap;

    @JaLang("扩展纪录2")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public Map<String, Element> metaMap2;

    @JaLang("扩展纪录3")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public Map<Element, String> metaMap3;

    @JaLang("扩展纪录4")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public Map<Element, Element> metaMap4;

    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @JaClasses(JModelDemoMapped.class)
    public Long test3;

    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @JaClasses(JModelDemoMapped.class)
    public Long[] test1;

    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @ManyToMany
    public List<JModelDemoMapped> test2;

    @Override
    public Class<DemoOpition> classForOption() {
        return DemoOpition.class;
    }

    @Override
    public DMessage submitOption(DemoOpition option) {
        DMessage message = new DMessage();
        message.value = option.toString();
        return message;
    }

    public static enum DemoOpition {

        @JaLang("测试1")
        test1,

        @JaLang("测试2")
        test2,

        @JaLang("测试3")
        test3,

    }

    public static class Element {

        public String name;

        public String setter;

        public int code;

    }
}
