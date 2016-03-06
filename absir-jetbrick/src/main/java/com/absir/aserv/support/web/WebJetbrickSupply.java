/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年7月24日 下午1:02:10
 */
package com.absir.aserv.support.web;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import com.absir.aserv.developer.Pag;
import com.absir.aserv.developer.Scenario;
import com.absir.aserv.menu.IMenuBean;
import com.absir.aserv.menu.MenuContextUtils;
import com.absir.aserv.support.web.WebJetbrickSupply.ConfigureFound;
import com.absir.aserv.support.web.value.BaFunction;
import com.absir.aserv.support.web.value.BaMethod;
import com.absir.aserv.support.web.value.BaTag;
import com.absir.aserv.system.bean.JEmbedLL;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.statics.EntityStatics;
import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanScope;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.IMethodSupport;
import com.absir.bean.inject.InjectInvoker;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Value;
import com.absir.client.helper.HelperJson;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.orm.value.JoEntity;
import com.absir.server.exception.ServerException;
import com.absir.servlet.InDispathFilter;

import jetbrick.io.resource.Resource;
import jetbrick.template.JetEngine;
import jetbrick.template.JetGlobalContext;
import jetbrick.template.JetWriterNone;
import jetbrick.template.VariableResolverBean;
import jetbrick.template.runtime.InterpretContext;
import jetbrick.template.runtime.JetTagContext;
import jetbrick.template.runtime.JetWriter;
import jetbrick.template.web.JetWebContext;
import jetbrick.template.web.JetWebEngine;
import jetbrick.util.PathUtils;

/**
 * @author absir
 *
 */
@SuppressWarnings("unchecked")
@Basis
@Bean
public class WebJetbrickSupply implements IMethodSupport<ConfigureFound> {

	/** engine */
	private static JetEngine engine;

	/** variableResolverBean */
	private static VariableResolverBean variableResolverBean;

	/** resourceLoaderRoot */
	private static String resourceLoaderRoot;

	/**
	 * @return
	 */
	/**
	 * @return
	 */
	public static JetEngine getEngine() {
		if (engine == null) {
			Properties configProperties = new Properties();
			BeanConfigImpl.readProperties(BeanFactoryUtils.getBeanConfig(),
					(Map<String, Object>) (Object) configProperties,
					new File(BeanFactoryUtils.getBeanConfig().getClassPath("jetbrick.properties")), null);
			ServletContext servletContext = InDispathFilter.getServletContext();
			if (servletContext == null) {
				engine = JetEngine.create(configProperties);

			} else {
				engine = JetWebEngine.create(servletContext, configProperties, null);
			}

			JetGlobalContext globalContext = engine.getGlobalContext();
			if (globalContext == null) {
				globalContext = new JetGlobalContext();
				KernelObject.declaredSet(engine, "globalContext", globalContext);
				globalContext = engine.getGlobalContext();
			}

			if (globalContext != null) {
				globalContext.set("APP_NAME", MenuContextUtils.getAppName());
				globalContext.set("SITE_ROUTE", MenuContextUtils.getSiteRoute());
				globalContext.set("ADMIN_ROUTE", MenuContextUtils.getAdminRoute());
			}

			getVariableResolverBean().importClass(JoEntity.class.getName());
			getVariableResolverBean().importClass(IMenuBean.class.getName());
			getVariableResolverBean().importPackage(KernelObject.class.getPackage().getName());
			getVariableResolverBean().importPackage(JEmbedLL.class.getPackage().getName());
			getVariableResolverBean().importPackage(JiUserBase.class.getPackage().getName());
			getVariableResolverBean().importPackage(ServerException.class.getPackage().getName());
			getVariableResolverBean().importPackage(Pag.class.getPackage().getName());
			getVariableResolverBean().importPackage(EntityStatics.class.getPackage().getName());
		}

		return engine;
	}

	/**
	 * @return
	 */
	public static VariableResolverBean getVariableResolverBean() {
		if (variableResolverBean == null) {
			variableResolverBean = new VariableResolverBean(getEngine());
		}

		return variableResolverBean;
	}

