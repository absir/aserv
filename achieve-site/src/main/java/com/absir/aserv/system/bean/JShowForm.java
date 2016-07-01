package com.absir.aserv.system.bean;

import com.absir.aserv.crud.ICrudSubmit;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaSubField;
import com.absir.orm.value.JaClasses;
import com.absir.server.in.InModel;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Map;

@MaEntity(parent = {@MaMenu("测试管理")}, name = "测试")
@Entity
public class JShowForm extends JbBean implements ICrudSubmit<JShowForm.DemoOpition> {

    @JaLang("名称")
    public String name;

    @JaLang("图片")
    @JaEdit(types = "image")
    public String image;

    @JaLang("文件")
    @JaEdit(types = "file")
    public String file;

    @JaLang("文本框")
    @JaEdit(types = "text")
    public String text;

    @JaLang("编辑器")
    @JaEdit(types = "html")
    public String html;

    @JaSubField("扩展数据")
    @JaLang("扩展编辑器")
    @JaEdit(types = "html")
    public String html1;

    @JaLang("扩展图片")
    @JaEdit(types = "image", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    public String image1;

    @JaLang("扩展文件")
    @JaEdit(types = "file", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    public String file1;

    @JaLang("扩展文本")
    @JaEdit(types = "text", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    public String text1;

    @JaLang("展示关联")
    @OneToMany(mappedBy = "showForm")
    @JaEdit(types = "subtable")
    public List<JShowMapped> mappeds;

    @JaSubField(value = "extend2", capition = "扩展数据2")
    @JaLang("扩展编辑器2")
    @JaEdit(types = "html")
    public String html2;

    @JaLang("扩展图片2")
    @JaEdit(types = "image")
    public String image2;

    @JaLang("扩展文件2")
    @JaEdit(types = "file")
    public String file2;

    @JaLang("扩展文本2")
    @JaEdit(types = "text")
    public String text2;

    @JaLang("扩展字符组")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public List<String> meta;

    @JaLang("扩展结构")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public List<Element> meta2;

    @JaLang("扩展字典")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public Map<String, String> metaMap;

    @JaLang("扩展字典实体")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public Map<String, Element> metaMap2;

    @JaLang("扩展复杂键字典")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public Map<Element, String> metaMap3;

    @JaLang("扩展复杂键值字典")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public Map<Element, Element> metaMap4;

    @JaLang("Enum选择")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    public DemoOpition opition;

    @JaLang("关联选择")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @JaClasses(JShowMapped.class)
    public Long showMappedId;

    @JaLang("关联多择")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @JaClasses(JShowMapped.class)
    public Long[] showMappedIds;

    @JaLang("ManyToMany关联")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @ManyToMany
    public List<JShowMapped> showMappeds;

    @Override
    public Class<DemoOpition> classForOption() {
        return DemoOpition.class;
    }

    @Override
    public String submitOption(DemoOpition option, InModel model) {
        model.put("message", option);
        return null;
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
