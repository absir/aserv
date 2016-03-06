/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-5 下午2:31:23
 */
package com.absir.core.kernel;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes" })
public abstract class KernelString {

	/**
	 * @param object
	 * @return null or String
	 */
	public static String valueOf(Object object) {
		if (object == null) {
			return null;

		} else {
			return String.valueOf(object);
		}
	}

	/**
	 * @param string
	 * @return
	 */
	public static boolean empty(String string) {
		return string.length() == 0;
	}

	/**
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string) {
		return string == null || empty(string);
	}

	/**
	 * @param chr
	 * @return
	 */
	public static boolean capitalize(char chr) {
		return chr >= 'A' && chr <= 'Z';
	}

	/**
	 * @param chr
	 * @return
	 */
	public static boolean unCapitalize(char chr) {
		return chr >= 'a' && chr <= 'z';
	}

	/**
	 * @param string
	 * @return
	 */
	public static String capitalize(String string) {
		char chr = string.charAt(0);
		if (capitalize(chr)) {
			return string;

		} else if (string.length() > 1) {
			if (capitalize(string.charAt(1))) {
				return string;
			}
		}

		char[] data = string.toCharArray();
		data[0] = Character.toUpperCase(chr);
		return new String(data);
	}

	/**
	 * @param string
	 * @return
	 */
	public static String unCapitalize(String string) {
		char chr = string.charAt(0);
		if (unCapitalize(chr)) {
			return string;

		} else if (string.length() > 1) {
			if (capitalize(string.charAt(1))) {
				return string;
			}
		}

		char[] data = string.toCharArray();
		data[0] = Character.toLowerCase(chr);
		return new String(data);
	}

