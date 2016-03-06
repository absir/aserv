/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年10月11日 下午3:34:37
 */
package com.absir.spring;

import java.util.Collection;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanConfig;
import com.absir.bean.basis.BeanSupply;
import com.absir.bean.config.IBeanFactoryAware;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Stopping;

/**
 * @author absir
 *
 */
@Basis
@Bean
public class SpringBeanDefineAware implements IBeanFactoryAware {

	/** CONTEXT */
	public static final ClassPathXmlApplicationContext CONTEXT = new ClassPathXmlApplicationContext();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
	 */
	@Override
	public int getOrder() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanFactoryAware#beforeRegister(com.absir.bean
	 * .core.BeanFactoryImpl)
	 */
	@Override
	public void beforeRegister(BeanFactoryImpl beanFactory) {
		BeanConfig beanConfig = beanFactory.getBeanConfig();
		String[] locations = beanConfig.getExpressionObject("spring.locations", null, String[].class);
		if (locations == null) {
			locations = new String[] { beanConfig.getClassPath() + "spring/*.xml" };
		}

		CONTEXT.setConfigLocations(locations);
		CONTEXT.start();
		beanFactory.addBeanSupply(new BeanSupply() {

			@Override
			public <T> Collection<T> getBeanObjects(Class<T> beanType) {
				try {
					return CONTEXT.getBeansOfType(beanType).values();

				} catch (Exception e) {
					return null;
				}
			}

			@Override
			public <T> T getBeanObject(String beanName, Class<T> beanType) {
				try {
					return CONTEXT.getBean(beanName, beanType);

				} catch (Exception e) {
					return null;
				}
			}

			@Override
			public <T> T getBeanObject(Class<T> beanType) {
				try {
					return CONTEXT.getBean(beanType);

				} catch (Exception e) {
					return null;
				}
			}

			@Override
			public Object getBeanObject(String beanName) {
				try {
					return CONTEXT.getBean(beanName);

				} catch (Exception e) {
					return null;
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.bean.config.IBeanFactoryAware#afterRegister(com.absir.bean.
	 * core.BeanFactoryImpl)
	 */
	@Override
	public void afterRegister(BeanFactoryImpl beanFactory) {
	}

	/**
	 * 
	 */
	@Stopping
	protected void stopping() {
		CONTEXT.stop();
	}
}
