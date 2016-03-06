/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-10-28 上午10:56:46
 */
package com.absir.aserv.system.helper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.FilterTemplate;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;

/**
 * @author absir
 * 
 */
public class HelperQuery {

	/** NAME_PATTERN */
	private static final Pattern NAME_PATTERN = Pattern.compile("o\\.(.*?)([\\s|=|>|<]+)", Pattern.CASE_INSENSITIVE);

	/**
	 * @param entityClass
	 * @param field
	 * @return
	 */
	public static Class<?> getFieldType(Class<?> entityClass, Field field) {
		Class<?> fieldType = field.getType();
		if (fieldType == Serializable.class && "id".equals(field.getName()) && IBase.class.isAssignableFrom(entityClass)) {
			fieldType = KernelClass.argumentClass(entityClass);
		}

		return fieldType;
	}

	/**
	 * @param entityClass
	 * @param conditions
	 * @return
	 */
	public static FilterTemplate<Object> getConditionFilter(Class<?> entityClass, List<Object> conditions) {
		int size = conditions.size();
		if (size == 0) {
			return null;
		}

		final List<FilterTemplate<Object>> filterTemplates = new ArrayList<KernelLang.FilterTemplate<Object>>(conditions.size() / 2 + 1);
		for (int i = 0; i < size; i += 2) {
			String conditon = (String) conditions.get(i);
			Matcher matcher = NAME_PATTERN.matcher(conditon);
			if (matcher.find()) {
				Field field = KernelReflect.declaredField(entityClass, matcher.group(1));
				if (field == null) {
					continue;
				}

				Object value = conditions.get(i + 1);
				FilterTemplate<Object> filterTemplate = getConditionFilter(field, getFieldType(entityClass, field), matcher, value);
				if (filterTemplate != null) {
					filterTemplates.add(filterTemplate);
				}
			}
		}

		if (filterTemplates.size() == 0) {
			return null;

		} else if (filterTemplates.size() == 1) {
			return filterTemplates.get(0);

		} else {
			return new FilterTemplate<Object>() {

				@Override
				public boolean doWith(Object template) throws BreakException {
					for (FilterTemplate<Object> filterTemplate : filterTemplates) {
						if (!filterTemplate.doWith(template)) {
							return false;
						}
					}

					return true;
				}
			};
		}
	}

	/**
	 * @param cls
	 * @return
	 */
	private static Class<?> getNumberClass(Class<?> cls) {
		if (Date.class.isAssignableFrom(cls)) {
			return int.class;

		}

		return KernelClass.getMatchNumberClass(cls);
	}

