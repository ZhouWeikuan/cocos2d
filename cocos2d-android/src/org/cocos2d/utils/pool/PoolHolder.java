package org.cocos2d.utils.pool;

import org.cocos2d.types.CGPoint;

public final class PoolHolder {
	
    private static ThreadLocal<PoolHolder> instance = new ThreadLocal<PoolHolder>() {
    	@Override
    	protected PoolHolder initialValue() {
    		return new PoolHolder();
    	}
    };
	
	public static PoolHolder getInstance() {
		return instance.get();
	}
	
	private ObjectPool objectPool;
	private OneClassPool<CGPoint> pointPool;
	
	private PoolHolder() {
		objectPool = new ObjectPool();
		
		pointPool = objectPool.registerType(CGPoint.class);
	}
	
	public ObjectPool getObjectPool() {
		return objectPool;
	}
	
	public OneClassPool<CGPoint> getCGPointPool() {
		return pointPool;
	}
}
