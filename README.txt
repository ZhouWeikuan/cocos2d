This branch was created to show some changes made to cocod2d-android to reduce GC calls in our project, plus some more things. This changes should come to master branch in some form later.

Some rules to keep in mind:
- do not create objects outside of constructors, try at least;
- call "set" method instead of creating new every time;

Notes:
- android have at minimum 2 theads in activity, UI thread should react on user interaction only, the rest of the work should be performed in rendering thread, i will implement deferred texture loading and key pressed dispatching to rendering thread soon. While white texture bug is solved in rude manner, but is gentle for memory(we can't stay Bitmap without recycle()).
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