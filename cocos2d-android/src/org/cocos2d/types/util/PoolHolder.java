package org.cocos2d.types.util;

import org.cocos2d.types.CGAffineTransform;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.ccQuad2;
import org.cocos2d.types.ccQuad3;
import org.cocos2d.utils.pool.ObjectPool;
import org.cocos2d.utils.pool.OneClassPool;

/**
 * This class stores pools for different types widely used in cocos2d.
 * List of such objects couldn't be extended while manually, but if you need your pool,
 * you are free to use classes OneClassPool and ObjectPool.
 * Thread local pools are used for concurrent usage. 
 * 
 * @author genius
 *
 */
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
	private OneClassPool<ccQuad2> ccQuad2Pool;
	private OneClassPool<ccQuad3> ccQuad3Pool;
	private OneClassPool<CGRect> rectPool;
	private OneClassPool<CGAffineTransform> affineTransformPool;
	
	private PoolHolder() {
		objectPool = new ObjectPool();
		
		pointPool   = new OneClassPool<CGPoint>() {
			@Override
			protected CGPoint allocate() {
				return new CGPoint();
			}
		};
		objectPool.registerPool(CGPoint.class, pointPool);

		ccQuad2Pool = new OneClassPool<ccQuad2>() {
			@Override
			protected ccQuad2 allocate() {
				return new ccQuad2();
			}
		};
		objectPool.registerPool(ccQuad2.class, ccQuad2Pool);
		
		ccQuad3Pool = new OneClassPool<ccQuad3>() {
			@Override
			protected ccQuad3 allocate() {
				return new ccQuad3();
			}
		};
		objectPool.registerPool(ccQuad3.class, ccQuad3Pool);
		
		rectPool    = new OneClassPool<CGRect>() {
			@Override
			protected CGRect allocate() {
				return new CGRect();
			}
		};
		objectPool.registerPool(CGRect.class, rectPool);
		
		affineTransformPool =  new OneClassPool<CGAffineTransform>() {
			@Override
			protected CGAffineTransform allocate() {
				return new CGAffineTransform();
			}
		};
		objectPool.registerPool(CGAffineTransform.class, affineTransformPool);
	}
	
	public ObjectPool getObjectPool() {
		return objectPool;
	}
	
	public OneClassPool<CGPoint> getCGPointPool() {
		return pointPool;
	}

	public OneClassPool<ccQuad2> getccQuad2Pool() {
		return ccQuad2Pool;
	}

	public OneClassPool<ccQuad3> getccQuad3Pool() {
		return ccQuad3Pool;
	}

	public OneClassPool<CGRect> getCGRectPool() {
		return rectPool;
	}
	
	public OneClassPool<CGAffineTransform> getCGAffineTransformPool() {
		return affineTransformPool;
	}
}
