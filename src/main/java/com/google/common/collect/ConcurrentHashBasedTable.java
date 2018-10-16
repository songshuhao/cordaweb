package com.google.common.collect;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Supplier;
import com.google.common.collect.Table;

/**
 * 实现线程安全的Table
 * 
 * @author:shi hongyu
 */
@GwtCompatible(serializable = true)
public class ConcurrentHashBasedTable<R, C, V> extends StandardTable<R, C, V> {

	private static final long serialVersionUID = 7760924657243213517L;

	/**
	 * 
	 * 方法功能：
	 * 
	 * @return
	 *
	 */
	public static <R, C, V> ConcurrentHashBasedTable<R, C, V> create() {
		return new ConcurrentHashBasedTable<R, C, V>(new ConcurrentHashMap<R, Map<C, V>>(), new Factory<C, V>(0));
	}

	/**
	 * 
	 * 方法功能：
	 * 
	 * @param expectedRows
	 * @param expectedCellsPerRow
	 * @return
	 *
	 */
	public static <R, C, V> ConcurrentHashBasedTable<R, C, V> create(int expectedRows, int expectedCellsPerRow) {
		CollectPreconditions.checkNonnegative(expectedCellsPerRow, "expectedCellsPerRow");
		Map<R, Map<C, V>> backingMap = new ConcurrentHashMap<R, Map<C, V>>(expectedRows);
		return new ConcurrentHashBasedTable<R, C, V>(backingMap, new Factory<C, V>(expectedCellsPerRow));
	}

	/**
	 * 
	 * 方法功能：
	 * 
	 * @param table
	 * @return
	 *
	 */
	public static <R, C, V> ConcurrentHashBasedTable<R, C, V> create(
			Table<? extends R, ? extends C, ? extends V> table) {
		ConcurrentHashBasedTable<R, C, V> result = create();
		result.putAll(table);
		return result;
	}

	/**
	 * 
	 * 
	 * 构造方法:创建一个新实例对象ConcurrentHashBasedTable.
	 *
	 * @param backingMap
	 * @param factory
	 */
	ConcurrentHashBasedTable(Map<R, Map<C, V>> backingMap, Factory<C, V> factory) {
		super(backingMap, factory);
	}

	/**
	 * 
	 * 方法功能:
	 *
	 * @see com.google.common.collect.StandardTable#contains(java.lang.Object,
	 *      java.lang.Object)
	 */
	public boolean contains(Object rowKey, Object columnKey) {
		return super.contains(rowKey, columnKey);
	}

	/**
	 * 
	 * 方法功能:
	 *
	 * @see com.google.common.collect.StandardTable#containsColumn(java.lang.Object)
	 */
	public boolean containsColumn(Object columnKey) {
		return super.containsColumn(columnKey);
	}

	/**
	 * 
	 * 方法功能:
	 *
	 * @see com.google.common.collect.StandardTable#containsRow(java.lang.Object)
	 */
	public boolean containsRow(Object rowKey) {
		return super.containsRow(rowKey);
	}

	/**
	 * 
	 * 方法功能:
	 *
	 * @see com.google.common.collect.StandardTable#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return super.containsValue(value);
	}

	/**
	 * 
	 * 方法功能:
	 *
	 * @see com.google.common.collect.StandardTable#get(java.lang.Object,
	 *      java.lang.Object)
	 */
	public V get(Object rowKey, Object columnKey) {
		return super.get(rowKey, columnKey);
	}

	/**
	 * 
	 * 方法功能:
	 *
	 * @see com.google.common.collect.AbstractTable#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/**
	 * 
	 * 方法功能:
	 *
	 * @see com.google.common.collect.StandardTable#remove(java.lang.Object,
	 *      java.lang.Object)
	 */
	public V remove(Object rowKey, Object columnKey) {
		return super.remove(rowKey, columnKey);
	}

	/**
	 * 
	 * 
	 * 构造方法:创建一个新实例对象ConcurrentHashBasedTable.
	 *
	 * @param backingMap
	 * @param factory
	 */
	ConcurrentHashBasedTable(Map<R, Map<C, V>> backingMap, Supplier<? extends Map<C, V>> factory) {
		super(backingMap, factory);
	}

	/**
	 * 
	 * 类功能：
	 * 
	 * @author:shy
	 */
	private static class Factory<C, V> implements Supplier<Map<C, V>>, Serializable {
		final int expectedSize;
		private static final long serialVersionUID = 0L;

		Factory(int expectedSize) {
			this.expectedSize = expectedSize;
		}

		public Map<C, V> get() {
			return new ConcurrentHashMap<C, V>(expectedSize);
		}
	}

}
