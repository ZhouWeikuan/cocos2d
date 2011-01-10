package org.cocos2d.utils.pool;

import java.util.HashMap;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ObjectPool {

	private HashMap<Class<?>, OneClassPool<?>> lists;

	public ObjectPool() {
		lists = new HashMap<Class<?>, OneClassPool<?>>();
	}

	public <T> OneClassPool<T> registerPool(Class<T> clazz, OneClassPool<T> newPool) {
		OneClassPool<T> pool = (OneClassPool<T>)lists.get(clazz);
		if(pool == null) {
//			pool = new OneClassPool<T>(clazz);
			lists.put(clazz, newPool);
		}
		return pool;
	}
	

	public <T> OneClassPool<T> getPool(Class<T> clazz) {
		return (OneClassPool<T>)lists.get(clazz);
	}

	public void clearAll() {
		for(OneClassPool objs : lists.values()) {
			objs.clear();
		}
	}
}
