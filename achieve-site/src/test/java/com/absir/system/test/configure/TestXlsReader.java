/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-26 下午4:34:39
 */
package com.absir.system.test.configure;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import com.absir.aserv.configure.xls.XlsBase;
import com.absir.aserv.configure.xls.XlsUtils;
import com.absir.client.helper.HelperJson;
import com.absir.core.helper.HelperFile;
import com.absir.system.test.AbstractTest;

/**
 * @author absir
 * 
 */
public class TestXlsReader extends AbstractTest {

	@Test
	public void test() throws IOException {
		Class<? extends XlsBase> beanClass = XlsBase.class;
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(HelperFile.openInputStream(new File("/Users/absir/Desktop/" + beanClass.getSimpleName() + ".xls")));
		List<? extends XlsBase> beanList = XlsUtils.getXlsList(hssfWorkbook, beanClass);
		System.out.println(beanList);
		System.out.println(HelperJson.encode(beanList));
	}
}
