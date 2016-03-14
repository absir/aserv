package com.absir.aserv.system.bean;

import com.absir.aserv.system.bean.base.JbBean;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.orm.value.JaEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by absir on 16/2/5.
 */
@JaEntity
@Entity
public class JModelDemoMapped extends JbBean {

    @JaEdit(types = "image")
    public String image1;

    @JaEdit(types = "file", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    public String file1;

    @JaEdit(types = "text", groups = {JaEdit.GROUP_SUG, JaEdit.GROUP_LIST})
    public String text1;

    @JaEdit(groups = {JaEdit.GROUP_LIST})
    @ManyToOne
    public JModelDemo demo;

}
