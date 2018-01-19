package com.absir.aserv.app.bean;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.orm.value.JaEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@JaEntity
@Entity
public class JShowMapped extends JbBean {

    @JaLang("图片")
    @JaEdit(types = "image")
    public String image1;

    @JaLang("文件")
    @JaEdit(types = "file", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    public String file1;

    @JaLang("文本")
    @JaEdit(types = "text", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    public String text1;

    @JaLang("ManyToOne关联")
    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @ManyToOne
    public JShowForm showForm;

}
