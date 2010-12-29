package org.cocos2d.utils.pool;

import java.util.ArrayList;

public class OneClassPool<T> {
	private ArrayList<T> objs;
	private Class<T> clazz;

	public OneClassPool(Class<T> clazz) {
		this.clazz = clazz;
		objs = new ArrayList<T>();
	}

	public T get() {
		int s = objs.size();
		if(0 != s) {
			return objs.remove(s-1);
		}
		try {
			return clazz.newInstance();
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public void free(T obj) {
		objs.add(obj);
	}

	public int size() {
		return objs.size();
	}
	
	public void clear() {
		objs.clear();
	}
}
