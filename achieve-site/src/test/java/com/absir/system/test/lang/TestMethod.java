/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-4-8 下午4:13:43
 */
package com.absir.system.test.lang;

import java.io.IOException;

import javax.script.ScriptException;

import org.junit.Test;

import com.absir.system.test.AbstractTest;

/**
 * @author absir
 * 
 */
public class TestMethod extends AbstractTest {

	public interface TI<T> {

		public T t(T t);

	}

	public class TS implements TI<String> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.absir.system.test.lang.TestMethod.TI#t(java.lang.Object)
		 */
		@Override
		public String t(String t) {
			return null;
		}

	}

	public class TL implements TI<Long> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.absir.system.test.lang.TestMethod.TI#t(java.lang.Object)
		 */
		@Override
		public Long t(Long t) {
			return null;
		}

	}

	public static interface TestProxy {

		public void emptyProxy();
	}

	public static abstract class TestProxyClass implements TestProxy {

		protected String getName() {
			return "dddd";
		}
	}

	/**
	 * @param value
	 * @return
	 */
	public static String unTransferred(String value) {
		value = value.trim();
		int length = value.length();
		if (length < 1) {
			return value;
		}

		StringBuilder stringBuilder = new StringBuilder();
		int quotation = 0;
		char chr = value.charAt(0);
		if (chr == '"') {
			quotation = 1;

		} else {
			stringBuilder.append(chr);
		}

		length--;
		boolean transferred = false;
		for (int i = 1; i < length; i++) {
			chr = value.charAt(i);
			if (transferred) {
				transferred = false;
				appendTransferred(stringBuilder, chr);

			} else if (chr == '\\') {
				transferred = true;

			} else {
				stringBuilder.append(chr);
			}
		}

		chr = value.charAt(length);
		if (transferred) {
			appendTransferred(stringBuilder, chr);

		} else {
			// "" quotation
			if (quotation == 1 && chr == '"') {
				quotation = 2;

			} else {
				stringBuilder.append(chr);
			}
		}

		return quotation == 1 ? '"' + stringBuilder.toString() : stringBuilder.toString();
	}

	/**
	 * @param stringBuilder
	 * @param chr
	 */
	public static void appendTransferred(StringBuilder stringBuilder, char chr) {
		switch (chr) {
		case 't':
			stringBuilder.append("\t");
			break;
		case 'r':
			stringBuilder.append("\r");
			break;
		case 'n':
			stringBuilder.append("\n");
			break;
		case '"':
			stringBuilder.append('"');
			break;
		case '\'':
			stringBuilder.append('\'');
			break;

		default:
			stringBuilder.append('\\');
			stringBuilder.append(chr);
			break;
		}
	}

	public void dump() {
		new Exception().printStackTrace();
	}

	void test(String name, Object test, Object nl, Character t) {
		System.out.println(name);
	}

	@Test
	public void main() throws IOException, ScriptException, NoSuchMethodException {
		
		//IEntityMerge<T>

		// System.out.println(TI.class.getGenericSuperclass());

		// System.out.println(DynaBinder.to("name", List.class));
		// System.out.println(DynaBinder.to("name", String[].class));
		// System.out.println(HelperRandom.randSecendId(7));
		// System.out.println(HelperRandom.randSecendId(8));
		// System.out.println(HelperRandom.randSecendId(9));
		// System.out.println(HelperRandom.randSecendId(31));
		// System.out.println(HelperRandom.randSecendId(32));
		// System.out.println(HelperRandom.randSecendId(33));
		// //[^/\\]*([/\\]*)
		// String regx = "^([/\\\\]*)[^/\\\\]*([/\\\\]+)";
		// System.out.println("\\/ddd/a/sdsd".replaceFirst(regx, "$1admin$2"));
		// System.out.println("ddd/a/sdsd".replaceFirst(regx, "$1admin$2"));
		// System.out.println("/ddd\\a/sdsd".replaceFirst(regx, "$1admin$2"));
	}
}