	/**
	 * @param string
	 * @return
	 */
	public static int indexUncapitalizeOf(String string) {
		int length = string.length();
		for (int i = 0; i < length; i++) {
			if (unCapitalize(string.charAt(i))) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * @param string
	 * @return
	 */
	public static int lastIndexCapitalizeOf(String string) {
		int last = string.length() - 1;
		for (; last >= 0; last--) {
			if (capitalize(string.charAt(last))) {
				return last;
			}
		}

		return -1;
	}

	/**
	 * @param string
	 * @return
	 */
	public static String lastCapitalizeString(String string) {
		return rightString(string, string.length() - lastIndexCapitalizeOf(string));
	}

	/**
	 * @param string
	 * @return
	 */
	public static String subLastCapitalizeString(String string) {
		return subLastString(string, lastIndexCapitalizeOf(string));
	}

	/**
	 * @param string
	 * @return
	 */
	public static String camelUncapitalize(String string) {
		int length = string.length();
		int underline = 0;
		StringBuilder stringBuilder = null;
		for (int i = 0; i < length; i++) {
			char chr = string.charAt(i);
			if (chr == '_') {
				if (underline > 0) {
					if (underline == 2) {
						stringBuilder = new StringBuilder(length * 2);
						stringBuilder.append(leftString(string, i));
					}

					if (i++ < length) {
						stringBuilder.append(Character.toUpperCase(string.charAt(i)));
					}

					underline = -1;
				}

			} else {
				if (underline <= 0) {
					underline += 2;
				}

				if (underline != 2) {
					stringBuilder.append(chr);
				}
			}
		}

		return stringBuilder == null ? string : stringBuilder.toString();
	}

	/**
	 * @param string
	 * @return
	 */
	public static String camelUnderline(String string) {
		int length = string.length();
		int capitalize = 0;
		StringBuilder stringBuilder = null;
		for (int i = 0; i < length; i++) {
			char chr = string.charAt(i);
			if (capitalize(chr)) {
				if (capitalize > 0) {
					if (capitalize == 2) {
						stringBuilder = new StringBuilder(length * 2);
						stringBuilder.append(leftString(string, i));
					}

					stringBuilder.append('_');
					stringBuilder.append(Character.toLowerCase(chr));
					capitalize = -1;

				} else {
					stringBuilder.append(chr);
				}

			} else {
				if (capitalize <= 0) {
					capitalize += 2;
				}

				if (capitalize != 2) {
					stringBuilder.append(chr);
				}
			}
		}

		return stringBuilder == null ? string : stringBuilder.toString();
	}

	/**
	 * @param string
	 * @return
	 */
	public static String camelInvertUnderline(String string) {
		int index = lastIndexCapitalizeOf(string);
		return camelUnderline(index > 0 ? KernelString.unCapitalize(rightString(string, string.length() - index))
				+ leftString(string, index) : string);
	}

	/**
	 * @param stringBuilder
	 * @param value
	 * @param repeatCount
	 */
	public static void repeatString(StringBuilder stringBuilder, String value, int repeatCount) {
		while (repeatCount-- > 0) {
			stringBuilder.append(value);
		}
	}

	/**
	 * @param prefix
	 * @param value
	 * @param repeatCount
	 * @param suffix
	 * @return
	 */
	public static String repeateString(Object prefix, String value, int repeatCount, Object suffix) {
		StringBuilder stringBuilder = new StringBuilder();
		if (prefix != null) {
			stringBuilder.append(prefix);
		}

		repeatString(stringBuilder, value, repeatCount);
		if (suffix != null) {
			stringBuilder.append(suffix);
		}

		return stringBuilder.toString();
	}

	/**
	 * @param value
	 * @return
	 */
	public static String transferred(String value) {
		return '"' + value.replace("\"", "\\\"") + '"';
	}

	/**
	 * @param value
	 * @return
	 */
	public static String unTransferred(String value) {
		value = value.trim();
		int length = value.length();
		if (length <= 1) {
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

	/**
	 * @param string
	 * @param index
	 * @return
	 */
	public static String lastString(String string, int index) {
		if (index >= 0) {
			return string.substring(index + 1);
		}

		return string;
	}

	/**
	 * @param string
	 * @param index
	 * @return
	 */
	public static String subLastString(String string, int index) {
		if (index >= 0) {
			string = string.substring(0, index);
		}

		return string;
	}

	/**
	 * @param string
	 * @param ch
	 * @return
	 */
	public static String lastString(String string, char ch) {
		return lastString(string, string.lastIndexOf(ch));
	}

	/**
	 * @param string
	 * @param ch
	 * @return
	 */
	public static String subLastString(String string, char ch) {
		return subLastString(string, string.lastIndexOf(ch));
	}

	/**
	 * @param string
	 * @param length
	 * @return
	 */
	public static String leftString(String string, int length) {
		if (length <= 0) {
			return KernelLang.NULL_STRING;
		}

		return string.substring(0, length);
	}

	/**
	 * @param string
	 * @param length
	 * @return
	 */
	public static String leftSubString(String string, int length) {
		if (length <= 0) {
			return string;
		}

		return string.substring(length);
	}

	/**
	 * @param string
	 * @param length
	 * @return
	 */
	public static String rightString(String string, int length) {
		if (length <= 0) {
			return KernelLang.NULL_STRING;
		}

		length = string.length() - length;
		if (length <= 0) {
			return string;
		}

		return string.substring(length);
	}

	/**
	 * @param string
	 * @param length
	 * @return
	 */
	public static String rightSubString(String string, int length) {
		if (length <= 0) {
			return string;
		}

		length = string.length() - length;
		if (length <= 0) {
			return KernelLang.NULL_STRING;
		}

		return string.substring(0, length);
	}

	/**
	 * @param str
	 * @param strs
	 * @return
	 */
	public static boolean startStrings(String str, String[] strs) {
		if (strs == null) {
			return false;
		}

		for (String s : strs) {
			if (str.startsWith(s)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param str
	 * @param strs
	 * @return
	 */
	public static boolean matchStrings(String str, String[] strs) {
		if (strs == null) {
			return false;
		}

		for (String s : strs) {
			if (str.indexOf(s) >= 0) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param string
	 * @param target
	 * @param replacement
	 * @return
	 */
	public static String replaceLast(String string, String target, String replacement) {
		if (isEmpty(string) || isEmpty(target)) {
			return string;
		}

		int index = string.lastIndexOf(target);
		return string.substring(0, index) + replacement + string.substring(index + target.length());
	}

	/** SEQUENCE_SIZE */
	public static final int SEQUENCE_SIZE = 'z' - 'a' + 1;

	/**
	 * @param sequence
	 * @return
	 */
	public static String getSequenceString(int sequence) {
		StringBuilder stringBuilder = new StringBuilder();
		while ((sequence -= SEQUENCE_SIZE) > SEQUENCE_SIZE) {
			stringBuilder.append('z');
		}

		if (sequence >= 0) {
			stringBuilder.append((char) sequence);
		}

		return stringBuilder.toString();
	}

	/**
	 * @param sequence
	 * @return
	 */
	public static String nextSequenceString(String sequence) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(rightSubString(sequence, 1));
		char chr = (char) (sequence.charAt(sequence.length() - 1) + 1);
		if (chr <= 'z') {
			stringBuilder.append(chr);

		} else {
			stringBuilder.append("za");
		}

		return stringBuilder.toString();
	}

	/**
	 * @param str
	 * @param to
	 * @return
	 */
	public static int compare(String str, String to) {
		return compare(str, to, str.length(), to.length());
	}

	/**
	 * @param str
	 * @param to
	 * @param m
	 * @param n
	 * @return
	 */
	public static int compare(String str, String to, int m, int n) {
		if (m == 0) {
			return n;
		}

		if (n == 0) {
			return m;
		}

		int matrix[][];
		matrix = new int[m + 1][n + 1];
		for (int i = 0; i <= m; i++) {
			matrix[i][0] = i;
		}

		for (int j = 0; j <= n; j++) {
			matrix[0][j] = j;
		}

		for (int i = 0; i < m; i++) {
			char chr = str.charAt(i);
			for (int j = 0; j < n; j++) {
				matrix[i + 1][j + 1] = KernelLang.min(matrix[i][j + 1] + 1, matrix[i + 1][j] + 1,
						matrix[i][j] + (chr == to.charAt(j) ? 0 : 1));
			}
		}

		return matrix[m][n];
	}

	/**
	 * @param str
	 * @param to
	 * @return
	 */
	public static float similar(String str, String to) {
		if (str == to) {
			return 1;
		}

		if (str == null) {
			return 0;
		}

		int m = str.length();
		int n = to.length();
		if (m == 0 && n == 0) {
			return 1;
		}

		return 1.0f - (float) compare(str, to, m, n) / Math.max(str.length(), to.length());
	}

	/**
	 * @author absir
	 * 
	 */
	public static interface ImplodeBuilder {

		/**
		 * @param builder
		 * @param glue
		 * @param index
		 * @param value
		 * @param target
		 * @return
		 */
		public Object glue(StringBuilder builder, Object glue, int index, Object value, Object target);
	}

	/**
	 * @param array
	 * @param glues
	 * @return
	 */
	public static String implode(Object[] array, Object... glues) {
		return implode(array, null, null, glues);
	}

	/**
	 * @param array
	 * @param imploder
	 * @param target
	 * @param glues
	 * @return
	 */
	public static String implode(Object[] array, ImplodeBuilder imploder, Object target, Object... glues) {
		return implodeOffset(array, 0, 0, imploder, target, glues);
	}

	/**
	 * @param array
	 * @param beganIndex
	 * @param endIndex
	 * @param imploder
	 * @param target
	 * @param glues
	 * @return
	 */
	public static String implodeOffset(Object[] array, int beganIndex, int endIndex, ImplodeBuilder imploder, Object target,
			Object... glues) {
		int length = array.length;
		if (endIndex == 0 || endIndex > length) {
			endIndex = length;
		}

		if (beganIndex < 0) {
			beganIndex = 0;
		}

		StringBuilder builder = new StringBuilder();
		Object glue = null;
		int index = 0;
		length = glues.length;
		for (; beganIndex < endIndex; beganIndex++) {
			Object value = array[beganIndex];
			if (imploder == null) {
				if (glue != null) {
					builder.append(glue);
				}

				builder.append(value);

			} else {
				implode(imploder, builder, index, value, target, glue);
			}

			if (++index >= length) {
				index = 0;
			}

			glue = glues[index];
		}

		return builder.toString();
	}

	/**
	 * @param collection
	 * @param glues
	 * @return
	 */
	public static String implode(Collection collection, Object... glues) {
		return implode(collection, null, null, glues);
	}

	/**
	 * @param collection
	 * @param imploder
	 * @param target
	 * @param glues
	 * @return
	 */
	public static String implode(Collection collection, ImplodeBuilder imploder, Object target, Object... glues) {
		StringBuilder builder = new StringBuilder();
		Object glue = null;
		int index = 0;
		int length = glues.length;
		for (Object value : collection) {
			if (imploder == null) {
				if (glue != null) {
					builder.append(glue);
				}

				builder.append(value);

			} else {
				implode(imploder, builder, index, value, target, glue);
			}

			if (++index >= length) {
				index = 0;
			}

			glue = glues[index];
		}

		return builder.toString();
	}

	/**
	 * @param map
	 * @param glues
	 * @return
	 */
	public static String implode(Map map, Object... glues) {
		return implode(map, null, null, glues);
	}

	/**
	 * @param map
	 * @param imploder
	 * @param target
	 * @param glues
	 * @return
	 */
	public static String implode(Map<?, ?> map, ImplodeBuilder imploder, Object target, Object... glues) {
		StringBuilder builder = new StringBuilder();
		Object glue = null;
		int index = 0;
		int length = glues.length;
		for (Entry entry : map.entrySet()) {
			if (imploder == null) {
				if (glue != null) {
					builder.append(glue);
				}

				builder.append(entry.getKey());

			} else {
				implode(imploder, builder, index, entry.getKey(), target, glue);
			}

			if (++index >= length) {
				index = 0;
			}

			glue = glues[index];
			if (imploder == null) {
				if (glue != null) {
					builder.append(glue);
				}

				builder.append(entry.getValue());

			} else {
				implode(imploder, builder, index, entry.getValue(), target, glue);
			}

			if (++index >= length) {
				index = 0;
			}

			glue = glues[index];
		}

		return builder.toString();
	}

	/**
	 * @param imploder
	 * @param builder
	 * @param glue
	 * @param index
	 * @param value
	 * @param target
	 */
	protected static void implode(ImplodeBuilder imploder, StringBuilder builder, int index, Object value, Object target,
			Object glue) {
		value = imploder.glue(builder, glue, index, value, target);
		if (value != builder) {
			if (glue != null) {
				builder.append(glue);
			}

			builder.append(value);
		}
	}

	/**
	 * @param iterator
	 * @param glues
	 * @return
	 */
	public static String implodeIterator(Iterator iterator, Object... glues) {
		return implodeIterator(iterator, null, null, glues);
	}

	/**
	 * @param collection
	 * @param imploder
	 * @param target
	 * @param glues
	 * @return
	 */
	public static String implodeIterator(Iterator iterator, ImplodeBuilder imploder, Object target, Object... glues) {
		StringBuilder builder = new StringBuilder();
		Object glue = null;
		int index = 0;
		int length = glues.length;
		Object value;
		while (iterator.hasNext()) {
			value = iterator.next();
			if (imploder == null) {
				if (glue != null) {
					builder.append(glue);
				}

				builder.append(value);

			} else {
				implode(imploder, builder, index, value, target, glue);
			}

			if (++index >= length) {
				index = 0;
			}

			glue = glues[index];
		}

		return builder.toString();
	}
}
