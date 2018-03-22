package com.absir.aserv.system.service;

import com.absir.aserv.menu.OMenuBean;
import com.absir.aserv.system.bean.*;
import com.absir.aserv.system.bean.base.JbUserRole;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperArray;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelString;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;

import java.util.*;

@Base
@Bean
public class UserRolePermissionsService {

    public static final UserRolePermissionsService ME = BeanFactoryUtils.get(UserRolePermissionsService.class);

    @Transaction(readOnly = true)
    public JUserRolePermissions getUserRolePermissions(JbUserRole userRole) {
        Session session = BeanDao.getSession();
        JUserRolePermissions rolePermissions = BeanDao.get(session, JUserRolePermissions.class, userRole.getId());
        if (rolePermissions == null) {
            rolePermissions = new JUserRolePermissions();
        }

        rolePermissions.setId(userRole.getId());
        rolePermissions.setRolename(userRole.getRolename());

        List<JMenu> menuList = (List<JMenu>) QueryDaoUtils.createQueryArray(session, "SELECT o FROM JMenu o WHERE o.type = ? OR o.type = ? ", "MENU", "ENTITY").list();
        Set<String> permissions = new HashSet<String>();
        Map<Long, OMenuBean> menuMap = new HashMap<Long, OMenuBean>();
        List<OMenuBean> menuRoots = new ArrayList<OMenuBean>();
        OMenuBean menuBean;
        OMenuBean menuChild;
        for (JMenu menu : menuList) {
            if (permissions.add(menu.getType().equals("MENU") ? ("@" + menu.getRef()) : menu.getRef())) {
                menuChild = null;
                while (true) {
                    menuBean = menuMap.get(menu.getId());
                    if (menuBean != null) {
                        if (menuChild != null) {
                            menuBean.addChild(menuChild);
                        }

                        break;
                    }

                    menuBean = new OMenuBean(menu);
                    menuMap.put(menu.getId(), menuBean);
                    if (menuChild != null) {
                        menuBean.addChild(menuChild);
                    }

                    menu = menu.getParent();
                    if (menu == null) {
                        menuRoots.add(menuBean);
                        break;
                    }

                    menuChild = menuBean;
                }
            }
        }

        permissions = null;
        menuMap = null;
        OMenuBean.sortMenus(menuRoots);
        addUserRolePermissionsMenuBeans(session, rolePermissions, menuRoots, -1);
        return rolePermissions;
    }

    private static JPermission permissionDefault;

    protected JPermission getPermissionDefault() {
        if (permissionDefault == null) {
            permissionDefault = JPermission.getPermissionDefault();
        }

        return permissionDefault;
    }

    protected boolean addUserRolePermissionsMenuBeans(Session session, JUserRolePermissions rolePermissions, Collection<? extends OMenuBean> menuBeans, int depth) {
        boolean allAuthorize = true;
        if (menuBeans == null || menuBeans.size() == 0) {
            return allAuthorize;
        }

        List<JUserRolePermissions.DPermission> dPermissions = rolePermissions.getPermissions();
        if (dPermissions == null) {
            dPermissions = new ArrayList<JUserRolePermissions.DPermission>();
            rolePermissions.setPermissions(dPermissions);
        }

        depth++;
        long roleId = rolePermissions.getId();
        for (OMenuBean menuBean : menuBeans) {
            JMenu menu = (JMenu) menuBean.getMenuBean();
            JUserRolePermissions.DPermission dPermission = new JUserRolePermissions.DPermission();
            dPermission.depth = depth;
            dPermission.menu = menu;
            dPermissions.add(dPermission);

            String type = menu.getType();
            if (!KernelString.isEmpty(type)) {
                if (type.equals("MENU")) {
                    JMenuPermission menuPermission = BeanDao.get(session, JMenuPermission.class, menu.getRef());
                    if (menuPermission != null) {
                        dPermission.id = menu.getRef();
                        dPermission.authorize = menuPermission.getAllowIds() != null && HelperArray.contains(menuPermission.getAllowIds(), roleId);
                    }

                } else if (type.equals("ENTITY")) {
                    JMaMenu maMenu = BeanDao.get(session, JMaMenu.class, menu.getRef());
                    if (maMenu != null) {
                        dPermission.id = menu.getRef();
                        JPermission permission = maMenu.getPermissions() == null ? null : maMenu.getPermissions().get(rolePermissions.getId());
                        dPermission.authorize = permission != null;
                        if (permission == null && rolePermissions.getMaPermissions() != null) {
                            permission = rolePermissions.getMaPermissions().get(dPermission.id);
                        }

                        if (permission == null) {
                            permission = getPermissionDefault();
                        }

                        dPermission.permission = permission;
                    }
                }
            }

            boolean authorize = addUserRolePermissionsMenuBeans(session, rolePermissions, menuBean.getChildren(), depth);
            if (dPermission.id == null) {
                dPermission.authorize = authorize;
            }

            if (allAuthorize && !dPermission.authorize) {
                allAuthorize = false;
            }
        }

        return allAuthorize;
    }

    @Transaction
    public void saveUserRolePermissions(JUserRolePermissions rolePermissions) {
        Session session = BeanDao.getSession();
        List<JUserRolePermissions.DPermission> dPermissions = rolePermissions.getPermissions();
        Map<String, JPermission> maPermissions = null;
        if (dPermissions != null) {
            long roleId = rolePermissions.getId();
            maPermissions = new HashMap<String, JPermission>();
            for (JUserRolePermissions.DPermission dPermission : dPermissions) {
                if (dPermission != null && dPermission.id != null) {
                    if (dPermission.permission == null) {
                        JMenuPermission menuPermission = BeanDao.get(session, JMenuPermission.class, dPermission.id);
                        if (menuPermission == null) {
                            continue;
                        }

                        if (dPermission.authorize == HelperArray.contains(menuPermission.getAllowIds(), roleId)) {
                            continue;
                        }

                        if (dPermission.authorize) {
                            menuPermission.setAllowIds(HelperArray.add(menuPermission.getAllowIds(), roleId));

                        } else {
                            menuPermission.setAllowIds(HelperArray.removeElement(menuPermission.getAllowIds(), roleId));
                        }

                        session.merge(menuPermission);
                        session.flush();
                        session.evict(menuPermission);

                    } else {
                        JMaMenu maMenu = BeanDao.get(session, JMaMenu.class, dPermission.id);
                        if (maMenu == null) {
                            continue;
                        }

                        if (dPermission.authorize) {
                            Map<Long, JPermission> permissions = maMenu.getPermissions();
                            if (permissions == null) {
                                permissions = new HashMap<Long, JPermission>();
                                maMenu.setPermissions(permissions);
                            }

                            permissions.put(rolePermissions.getId(), dPermission.permission);

                        } else {
                            if (maMenu.getPermissions() != null) {
                                if (maMenu.getPermissions().remove(rolePermissions.getId()) == null) {
                                    maPermissions.put(dPermission.id, dPermission.permission);
                                    continue;
                                }
                            }
                        }

                        session.merge(maMenu);
                        session.flush();
                        session.evict(maMenu);
                    }
                }
            }
        }

        rolePermissions.setMaPermissions(maPermissions);
        session.merge(rolePermissions);
        session.flush();
    }

}