	/**
	 * @return
	 */
	public static String getResourceLoaderRoot() {
		if (resourceLoaderRoot == null) {
			try {
				Resource resource = getEngine().getResource("/");
				Object file = KernelObject.declaredGet(resource, "file");
				if (file != null && file instanceof File) {
					resourceLoaderRoot = ((File) file).getPath();
				}

				if (resourceLoaderRoot == null) {
					resourceLoaderRoot = resource.getRelativePathName();
				}

				resourceLoaderRoot = HelperFileName.getFullPathNoEndSeparator(resourceLoaderRoot + "/");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return resourceLoaderRoot;
	}

	/**
	 * @author absir
	 *
	 */
	protected static enum Configure {

		METHOD {
			@Override
			public Object find(Method method) {
				return method.getAnnotation(BaMethod.class);
			}

			@Override
			public String findName(Object found) {
				return ((BaMethod) found).name();
			}

			@Override
			public void process(String name, final Object object, Object found, final Method method,
					WebJetbrickSupply webItSupply) {
				getVariableResolverBean().registerMethod(name, method);
			}
		},

		FUNCTION {
			@Override
			public Object find(Method method) {
				return method.getAnnotation(BaFunction.class);
			}

			@Override
			public String findName(Object found) {
				return ((BaFunction) found).name();
			}

			@Override
			public void process(String name, final Object object, Object found, final Method method,
					WebJetbrickSupply webItSupply) {
				getVariableResolverBean().registerFunction(name, method);
			}
		},

		TAG {
			@Override
			public Object find(Method method) {
				return method.getAnnotation(BaTag.class);
			}

			@Override
			public String findName(Object found) {
				return ((BaTag) found).name();
			}

			@Override
			public void process(String name, final Object object, Object found, final Method method,
					WebJetbrickSupply webItSupply) {
				getVariableResolverBean().registerTag(name, method);
			}
		},

		;

		/**
		 * @param method
		 * @return
		 */
		public abstract Object find(Method method);

		/**
		 * @param found
		 * @return
		 */
		public abstract String findName(Object found);

		/**
		 * @param name
		 * @param foundMethod
		 * @param webItSupply
		 */
		public abstract void process(String name, final Object object, Object found, final Method method,
				WebJetbrickSupply webItSupply);
	}

	/**
	 * @author absir
	 *
	 */
	protected class ConfigureFound {

		/** beetlConfigure */
		protected Configure configure;

		/** found */
		protected Object found;

		/**
		 * @param object
		 * @param method
		 * @param groupTemplate
		 */
		protected void process(Object object, Method method) {
			String name = configure.findName(found);
			if (KernelString.isEmpty(name)) {
				name = method.getName();
			}

			configure.process(name, object, name, method, WebJetbrickSupply.this);
		}
	}

	/** configures */
	private Configure[] configures = Configure.values();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.bean.inject.IMethodSupport#getInject(com.absir.bean.basis.
	 * BeanScope , com.absir.bean.basis.BeanDefine, java.lang.reflect.Method)
	 */
	@Override
	public ConfigureFound getInject(BeanScope beanScope, BeanDefine beanDefine, Method method) {
		for (Configure configure : configures) {
			Object found = configure.find(method);
			if (found != null) {
				ConfigureFound configureFound = new ConfigureFound();
				configureFound.configure = configure;
				configureFound.found = found;
				return configureFound;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.inject.IMethodSupport#getInjectInvoker(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.reflect.Method, java.lang.Object,
	 * java.util.Map)
	 */
	@Override
	public InjectInvoker getInjectInvoker(ConfigureFound inject, Method method, Method beanMethod, Object beanObject,
			Map<Method, Set<Object>> methodMapInjects) {
		inject.process(beanObject, method);
		return null;
	}

	/**
	 * @author absir
	 *
	 */
	public static class TagWrapper {

		/** context */
		private JetTagContext context;

		/** bodyContent */
		private String bodyContent;

		/**
		 * @param context
		 */
		public TagWrapper(JetTagContext tagContext) {
			context = tagContext;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			if (bodyContent == null) {
				bodyContent = context.getBodyContent();
			}

			return bodyContent;
		}
	}

	/**
	 * @param obj
	 * @param name
	 * @return
	 */
	public Object getter(Object obj, String name) {
		Accessor accessor = UtilAccessor.getAccessorObj(obj, name);
		return accessor == null ? null : accessor.get(obj);
	}

	/** highlightSpan */
	@Value(value = "<span ${web.view.highlight}>")
	private static String highlightSpan = "<span class=\"highlight\">";

	/**
	 * @param obj
	 * @param highlight
	 * @return
	 */
	@BaMethod
	public static Object highlight(Object obj, String highlight) {
		if (obj != null) {
			return HelperString.replace(obj.toString(), highlight, "<span>" + highlight + "</span>");
		}

		return obj;
	}

	/**
	 * @param obj
	 */
	@BaMethod
	public static void voidValue(Object obj) {
	}

	/**
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	@BaMethod
	public static String jsonValue(Object obj) throws IOException {
		return HelperJson.encode(obj);
	}

	/** ECHO_NAME */
	public static final String ECHO_NAME = WebJetbrickSupply.class.getName() + "@ECHO_NAME";

	/**
	 * @param obj
	 */
	@BaMethod
	public static void echoValue(Object obj) {
		if (obj != null) {
			echo().append(obj);
		}
	}

	/**
	 * @return
	 */
	@BaFunction
	public static StringBuilder echo() {
		return echo(true);
	}

	/**
	 * @param force
	 * @return
	 */
	@BaFunction
	public static StringBuilder echo(boolean force) {
		InterpretContext ctx = InterpretContext.current();
		Object echo = ctx.getValueStack().getValue(ECHO_NAME);
		if (echo == null || !(echo instanceof StringBuilder)) {
			if (force) {
				StringBuilder echoBuilder = new StringBuilder();
				ctx.getValueStack().setLocal(ECHO_NAME, echoBuilder);
				return echoBuilder;

			} else {
				return null;
			}

		} else {
			return (StringBuilder) echo;
		}
	}

	/**
	 * 
	 */
	@BaFunction
	public static void clear() {
		InterpretContext.current().getValueStack().setLocal(ECHO_NAME, null);
	}

	/**
	 * @param ctx
	 * @return
	 * @throws IOException
	 */
	@BaFunction
	public static InterpretContext getContext() throws IOException {
		return InterpretContext.current();
	}

	/**
	 * @param ctx
	 * @param include
	 * @return
	 * @throws IOException
	 */
	@BaFunction
	public static String pagInclude(String include) throws IOException {
		return pagInclude(include, include);
	}

	/**
	 * @param ctx
	 * @param include
	 * @param generate
	 * @return
	 * @throws IOException
	 */
	@BaFunction
	public static String pagInclude(String include, String generate) throws IOException {
		InterpretContext ctx = InterpretContext.current();
		return Pag.getInclude(include, generate, ctx, ctx.getValueStack().getValue(JetWebContext.REQUEST));
	}

	/**
	 * @param ctx
	 * @param file
	 * @throws IOException
	 */
	@BaTag
	public static void script(JetTagContext ctx) throws IOException {
		InterpretContext context = ctx.getInterpretContext();
		JetWriter originWriter = context.getWriter();
		context.setWriter(JetWriterNone.ME);
		ctx.invoke();
		context.setWriter(originWriter);
		StringBuilder echo = echo(false);
		if (echo != null) {
			originWriter.print(echo.toString());
			clear();
		}
	}

	/**
	 * @param ctx
	 * @param name
	 */
	@BaTag
	public static void scenario(JetTagContext ctx, String name) {
		Object request = ctx.getValueStack().getValue(JetWebContext.REQUEST);
		if (request != null && request instanceof ServletRequest) {
			if (Scenario.requestName((ServletRequest) request, name)) {
				ctx.invoke();
			}
		}
	}

	/**
	 * @param ctx
	 * @param file
	 */
	@BaTag
	public static void layout(JetTagContext ctx, String file) {
		layout(ctx, file, null);
	}

	/**
	 * @param ctx
	 * @param file
	 * @param parameters
	 */
	@BaTag
	public static void layout(JetTagContext ctx, String file, Map<String, Object> parameters) {
		ctx.getValueStack().setLocal("bodyContent", ctx.getBodyContent());
		file = PathUtils.getRelativePath(ctx.getInterpretContext().getTemplate().getName(), file);
		ctx.getInterpretContext().doIncludeCall(file, parameters, null);
	}

	/**
	 * @param ctx
	 * @param file
	 */
	@BaTag
	public static void preLayout(JetTagContext ctx, String file) {
		preLayout(ctx, file, null);
	}

	/**
	 * @param ctx
	 * @param file
	 * @param parameters
	 */
	@BaTag
	public static void preLayout(JetTagContext ctx, String file, Map<String, Object> parameters) {
		ctx.getValueStack().setLocal("bodyContent", new TagWrapper(ctx));
		file = PathUtils.getRelativePath(ctx.getInterpretContext().getTemplate().getName(), file);
		ctx.getInterpretContext().doIncludeCall(file, parameters, null);
	}
}
