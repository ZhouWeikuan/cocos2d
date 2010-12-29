package org.cocos2d.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ConcurrentArrayHashMap<K,V> {
	private ArrayList<V> array;
	private ArrayList<Integer> freeIndexes;
	private HashMap<K,Integer> map;
	
	public ConcurrentArrayHashMap() {
		freeIndexes = new ArrayList<Integer>();
		array = new ArrayList<V>();
		map = new HashMap<K,Integer>();
	}
	
	public synchronized void put(K key, V value) {
		if(freeIndexes.isEmpty()) {
			int ind = array.size();
			array.add(value);
			map.put(key, ind);
		} else {
			Integer ind = freeIndexes.remove( freeIndexes.size() -1 );
			array.set(ind.intValue(), value);
			map.put(key, ind);
		}
	}
	
	public synchronized V get(K key) {
		Integer ind = map.get(key);
		if ( ind == null )
			return null;
		return array.get(ind.intValue());
	}
	
	/*
	 * This method is not synchronized.
	 * array may contain null values.
	 */
	public ArrayList<V> getValuesInArrayList() {
		return array;
	}
	
	public synchronized void remove(K key) {
		Integer ind = map.get(key);
		if ( ind == null )
			return;
	
		map.remove(key);
		
		if(ind != array.size()-1) {
			array.set(ind.intValue(), null);
			freeIndexes.add(ind);
		} else {
			array.remove(ind.intValue());
		}
	}
	
}
