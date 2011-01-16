Some rules to keep in mind to reduce garbage collector:
- do not create objects outside of constructors, try at least;
- call "set" method instead of creating new every time;
- look at CG/cc<Typename>Utils classes they do not generate garbage.

Notes:
- package org.cocos2d.utils contains PlistParser for reading plists into HashMap.
- Pools now can help you to reduce garbage of small objects, like CGPoint. org.cocos2d.types.util.PoolHolder keeps some of pools. This class should be used for stack-like objects(number of get() must be higher or equal, but not LESS, then number of free() calls). There is ConcOneClassPool for use in multithreaded classes;
- org.cocos2d.utils.collections package contains classes mainly for internal usege). They are garbage-free and used in that parts of where changes performed frequently.
- TextBulder from Javolution included in utils, it can be used instead of standert StringBuilder. why? It is also garbage-free. Look at CCDirector's showFPS(), now it is not garbage generator.
- CCScheduler now can use interface UpdateCallback instead of scheduling methods for invocation. This is more java way solution and doesn't generate garbage for GC.
- android have at minimum 2 theads in activity, UI thread should react on user interaction only, the rest of the work should be performed in rendering thread.
- thread count. There is no multicore CPUs as I know while, we should keep number of theads at minimum.  Deffer long user events from UI thread, and handle them in Render thread. Somehow...
- Now key pressed is dispatching handling to rendering thread.
- Texture handling. When activity pause all OpenGL resources are destroyed. We need to recreate them. Textures are created from different sources. For this there is interface in CCTexture2D class (later it may go out of there, if we need to recreate another resources, now textures only) which define load() method. Class implementing this inteface must not have reference to CCTexture2D handling it. Good news that you usually do not have to know about this. Except that case if you initialize texture with your own Bitmap(then load() method implementation is your work, see ActionsTest.java(ActionDemo constructor)). When init sprite from bitmap (public static CCSprite sprite(Bitmap image, String key)) bitmap is copied and is stored in memory for reinit, try to avoid this CCTexture2D initialization. You are free to call initWith..., but reinit logic is on yours.
- we ported Sky Tower from iPhone, and there the scene has fixed size 320x480, therefore I've changed code in CCDirector so that screenSize_ could be requested with specific size. This seems to be expected considering to CCDirector logic, but makes 2d default projection. This is performed in such way in our application:

	public static final float SUPPOSED_WIN_WIDTH  = 320; 
	public static final float SUPPOSED_WIN_HEIGHT = 480;
	
	public void onCreate(Bundle savedInstanceState) {
	
		...
		CCDirector.sharedDirector().setScreenSize(SUPPOSED_WIN_WIDTH, SUPPOSED_WIN_HEIGHT);    
	    CCDirector.sharedDirector().getActivity().setContentView(mGLSurfaceView, createLayoutParams());
		...
		
	}
	
	private LayoutParams createLayoutParams() {
        final DisplayMetrics pDisplayMetrics = new DisplayMetrics();
		CCDirector.sharedDirector().getActivity().getWindowManager().getDefaultDisplay().getMetrics(pDisplayMetrics);
		
		
		final float mRatio = (float)SUPPOSED_WIN_WIDTH / SUPPOSED_WIN_HEIGHT;
		final float realRatio = (float)pDisplayMetrics.widthPixels / pDisplayMetrics.heightPixels;

		final int width;
		final int height;
		if(realRatio < mRatio) {
			width = pDisplayMetrics.widthPixels;
			height = Math.round(width / mRatio);
		} else {
			height = pDisplayMetrics.heightPixels;
			width = Math.round(height * mRatio);
		}

		final LayoutParams layoutParams = new LayoutParams(width, height);

		layoutParams.gravity = Gravity.CENTER;
		return layoutParams;
	}
	
- CCDirector pause/resume doesn't mean that rendering thread must be stopped, but we need to stop it when activity pauses, that why there should be separate methods onPause/onResume for activity and pause/resume.
