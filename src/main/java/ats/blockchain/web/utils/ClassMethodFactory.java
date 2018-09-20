package ats.blockchain.web.utils;

import java.util.HashMap;
import java.util.Map;

public enum ClassMethodFactory {
	Instance;
	private Map<String, ClassMethods> clazzMap = new HashMap<String, ClassMethods>();

	public ClassMethods getClassMethods(Class<?> clazz) {
		ClassMethods clazzM = null;
		String clazzName = clazz.getName();
		if(clazzMap.containsKey(clazzName)) {
			clazzM = clazzMap.get(clazzName);
		}else {
			clazzM = new ClassMethods(clazz);
			clazzMap.put(clazzName, clazzM);
		}
		return clazzM;
	}
}
