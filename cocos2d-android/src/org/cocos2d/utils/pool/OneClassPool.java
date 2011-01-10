package org.cocos2d.utils.pool;

import java.util.ArrayList;

public abstract class OneClassPool<T> {
	private ArrayList<T> objs;

	public OneClassPool() {
		objs = new ArrayList<T>();
	}

	protected abstract T allocate();
	
	public T get() {
		int s = objs.size();
		if(0 != s) {
			return objs.remove(s-1);
		} else {
			return allocate();
		}
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
