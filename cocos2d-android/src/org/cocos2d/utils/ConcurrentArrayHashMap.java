package org.cocos2d.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ConcurrentArrayHashMap<K,V> {
	private ArrayList<V> array;
	private HashMap<K,Integer> map;
	
	public ConcurrentArrayHashMap() {
		array = new ArrayList<V>();
		map = new HashMap<K,Integer>();
	}
	
	public synchronized void put(K key, V value) {
		int ind = array.size();
		array.add(value);
		map.put(key, ind);
	}
	
	public synchronized V get(K key) {
		Integer ind = map.get(key);
		if ( ind == null )
			return null;
		return array.get(ind);
	}
	
	/*
	 * This method is not synchronized.
	 */
	public ArrayList<V> getValuesInArrayList() {
		return array;
	}
	
	public synchronized void remove(K key) {
		Integer ind = map.get(key);
		if ( ind == null )
			return;
		
		array.remove(ind);
		map.remove(key);			
	}
	
}
