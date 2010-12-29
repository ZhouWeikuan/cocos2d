package org.cocos2d.utils.pool;

import java.util.HashMap;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ObjectPool {

	private HashMap<Class<?>, OneClassPool<?>> lists;

	public ObjectPool() {
		lists = new HashMap<Class<?>, OneClassPool<?>>();
	}

	public <T> OneClassPool<T> registerType(Class<T> clazz) {
		OneClassPool<T> pool = (OneClassPool<T>)lists.get(clazz);
		if(pool == null) {
			pool = new OneClassPool<T>(clazz);
			lists.put(clazz, pool);
		}
		return pool;
	}
	

	public <T> OneClassPool<T> getPool(Class<T> clazz) {
		return (OneClassPool<T>)lists.get(clazz);
	}
	
	public <T> T get(Class<T> clazz) {
		OneClassPool<T> objs = (OneClassPool<T>)lists.get(clazz);
		return objs.get();
	}
	
	public <T> void free(T obj) {
		Class<?> clazz = obj.getClass();
		OneClassPool<T> objs = (OneClassPool<T>)lists.get(clazz);
		objs.free(obj);
	}

	public int size(Class<?> clazz) {
		OneClassPool objs = lists.get(clazz);
		return objs.size();
	}
	
	public void clear(Class<?> clazz) {
		OneClassPool objs = lists.get(clazz);
		objs.clear();
	}
	
	public void clearAll() {
		for(OneClassPool objs : lists.values()) {
			objs.clear();
		}
	}
}