	/**
	 * @param field
	 * @param matcher
	 * @param value
	 * @return
	 */
	private static FilterTemplate<Object> getConditionFilter(final Field field, Class<?> fieldType, Matcher matcher, final Object value) {
		if (value == null) {
			if (!Object.class.isAssignableFrom(fieldType)) {
				return null;
			}

			return new FilterTemplate<Object>() {

				@Override
				public boolean doWith(Object template) throws BreakException {
					return KernelReflect.get(template, field) == null;
				}
			};
		}

		if (String.class.isAssignableFrom(fieldType)) {
			if (value instanceof String) {
				String str = ((String) value).toLowerCase();
				if (str.length() > 1) {
					if (str.startsWith("%")) {
						if (str.length() > 2 && str.endsWith("%")) {
							// LIKE %val%
							final String val = str.substring(1, str.length() - 1);
							return new FilterTemplate<Object>() {

								@Override
								public boolean doWith(Object template) throws BreakException {
									String str = (String) KernelReflect.get(template, field);
									return str != null && (str.toLowerCase()).indexOf(val) >= 0;
								}
							};

						} else {
							// LIKE %val
							final String val = str.substring(1, str.length());
							return new FilterTemplate<Object>() {

								@Override
								public boolean doWith(Object template) throws BreakException {
									String str = (String) KernelReflect.get(template, field);
									return str != null && (str.toLowerCase()).endsWith(val);
								}
							};
						}

					} else if (str.endsWith("%")) {
						// LIKE val%
						final String val = str.substring(0, str.length() - 1);
						return new FilterTemplate<Object>() {

							@Override
							public boolean doWith(Object template) throws BreakException {
								String str = (String) KernelReflect.get(template, field);
								return str != null && (str.toLowerCase()).startsWith(val);
							}
						};
					}
				}
			}

			// LIKE val
			final String val = value.toString().toLowerCase();
			return new FilterTemplate<Object>() {

				@Override
				public boolean doWith(Object template) throws BreakException {
					String str = (String) KernelReflect.get(template, field);
					return str != null && (str.toLowerCase()).equals(val);
				}
			};
		}

		Class<?> numberClass = getNumberClass(fieldType);
		if (numberClass != null) {
			byte option = 0;
			if (matcher.group(2).indexOf('>') >= 0) {
				if (matcher.group(2).indexOf('=') >= 0) {
					// >= val
					option = 1;

				} else {
					// > val
					option = 2;
				}

			} else if (matcher.group(2).indexOf('<') >= 0) {
				if (matcher.group(2).indexOf('=') >= 0) {
					// <= val
					option = 3;

				} else {
					// < val
					option = 4;
				}
			}

			final byte opt = option;
			if (numberClass == int.class) {
				final int val = KernelDyna.to(value, int.class);
				return new FilterTemplate<Object>() {

					@Override
					public boolean doWith(Object template) throws BreakException {
						Object value = KernelReflect.get(template, field);
						if (value == null) {
							return false;
						}

						int v = KernelDyna.to(value, int.class);
						switch (opt) {
						case 1:
							return v >= val;

						case 2:
							return v > val;

						case 3:
							return v <= val;

						case 4:
							return v < val;

						default:
							return v == val;
						}
					}
				};

			} else if (numberClass == long.class) {
				final long val = KernelDyna.to(value, long.class);
				return new FilterTemplate<Object>() {

					@Override
					public boolean doWith(Object template) throws BreakException {
						Object value = KernelReflect.get(template, field);
						if (value == null) {
							return false;
						}

						long v = KernelDyna.to(value, long.class);
						switch (opt) {
						case 1:
							return v >= val;

						case 2:
							return v > val;

						case 3:
							return v <= val;

						case 4:
							return v < val;

						default:
							return v == val;
						}
					}
				};

			} else if (numberClass == float.class) {
				final float val = KernelDyna.to(value, float.class);
				return new FilterTemplate<Object>() {

					@Override
					public boolean doWith(Object template) throws BreakException {
						Object value = KernelReflect.get(template, field);
						if (value == null) {
							return false;
						}

						float v = KernelDyna.to(value, float.class);
						switch (opt) {
						case 1:
							return v >= val;

						case 2:
							return v > val;

						case 3:
							return v <= val;

						case 4:
							return v < val;

						default:
							return v == val;
						}
					}
				};

			} else if (numberClass == double.class) {
				final double val = KernelDyna.to(value, double.class);
				return new FilterTemplate<Object>() {

					@Override
					public boolean doWith(Object template) throws BreakException {
						Object value = KernelReflect.get(template, field);
						if (value == null) {
							return false;
						}

						double v = KernelDyna.to(value, double.class);
						switch (opt) {
						case 1:
							return v >= val;

						case 2:
							return v > val;

						case 3:
							return v <= val;

						case 4:
							return v < val;

						default:
							return v == val;
						}
					}
				};
			}

			final int val = KernelDyna.to(value, int.class);
			return new FilterTemplate<Object>() {

				@Override
				public boolean doWith(Object template) throws BreakException {
					Object value = KernelReflect.get(template, field);
					return value != null && KernelDyna.to(value, int.class) >= val;
				}
			};
		}

		// equals val
		final Object val = value;
		return new FilterTemplate<Object>() {

			@Override
			public boolean doWith(Object template) throws BreakException {
				Object value = KernelReflect.get(template, field);
				return value != null && value.equals(val);
			}
		};
	}

