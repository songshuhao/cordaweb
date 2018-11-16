package ats.blockchain.web.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClassMethods {
	private String className;
	private Map<String, Method> methodMap = new HashMap<String, Method>(128);
	private Map<String, Field> fieldMap = new HashMap<String, Field>(128);

	public ClassMethods(Class<?> clazz) {
		this.className = clazz.getName();
		initMethodMap(clazz);
		initFieldMap(clazz);
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	private void initMethodMap(Class<?> clazz) {
		Method[] methods = clazz.getDeclaredMethods();
		
		for (Method method : methods) {
			
			String name = method.getName().toUpperCase();
			//子类已存在的方法不再覆盖
			if(methodMap.containsKey(name))
				continue;
			
			methodMap.put(name, method);
		}
		Class<?> supeClazz = clazz.getSuperclass();
		if(supeClazz != null && supeClazz != Object.class) {
			initMethodMap(supeClazz);
		}
	}
	

	private void initFieldMap(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if(fieldMap.containsKey(field.getName())) {
				continue;
			}
			field.setAccessible(true);
			fieldMap.put(field.getName(), field);
		}
		
		Class<?> supeClazz = clazz.getSuperclass();
		if(supeClazz != null && supeClazz != Object.class) {
			initFieldMap(supeClazz);
		}
	}

	public Map<String, Field> getFieldMap() {
		return Collections.unmodifiableMap(fieldMap);
	}

	public Map<String, Method> getMethodMap() {
		return Collections.unmodifiableMap(methodMap);
	}

	public Method getMethod(String methodName) {
		return methodMap.get(methodName);
	}

	public Field getField(String fieldName) {
		return fieldMap.get(fieldName);
	}
}