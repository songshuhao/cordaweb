package ats.blockchain.web.cache;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.google.common.collect.ConcurrentHashBasedTable;
import com.google.common.collect.Lists;

/**
 * 
 * @author shi hongyu
 *
 * @param <R>
 * @param <C>
 * @param <V>
 */
public class TableCache<R, C, V> {

	private ConcurrentHashBasedTable<R, C, V> cache = ConcurrentHashBasedTable.create(2, 32);
	
	
	public V getValue(@Nonnull R row, @Nonnull C col) {
		return cache.get(row, col);
	}

	public List<V> getValuesByRow(@Nonnull R row) {
		Map<C, V> map = cache.row(row);
		List<V> list = Lists.newArrayListWithCapacity(map.size());
		map.forEach((k, v) -> list.add(v));
		return list;
	}
	
	public boolean containRow(@Nonnull R row) {
		return cache.containsRow(row);
	}
	
	public boolean containColumn(@Nonnull C col) {
		return cache.containsColumn(col);
	}
	public List<V> getValuesByColumn(@Nonnull C col) {
		
		Map<R, V> map = cache.column(col);
		List<V> list = Lists.newArrayListWithCapacity(map.size());
		map.forEach((k, v) -> list.add(v));
		return list;
	}
	
	public V put(@Nonnull R row,@Nonnull C col,@Nonnull V value) {
		return cache.put(row, col, value);
	}
	
	public V remove(@Nonnull R row,@Nonnull C col) {
		return cache.remove(row, col);
	}
	
}
