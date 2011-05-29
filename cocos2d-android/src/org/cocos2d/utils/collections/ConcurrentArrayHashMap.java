package org.cocos2d.utils.collections;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.utils.pool.OneClassPool;

/**
 * This class is temporary replacement of ConcurrentHashMap<K,V> for frequently 
 * iterating through values. See firstValue, nextValue methods.
 * Do not use this outside, this is not optimized.
 * Put into HashMap still causes new Entry, but iteration is done without call to values().
 * 
 * Also contains only put, get, remove methods.
 * @author genius
 */
public class ConcurrentArrayHashMap<K,V> {
	private ArrayList<Entry> array;
	private HashMap<K,Integer> map;
	
	OneClassPool<Entry> pool = new OneClassPool<Entry>() {
		@Override
		protected Entry allocate() {
			return new Entry();
		}
	};
	
	public final class Entry {
		K key;
		V value;
		Entry next;
		
		public V getValue() {
			return value;
		}
	}
	
	public ConcurrentArrayHashMap() {
		array = new ArrayList<Entry>();
		map = new HashMap<K,Integer>();
	}
	
	public synchronized void put(K key, V value) {
		Integer pos = map.get(key); 
		if(pos == null) {
		
			int ind = array.size();
			
			Entry entry = pool.get();
			entry.key   = key;
			entry.value = value;
			
			array.add(entry);
			
			if(ind > 0) {
				array.get(ind - 1).next = entry;
			}
			
			map.put(key, ind);
		} else {
			array.get(pos.intValue()).value = value;
		}
	}
	
	public synchronized V get(K key) {
		Integer ind = map.get(key);
		if ( ind == null )
			return null;
		return array.get(ind.intValue()).value;
	}
	
	public synchronized V remove(K key) {
		Integer ind = map.get(key);
		if ( ind == null )
			return null;
	
		map.remove(key);
		
		int curInd = ind.intValue();
		int lastInd = array.size()-1;
		
		V ret = array.get(curInd).value;
		
		if(curInd != lastInd) {
			// swap values
			K lastKey = array.get(lastInd).key;
			array.get(curInd).value = array.get(lastInd).value;
			array.get(curInd).key   = lastKey;
			map.put(lastKey, ind);
		}
		// remove last now
		if(lastInd != 0)
			array.get(lastInd - 1).next = null;
		Entry removedEntry = array.remove(lastInd);
		// should check for primitive type?
		removedEntry.key = null;
		removedEntry.value = null;
		pool.free(removedEntry);
		return ret;
	}

	/**
	 * First value Entry is returned.
	 * 
	 * Iteration is organized in this manner: 
	 * Iteration is organized in this manner: 
	 *		for(ConcurrentArrayHashMap<K, V>.Entry e = targets.firstValue();
	 *				e != null; e = targets.nextValue(e)) {
     *   		V element = e.getValue();
	 */
	public synchronized Entry firstValue() {
		if (array.isEmpty())
			return null;
		else {
			return array.get(0);
		}
	}
	
	/**
	 * Next Entry with not null value is returned.
	 * 
	 * Iteration is organized in this manner: 
	 *		for(ConcurrentArrayHashMap<K, V>.Entry e = targets.firstValue();
	 *				e != null; e = targets.nextValue(e)) {
     *   		V element = e.getValue();
	 */
	public synchronized Entry nextValue(Entry prev) {
		return prev.next;
	}

}
