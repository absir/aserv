/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-3-5 下午2:31:23
 */
package com.absir.core.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class KernelObject {

	/**
	 * @param value
	 * @param defaultValue
	 * @return
	 */
	public static <T> T getValue(T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}

	/**
	 * @param obj
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean set(Object obj, String name, Object value) {
		return declaredSet(obj, name, 0, false, value);
	}

	/**
	 * @param obj
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean declaredSet(Object obj, String name, Object value) {
		return declaredSet(obj, name, 0, true, value);
	}

	/**
	 * @param obj
	 * @param name
	 * @param ancest
	 * @param declared
	 * @param value
	 * @return
	 */
	public static boolean declaredSet(Object obj, String name, int ancest, boolean declared, Object value) {
		Field field;
		if (obj instanceof Class) {
			field = KernelReflect.declaredField((Class) obj, name, ancest, declared);
			field = KernelReflect.memberStatic(field);

		} else {
			field = KernelReflect.declaredField(obj.getClass(), name, ancest, declared);
		}

		return KernelReflect.set(obj, field, value);
	}

	/**
	 * @param obj
	 * @param name
	 * @return
	 */
	public static Object get(Object obj, String name) {
		return declaredGet(obj, name, 0, false);
	}

	/**
	 * @param obj
	 * @param name
	 * @return
	 */
	public static Object declaredGet(Object obj, String name) {
		return declaredGet(obj, name, 0, true);
	}

	/**
	 * @param obj
	 * @param name
	 * @param ancest
	 * @param declared
	 * @return
	 */
	public static Object declaredGet(Object obj, String name, int ancest, boolean declared) {
		Field field;
		if (obj instanceof Class) {
			field = KernelReflect.declaredField((Class) obj, name, ancest, declared);
			field = KernelReflect.memberStatic(field);

		} else {
			field = KernelReflect.declaredField(obj.getClass(), name, ancest, declared);
		}

		return KernelReflect.get(obj, field);
	}

	/**
	 * @param obj
	 * @param name
	 * @param args
	 * @return
	 */
	public static Object send(Object obj, String name, Object... args) {
		return send(obj, name, 0, true, true, KernelClass.parameterTypes(args), args);
	}

	/**
	 * @param obj
	 * @param name
	 * @param parameterTypes
	 * @param args
	 * @return
	 */
	public static Object send(Object obj, String name, Class[] parameterTypes, Object... args) {
		return send(obj, name, 0, false, false, parameterTypes, args);
	}

	/**
	 * @param obj
	 * @param name
	 * @param ancest
	 * @param assignable
	 * @param cacheable
	 * @param parameterTypes
	 * @param args
	 * @return
	 */
	public static Object send(Object obj, String name, int ancest, boolean assignable, boolean cacheable, Class[] parameterTypes,
			Object... args) {
		return declaredSend(obj, name, ancest, false, assignable, cacheable, parameterTypes, args);
	}

	/**
	 * @param obj
	 * @param name
	 * @param args
	 * @return
	 */
	public static Object declaredSend(Object obj, String name, Object... args) {
		return declaredSendArray(obj, name, args);
	}

	/**
	 * @param obj
	 * @param name
	 * @param args
	 * @return
	 */
	public static Object declaredSendArray(Object obj, String name, Object[] args) {
		return declaredSend(obj, name, 0, true, true, true, KernelClass.parameterTypes(args), args);
	}

	/**
	 * @param obj
	 * @param name
	 * @param parameterTypes
	 * @param args
	 * @return
	 */
	public static Object declaredSend(Object obj, String name, Class[] parameterTypes, Object... args) {
		return declaredSend(obj, name, 0, true, false, false, parameterTypes, args);
	}

	/**
	 * @param obj
	 * @param name
	 * @param ancest
	 * @param declared
	 * @param assignable
	 * @param cacheable
	 * @param parameterTypes
	 * @param args
	 * @return
	 */
	public static Object declaredSend(Object obj, String name, int ancest, boolean declared, boolean assignable, boolean cacheable,
			Class[] parameterTypes, Object... args) {
		Method method;
		if (obj instanceof Class) {
			method = KernelReflect.assignableMethod((Class) obj, name, ancest, declared, assignable, cacheable, parameterTypes);
			method = KernelReflect.memberStatic(method);

		} else {
			method = KernelReflect.assignableMethod(obj.getClass(), name, ancest, declared, assignable, cacheable, parameterTypes);
		}

		return KernelReflect.invoke(obj, method, args);
	}

	/**
	 * @param obj
	 * @param field
	 * @param value
	 * @return
	 */
	public static boolean setter(Object obj, Field field, Object value) {
		return setter(obj, field.getName(), field.getType(), value);
	}

	/**
	 * @param obj
	 * @param field
	 * @param value
	 * @return
	 */
	public static boolean setter(Object obj, String field, Object value) {
		return setter(obj, field, value.getClass(), value);
	}

	/**
	 * @param obj
	 * @param field
	 * @param fieldType
	 * @param value
	 * @return
	 */
	public static boolean setter(Object obj, String field, Class fieldType, Object value) {
		Method method = KernelClass.setter(obj.getClass(), field, fieldType);
		if (method != null) {
			if (KernelReflect.invoke(obj, false, method, value) == null) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param obj
	 * @param field
	 * @param value
	 * @return
	 */
	public static boolean publicSetter(Object obj, Field field, Object value) {
		if (setter(obj, field, value)) {
			return true;
		}

		if (Modifier.isPublic(field.getModifiers())) {
			if (KernelReflect.set(obj, field, value)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param obj
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public static boolean publicSetter(Object obj, String field, Object value) {
		Method method = KernelClass.setter(obj.getClass(), field, value.getClass());
		if (method == null) {
			return set(obj, field, value);

		} else {
			return KernelReflect.run(obj, method, value);
		}
	}

	/**
	 * @param obj
	 * @param field
	 * @param value
	 * @return
	 */
	public static boolean declaredSetter(Object obj, Field field, Object value) {
		Method method = KernelClass.declaredSetter(obj.getClass(), field);
		if (method == null) {
			if (KernelReflect.set(obj, field, value)) {
				return true;
			}

		} else {
			return KernelReflect.run(obj, method, value);
		}

		return false;
	}

	/**
	 * @param obj
	 * @param field
	 * @return
	 */
	public static Object getter(Object obj, Field field) {
		return getter(obj, field.getName(), field.getType());
	}

	/**
	 * @param obj
	 * @param field
	 * @return
	 */
	public static Object getter(Object obj, String field) {
		return getter(obj, field, Object.class);
	}

	/**
	 * @param obj
	 * @param field
	 * @param fieldType
	 * @return
	 */
	public static Object getter(Object obj, String field, Class fieldType) {
		Method method = KernelClass.getter(obj.getClass(), field, fieldType);
		if (method != null) {
			return KernelReflect.invoke(obj, method);
		}

		return null;
	}

	/**
	 * @param obj
	 * @param field
	 * @return
	 */
	public static Object publicGetter(Object obj, Field field) {
		Method method = KernelClass.getter(obj.getClass(), field);
		if (method == null) {
			if (Modifier.isPublic(field.getModifiers())) {
				return KernelReflect.get(obj, field);
			}

		} else {
			return KernelReflect.invoke(obj, method);
		}

		return null;
	}

	/**
	 * @param obj
	 * @param field
	 * @return
	 */
	public static Object publicGetter(Object obj, String field) {
		Method method = KernelClass.getter(obj.getClass(), field);
		if (method == null) {
			return get(obj, field);

		} else {
			return KernelReflect.invoke(obj, method);
		}
	}

	/**
	 * @param obj
	 * @param field
	 * @return
	 */
	public static Object declaredGetter(Object obj, Field field) {
		Method method = KernelClass.getter(obj.getClass(), field);
		if (method == null) {
			return KernelReflect.get(obj, field);

		} else {
			return KernelReflect.invoke(obj, method);
		}
	}

	/**
	 * @param obj
	 * @param field
	 * @return
	 */
	public static Object declaredGetter(Object obj, String field) {
		Method method = KernelClass.getter(obj.getClass(), field);
		if (method == null) {
			return declaredGet(obj, field);

		} else {
			return KernelReflect.invoke(obj, method);
		}
	}

	/**
	 * @param obj
	 * @param target
	 * @return
	 */
	public static Object expressGetter(Object obj, String target) {
		if (KernelString.isEmpty(target)) {
			return obj;
		}

		String[] fields = target.split("\\.");
		for (String field : fields) {
			if (obj == null) {
				return null;
			}

			if (field.startsWith(":")) {
				Method method = KernelReflect.method(obj.getClass(), field.substring(1));
				if (method != null) {
					obj = KernelReflect.invoke(obj, method);
				}

			} else {
				obj = declaredGetter(obj, field);
			}
		}

		return obj;
	}

	/**
	 * @param obj
	 * @param toClass
	 * @return
	 */
	public static <T> T cast(Object obj, Class<T> toClass) {
		if (obj != null && toClass.isAssignableFrom(obj.getClass())) {
			return (T) obj;
		}

		return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	public static <T> T clone(T obj) {
		if (obj == null) {
			return null;
		}

		try {
			if (obj.getClass().isArray()) {
				return KernelArray.clone(obj);

			} else {
				T clone = (T) obj.getClass().newInstance();
				clone(obj, clone);
				return clone;
			}

		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		return null;
	}

	/**
	 * @param obj
	 * @param clone
	 */
	public static <T> void clone(final T obj, final T clone) {
		if (obj.getClass().isArray()) {
			KernelArray.copy(obj, clone);

		} else if (obj instanceof Collection) {
			KernelCollection.copy((Collection) obj, (Collection) clone);

		} else if (obj instanceof Map) {
			KernelMap.copy((Map<Object, Object>) obj, (Map<Object, Object>) clone);

		} else {
			KernelReflect.doWithDeclaredFields(obj.getClass(), new CallbackBreak<Field>() {

				@Override
				public void doWith(Field template) throws BreakException {
					template.setAccessible(true);
					try {
						template.set(clone, template.get(obj));

					} catch (IllegalArgumentException e) {
					} catch (IllegalAccessException e) {
					}
				}
			});
		}
	}

	/**
	 * @param obj
	 * @param copy
	 */
	public static void copy(final Object obj, final Object copy) {
		final Class cls = copy.getClass();
		if (obj.getClass().isArray()) {
			KernelArray.copy(obj, copy);

		} else if (obj instanceof Collection) {
			if (copy instanceof Collection) {
				KernelCollection.copy((Collection) obj, (Collection) copy);
			}

		} else if (obj instanceof Map) {
			if (copy instanceof Map) {
				KernelMap.copy((Map<Object, Object>) obj, (Map<Object, Object>) copy);
			}

		} else {
			KernelReflect.doWithDeclaredFields(obj.getClass(), new CallbackBreak<Field>() {

				@Override
				public void doWith(Field template) throws BreakException {
					Field field = KernelReflect.declaredField(cls, template.getName());
					if (field != null && field.getType().isAssignableFrom(template.getType())) {
						template.setAccessible(true);
						try {
							field.set(copy, template.get(obj));

						} catch (IllegalArgumentException e) {
						} catch (IllegalAccessException e) {
						}
					}
				}
			});
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public static byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
			objectOut.writeObject(obj);
			return byteOut.toByteArray();

		} catch (IOException e) {
		}

		return null;
	}

	/**
	 * @param buf
	 * @return
	 */
	public static Object unserialize(byte[] buf) {
		try {
			ByteArrayInputStream byteInput = new ByteArrayInputStream(buf);
			ObjectInputStream objectInput = new ObjectInputStream(byteInput);
			Object obj = objectInput.readObject();
			return obj;

		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}

		return null;
	}

	/**
	 * @param obj
	 * @return
	 */
	public static <T> T serializeClone(T obj) {
		byte[] buf = serialize(obj);
		if (buf == null) {
			return null;
		}

		return (T) unserialize(buf);
	}

	/**
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> getMap(final Object obj) {
		final Map<String, Object> map = new HashMap<String, Object>();
		KernelReflect.doWithDeclaredFields(obj.getClass(), new CallbackBreak<Field>() {

			@Override
			public void doWith(Field template) throws BreakException {
				Object value = publicGetter(obj, template);
				if (value != null) {
					map.put(template.getName(), value);
				}
			}
		});

		return map;
	}

	/**
	 * @param obj
	 * @param map
	 * @return
	 */
	public static void setMap(Object obj, Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			publicSetter(obj, entry.getKey(), entry.getValue());
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	public static int hashCode(Object obj) {
		return obj == null ? 1 : obj.hashCode();
	}

	/**
	 * @param obj
	 * @param equal
	 * @return
	 */
	public static boolean equals(Object obj, Object equal) {
		return obj == equal || (obj != null && obj.equals(equal));
	}

	/**
	 * @param obj
	 * @param compare
	 * @return
	 */
	public static int compare(Object obj, Object compare) {
		if (obj == null) {
			return compare == null ? 0 : -1;
		}

		if (compare == null) {
			return 1;
		}

		return 0;
	}

}
