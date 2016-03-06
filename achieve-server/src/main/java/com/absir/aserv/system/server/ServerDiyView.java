/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年8月12日 下午1:56:21
 */
package com.absir.aserv.system.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.absir.aserv.support.developer.IDeveloper;
import com.absir.aserv.support.developer.IRender;
import com.absir.aserv.support.developer.IRenderSuffix;
import com.absir.aserv.system.asset.Asset_diy;
import com.absir.aserv.system.helper.HelperInput;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectOrder;
import com.absir.bean.inject.value.Value;
import com.absir.core.base.Environment;
import com.absir.core.helper.HelperFile;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.route.returned.ReturnedResolverView;
import com.absir.servlet.InputRequest;

/**
 * @author absir
 *
 */
public abstract class ServerDiyView extends ReturnedResolverView implements IRender, IRenderSuffix {

	/** diyView */
	@Value("developer.diy.view")
	private String diyView = "/WEB-INF/developer/diy.html";

	/** diyExpression */
	protected String diyExpression;

	/** diyInclude */
	protected String diyInclude;

	/**
	 * @return
	 */
	protected String diyExpression() {
		return "<% , %>";
	}

	/**
	 * @return
	 */
	protected String diyInclude() {
		return echo("Pag.include(\",\")");
	}

	/**
	 * 
	 */
	@Inject
	@InjectOrder(value = 255)
	protected void initDiyView() {
		if (diyExpression == null) {
			diyExpression = diyExpression();
		}

		if (diyInclude == null) {
			diyInclude = diyInclude();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.server.route.returned.ReturnedResolverView#resolveReturnedView
	 * (java.lang.String, com.absir.server.on.OnPut)
	 */
	@Override
	public void resolveReturnedView(String view, OnPut onPut) throws Exception {
		Input input = onPut.getInput();
		if (input instanceof InputRequest) {
			render(getView(view), (InputRequest) input);

		} else {
			onPut.getInput().write(view);
		}
	}

	/**
	 * @param view
	 * @param input
	 * @throws Exception
	 */
	public void render(String view, InputRequest input) throws Exception {
		HttpServletRequest request = input.getRequest();
		request.setAttribute("input", input);
		Object[] renders = null;
		int diy = 0;
		if (IDeveloper.ME != null) {
			diy = IDeveloper.ME.diy(request);
			if (diy == 1) {
				Asset_diy.authentication(input);
				InModel inModel = input.getModel();
				inModel.put("diy_url", HelperInput.getRequestUrl(request));
				inModel.put("diy_view", view);
				inModel.put("diy_restore", getDiyRestore(view));
				inModel.put("diy_expression", diyExpression);
				inModel.put("diy_include", diyInclude);
				view = diyView;

			} else if (diy == 2 || BeanFactoryUtils.getEnvironment() != Environment.PRODUCT) {
				renders = getRenders(null, input);
				input.setAttribute(READERS_NAME, renders);
				IDeveloper.ME.generate(view, view, renders);
			}
		}

		renderView(view, renders, input);
	}

	/**
	 * @param view
	 * @return
	 * @throws IOException
	 */
	public String getDiyRestore(String view) throws IOException {
		if (IDeveloper.ME != null) {
			String diyView = IDeveloper.ME.getDeveloperPath(view);
			File file = new File(getRealPath(diyView));
			if (file.exists()) {
				return HelperFile.readFileToString(file);
			}

			file = new File(getRealPath(view));
			if (file.exists()) {
				return HelperFile.readFileToString(file);
			}
		}

		return "";
	}

	/** READERS_NAME */
	private static final String READERS_NAME = ServerDiyView.class + "@READERS_NAME";

	/**
	 * @param input
	 * @return
	 */
	public static Object[] getRenders(Input input) {
		return (Object[]) input.getAttribute(READERS_NAME);
	}

	/**
	 * @param view
	 * @return
	 */
	protected abstract String getView(String view);

	/**
	 * @param view
	 * @param renders
	 */
	protected abstract Object getRender(String view, InputRequest input);

	/**
	 * @param render
	 * @param input
	 * @return
	 */
	protected abstract Object[] getRenders(Object render, InputRequest input);

	/**
	 * @param view
	 * @param renders
	 * @param input
	 * @throws Exception
	 */
	protected abstract void renderView(String view, Object[] renders, InputRequest input) throws Exception;
}