	/**
	 * @param entityClass
	 * @param orderQueue
	 * @return
	 */
	public static Comparator<Object> getComparator(Class<?> entityClass, String orderQueue) {
		if (KernelString.isEmpty(orderQueue)) {
			return null;
		}

		String[] orderStrs = orderQueue.split(",");
		final List<Comparator<Object>> comparators = new ArrayList<Comparator<Object>>();
		for (String orderStr : orderStrs) {
			Matcher matcher = NAME_PATTERN.matcher(orderStr);
			if (matcher.find()) {
				Field field = KernelReflect.declaredField(entityClass, matcher.group(1));
				if (field == null) {
					continue;
				}

				Comparator<Object> comparator = getComparator(field, getFieldType(entityClass, field), matcher, orderStr);
				if (comparator != null) {
					comparators.add(comparator);
				}
			}
		}

		if (comparators.size() == 0) {
			return null;

		} else if (comparators.size() == 1) {
			return comparators.get(0);

		} else {
			return new Comparator<Object>() {

				@Override
				public int compare(Object o1, Object o2) {
					for (Comparator<Object> comparator : comparators) {
						int compare = comparator.compare(o1, o2);
						if (compare != 0) {
							return compare;
						}
					}

					return 0;
				}
			};
		}
	}

	/**
	 * @param field
	 * @param fieldType
	 * @param matcher
	 * @param orderStr
	 * @return
	 */
	private static Comparator<Object> getComparator(final Field field, Class<?> fieldType, Matcher matcher, String orderStr) {
		final boolean opt = matcher.end() < orderStr.length() - 3 && orderStr.substring(matcher.end()).toUpperCase().indexOf("DESC") >= 0;
		if (String.class.isAssignableFrom(fieldType)) {
			return new Comparator<Object>() {

				@Override
				public int compare(Object o1, Object o2) {
					o1 = KernelReflect.get(o1, field);
					o2 = KernelReflect.get(o2, field);
					int compare = KernelObject.compare(o1, o2);
					if (compare == 0) {
						compare = ((String) o1).compareTo((String) o2);
					}

					if (opt) {
						compare = -compare;
					}

					return compare;
				}
			};
		}

		Class<?> numberClass = getNumberClass(fieldType);
		if (numberClass != null) {
			if (numberClass == int.class) {
				return new Comparator<Object>() {

					@Override
					public int compare(Object o1, Object o2) {
						o1 = KernelReflect.get(o1, field);
						o2 = KernelReflect.get(o2, field);
						int compare = KernelObject.compare(o1, o2);
						if (compare == 0) {
							compare = KernelDyna.to(o1, int.class) - KernelDyna.to(o2, int.class);
						}

						if (opt) {
							compare = -compare;
						}

						return compare;
					}
				};

			} else if (numberClass == long.class) {
				return new Comparator<Object>() {

					@Override
					public int compare(Object o1, Object o2) {
						o1 = KernelReflect.get(o1, field);
						o2 = KernelReflect.get(o2, field);
						int compare = KernelObject.compare(o1, o2);
						if (compare == 0) {
							long cmp = KernelDyna.to(o1, long.class) - KernelDyna.to(o2, long.class);
							compare = cmp == 0 ? 0 : cmp < 0 ? -1 : 1;
						}

						if (opt) {
							compare = -compare;
						}

						return compare;
					}
				};

			} else if (numberClass == float.class) {

				return new Comparator<Object>() {

					@Override
					public int compare(Object o1, Object o2) {
						o1 = KernelReflect.get(o1, field);
						o2 = KernelReflect.get(o2, field);
						int compare = KernelObject.compare(o1, o2);
						if (compare == 0) {
							float cmp = KernelDyna.to(o1, float.class) - KernelDyna.to(o2, float.class);
							compare = cmp == 0 ? 0 : cmp < 0 ? -1 : 1;
						}

						if (opt) {
							compare = -compare;
						}

						return compare;
					}
				};

			} else if (numberClass == double.class) {

				return new Comparator<Object>() {

					@Override
					public int compare(Object o1, Object o2) {
						o1 = KernelReflect.get(o1, field);
						o2 = KernelReflect.get(o2, field);
						int compare = KernelObject.compare(o1, o2);
						if (compare == 0) {
							double cmp = KernelDyna.to(o1, double.class) - KernelDyna.to(o2, double.class);
							compare = cmp == 0 ? 0 : cmp < 0 ? -1 : 1;
						}

						if (opt) {
							compare = -compare;
						}

						return compare;
					}
				};
			}
		}

		return new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				o1 = KernelReflect.get(o1, field);
				o2 = KernelReflect.get(o2, field);
				int compare = KernelObject.compare(o1, o2);
				if (compare == 0) {
					compare = o1.hashCode() - o2.hashCode();
				}

				if (opt) {
					compare = -compare;
				}

				return compare;
			}
		};
	}
}
