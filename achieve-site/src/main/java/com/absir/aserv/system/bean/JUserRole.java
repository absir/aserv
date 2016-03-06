package com.absir.aserv.system.bean;

import javax.persistence.Entity;

import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.base.JbUserRole;

@MaEntity(parent = { @MaMenu("用户管理") }, name = "角色")
@Entity
public class JUserRole extends JbUserRole {

}
