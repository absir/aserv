/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-11-29 上午11:22:34
 */
package com.absir.aserv.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.absir.aserv.lang.LangBundleImpl;
import com.absir.aserv.menu.value.MeUrlType;
import com.absir.aserv.system.bean.JMaMenu;
import com.absir.aserv.system.bean.JMenu;
import com.absir.aserv.system.bean.JMenuCite;
import com.absir.aserv.system.bean.JPermission;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JeVote;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.service.AuthService;
import com.absir.aserv.system.service.utils.AuthServiceUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectType;
import com.absir.core.kernel.KernelString;
import com.absir.orm.transaction.value.Transaction;

/**
 * @author absir
 * 
 */
@Bean
public class MenuBeanService {

	/** typeMapMenuSupport */
	private Map<String, IMenuSupport> typeMapMenuSupport = new HashMap<String, IMenuSupport>();

	/**
	 * 
	 */
	@Inject
	protected void addMenuSupply() {
		// 父链接权限管理
		typeMapMenuSupport.put("FOLDER", null);
		addMenuSupply(new IMenuSupport[] {

		new IMenuSupport() {

			@Override
			public boolean isPermission(IMenuBean menuBean, JiUserBase user) {
				String ref = menuBean.getRef();
				if (ref == null) {
					return true;
				}

				return AuthService.ME.menuPermission(ref, user);
			}

			@Override
			public String getMenuType() {
				return "MENU";
			}
		},

		new IMenuSupport() {

			@Override
			public boolean isPermission(IMenuBean menuBean, JiUserBase user) {
				return AuthServiceUtils.selectPermission(menuBean.getRef(), user);
			}

			@Override
			public String getMenuType() {
				return "ENTITY";
			}
		},

		});
	}

	/**
	 * @param menuSupports
	 */
	@Inject(type = InjectType.Selectable)
	protected void addMenuSupply(IMenuSupport[] menuSupports) {
		for (IMenuSupport menuSupport : menuSupports) {
			typeMapMenuSupport.put(menuSupport.getMenuType(), menuSupport);
		}
	}

	/**
	 * 获取深度菜单
	 * 
	 * @param cite
	 * @param user
	 * @param depth
	 * @return
	 */
	@Transaction(readOnly = true)
	public List<OMenuBean> getMenuBeans(String cite, JiUserBase user, int depth) {
		JMenuCite menuCite = BeanDao.get(BeanDao.getSession(), JMenuCite.class, cite);
		if (menuCite == null) {
			return null;
		}

		if (depth == 0) {
			depth = 1;
		}

		return getMenuBeans(LangBundleImpl.ME.getLangProxy("JMenu", menuCite.getMenu()), user, depth);
	}

	/**
	 * @param parent
	 * @param user
	 * @param depth
	 */
	private List<OMenuBean> getMenuBeans(JMenu parent, JiUserBase user, int depth) {
		if ((depth >= 0 && --depth < 0) || parent.getChildren() == null) {
			return null;
		}

		List<OMenuBean> menuBeans = new ArrayList<OMenuBean>();
		for (JMenu menu : parent.getChildren()) {
			boolean emptyUrl = KernelString.isEmpty(menu.getUrl());
			boolean isFolder = false;
			if (!(emptyUrl || KernelString.isEmpty(menu.getType()))) {
				IMenuSupport menuSupport = typeMapMenuSupport.get(menu.getType());
				if (menuSupport == null) {
					isFolder = true;

				} else if (!menuSupport.isPermission(menu, user)) {
					continue;
				}
			}

			List<OMenuBean> children = getMenuBeans(menu, user, isFolder && depth < 1 ? 1 : depth);
			if (!(emptyUrl || isFolder) || (children != null && children.size() > 0)) {
				OMenuBean menuBean = new OMenuBean(menu);
				menuBean.setChildren(children);
				menuBeans.add(menuBean);
			}
		}

		return menuBeans;
	}

	/**
	 * 添加实体权限
	 * 
	 * @param entityNames
	 */
	@Transaction
	public void addEntityPermission(List<String> entityNames) {
		Map<Long, JPermission> permissions = null;
		int size = entityNames.size();
		Session session = BeanDao.getSession();
		for (int i = 0; i < size; i += 2) {
			String entityName = entityNames.get(i);
			JMaMenu maMenu = BeanDao.get(session, JMaMenu.class, entityName);
			if (maMenu == null) {
				maMenu = new JMaMenu();
				maMenu.setId(entityName);
				maMenu.setCaption(entityNames.get(i + 1));
				if (permissions == null) {
					JPermission permission = new JPermission();
					permission.setSelectable(JeVote.ALLOW);
					permission.setUpdatable(JeVote.ALLOW);
					permission.setInsertable(JeVote.ALLOW);
					permission.setDeletable(JeVote.ALLOW);
					permission.setAllows(new String[] { "*" });
					permissions = new HashMap<Long, JPermission>();
					permissions.put(1L, permission);
				}

				maMenu.setPermissions(permissions);
				session.merge(maMenu);
			}
		}
	}

	/**
	 * 添加链接菜单
	 * 
	 * @param menuBeanRoot
	 * @param cite
	 * @param name
	 * @param urlType
	 */
	@Transaction
	public void addMenuBeanRoot(MenuBeanRoot menuBeanRoot, String cite, String name, MeUrlType urlType) {
		Session session = BeanDao.getSession();
		JMenuCite menuCite = BeanDao.get(session, JMenuCite.class, cite);
		if (menuCite == null) {
			JMenu menu = new JMenu();
			menu.setName(name);
			session.persist(menu);
			menuCite = new JMenuCite();
			menuCite.setId(cite);
			menuCite.setMenu(menu);
			session.merge(menuCite);
		}

		addMenuBeanRoot(session, menuBeanRoot, menuCite.getMenu(), urlType);
	}

	/**
	 * 迭代添加链接菜单
	 * 
	 * @param menuBeanRoot
	 * @param parent
	 * @param urlType
	 */
	private void addMenuBeanRoot(Session session, MenuBeanRoot menuBeanRoot, JMenu parent, MeUrlType urlType) {
		if (menuBeanRoot.getChildren() == null) {
			return;
		}

		for (MenuBeanRoot beanRoot : menuBeanRoot.getChildren().values()) {
			JMenu menuBean = beanRoot.getMenuBean();
			JMenu menu = (JMenu) QueryDaoUtils.select(session, "JMenu", KernelString.isEmpty(menuBean.getUrl()) ? new Object[] { "o.parent", parent, "o.name", menuBean.getName() } : new Object[] {
					"o.parent", parent, "o.url", menuBean.getUrl() });
			if (menu == null) {
				menu = menuBean;
				menu.setParent(parent);
				if (menu.getUrlType() == null) {
					menu.setUrlType(urlType);
				}

				session.persist(menu);
			}

			addMenuBeanRoot(session, beanRoot, menu, urlType);
		}
	}
}
