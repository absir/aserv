package com.absir.aserv.app.bean;

import com.absir.aserv.crud.ICrudSubmit;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.JUser;
import com.absir.aserv.system.bean.JUserRole;
import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.*;
import com.absir.orm.value.JaClasses;
import com.absir.server.in.InModel;
import com.absir.validator.value.*;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Map;

@MaEntity(parent = {@MaMenu("测试管理")}, name = "测试")
@Entity
public class JShowForm extends JbBean implements ICrudSubmit<JShowForm.DemoOpition> {

    @NotEmpty
    @JaLang("名称")
    public String name;

    @Range(min = 2, max = 100)
    @JaLang("标识")
    public int tag;

    @Min(2)
    @JaLang("标识1")
    public int tag1;

    @Max(100)
    @JaLang("标识2")
    public int tag2;

    @NotEmpty
    @Email
    @JaLang("邮箱")
    public String email;

    @Digits
    @JaLang("数字")
    public String number;

    @Length(min = 10, max = 100)
    @JaLang("内容")
    @JaEdit(types = "text")
    public String content;

    @Regex("^((13[0-9])|(15[^4,\\\\D])|(18[0,5-9]))\\\\d{8}$")
    @JaLang("手机")
    public String mobilePhone;

    @JaLang("创建时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST)
    public long createTime;

    @JaLang("过期时间")
    @JaEdit(types = "dateTime", groups = JaEdit.GROUP_LIST, editable = JeEditable.LOCKABLE)
    public long passTime;

    @JaLang("标示")
    public boolean flag;

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

    @JaSubField(value = "扩展数据E")
    @JaEmbedd
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    public Element element;

    @JaSubField(value = "extend2", caption = "扩展数据2")
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

    @JaLang("复杂键值字典KeyName")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    @JaClasses(key = JUser.class)
    public Map<Long, Long> metaMap5;

    @JaLang("复杂键值字典ValueName")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    @JaClasses(value = JUserRole.class)
    public Map<Long, Long> metaMap6;

    @JaLang("复杂键值字典AllName")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonDynamic")
    @JaClasses(key = JUser.class, value = JUserRole.class)
    public Map<Long, Long> metaMap7;

    @JaLang("Enum选择")
    @NotEmpty
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    public DemoOpition opition;

    @JaLang("关联选择")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @JaClasses(JShowMapped.class)
    public Long showMappedId;

    @JaLang("关联多择")
    @NotEmpty
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @JaClasses(JShowMapped.class)
    public Long[] showMappedIds;

    @JaLang("ManyToMany关联")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @ManyToMany
    public List<JShowMapped> showMappeds;

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
