package org.cocos2d.utils.pool;

import org.cocos2d.utils.collections.ConcNodeCachingStack;

public abstract class ConcOneClassPool<T> {
	private ConcNodeCachingStack<T> objs;

	public ConcOneClassPool() {
		objs = new ConcNodeCachingStack<T>();
	}

	protected abstract T allocate();
	
	public T get() {
		T ret = objs.pop();
		if(null == ret) {
			ret = allocate();
		}

		return ret;
	}
	
	public void free(T obj) {
		objs.push(obj);
	}
}
