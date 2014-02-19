package com.liken;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchDoubleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.OnDrawableChangeListener;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import application.GlobalState;

import com.google.analytics.tracking.android.EasyTracker;
import com.liken.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotoActivity extends Activity {
	private static final String LOG_TAG = "image-test";
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	static ImageViewTouch mImage,mImage2;
	String Uri;
	static int displayTypeCount = 0; 

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.activity_photo );
		Uri=getIntent().getStringExtra("data");
		System.out.println(Uri);
		mImage = (ImageViewTouch) findViewById( R.id.image );
		imageLoader
		.displayImage(
				Uri,
				mImage,
				((GlobalState) this.getApplicationContext()).options,
				((GlobalState) this.getApplicationContext()).animateFirstListener);
		
		// set the default image display type
				mImage.setDisplayType( DisplayType.FIT_IF_BIGGER );
	}
	
	@Override
	  public void onStart() {
	    super.onStart();
	  
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }


/*	@Override
	public void onContentChanged() {
		super.onContentChanged();
		
	
		//mImage.setScaleType(ScaleType.MATRIX);
		
		// set the default image display type
		mImage.setDisplayType( DisplayType.FIT_IF_BIGGER );
		

		
		
		mImage.setSingleTapListener( new OnImageViewTouchSingleTapListener() {
			
			@Override
			public void onSingleTapConfirmed() {
				Log.d( LOG_TAG, "onSingleTapConfirmed" );
			}
		} );
		
		mImage.setDoubleTapListener( new OnImageViewTouchDoubleTapListener() {
			
			@Override
			public void onDoubleTap() {
				Log.d( LOG_TAG, "onDoubleTap" );
			}
		} );
		
		mImage.setOnDrawableChangedListener( new OnDrawableChangeListener() {
			
			@Override
			public void onDrawableChanged( Drawable drawable ) {
				Log.i( LOG_TAG, "onBitmapChanged: " + drawable );
			}
		} );
	}*/
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		imageLoader.cancelDisplayTask(mImage);
		 EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}


	@Override
	public void onConfigurationChanged( Configuration newConfig ) {
		super.onConfigurationChanged( newConfig );
	}
}
