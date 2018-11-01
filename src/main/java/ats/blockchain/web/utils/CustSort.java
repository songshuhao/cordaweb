package ats.blockchain.web.utils;

import java.lang.reflect.Field;
import java.util.Comparator;


public class CustSort<T> implements Comparator<T>{
	
	private String sortField;
	private boolean isAsc;
	
	public boolean isAsc() {
		return isAsc;
	}

	public void setAsc(boolean isAsc) {
		this.isAsc = isAsc;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}
	
	public  static <T>  int compare(T o1, T o2,String sortField) {
		ClassMethods clazzMethod = ClassMethodFactory.Instance.getClassMethods(o1.getClass());
		
		Field field = clazzMethod.getField(sortField);
		if(field==null) {
			throw new IllegalArgumentException(o1.getClass().getName()+" has no filed named:"+sortField);
		}
		int c = 0;
		try {
			Comparable o1Val = (Comparable) field.get(o1);
			Comparable o2Val = (Comparable) field.get(o2);
			
			c= o1Val.compareTo(o2Val);
		} catch (Exception e) {
		}
		return c;
	}
	
	public static <T>  int compare(T o1,T o2,String sortField,boolean isAsc) {
		int c = 0;
		c = compare(o1,o2,sortField);
		return isAsc?c:-c;
	}

	@Override
	public int compare(T o1, T o2) {
		return compare(o1,o2,sortField,isAsc);
	}

}
