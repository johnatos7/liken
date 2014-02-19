package application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.liken.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class GlobalState extends Application {
	public String TOPIC;
	//ArrayList<ImageView> listImageView=new ArrayList<ImageView>();
	public  DisplayImageOptions options,options_profile_img;
	 public ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	 protected ImageLoader imageLoader = ImageLoader.getInstance();
	 HashMap<String,String> hmpicName=new HashMap<String,String>();
	public int width;
	public	int height;
	public DisplayMetrics displayMetrics;
     public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

 		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

 		@Override
 		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
 			if (loadedImage != null) {
 				ImageView imageView = (ImageView) view;
 				boolean firstDisplay = !displayedImages.contains(imageUri);
 				if (firstDisplay) {
 					FadeInBitmapDisplayer.animate(imageView, 500);
 					displayedImages.add(imageUri);
 				}
 			}
 		}
 	}
     
 /*    public void addUIL(ImageView v){
    	 listImageView.add(v);
     }
     
     public void clearAllImageViewDisplay(){
    	 for(int i=0;i<listImageView.size();i++){
    		 imageLoader.cancelDisplayTask(listImageView.get(i));
    	 }
     }*/
     
     public void addUriforPicture(String picName,String PresignedURL){
    	 hmpicName.put(picName, PresignedURL);
     }
     public String getPresignedURLforPicture(String picName){
    	String presignedURL= hmpicName.get(picName);
    	if(presignedURL==null){
    		return null;
    	}
    	return presignedURL;
     }
    
     public void setTopic(String TOPIC){
    	 this.TOPIC=TOPIC;
     }
     public String getTopic(){
    	 Log.i("TOPIC", TOPIC);
    	 return TOPIC;
     }
     
	@SuppressLint("NewApi")
	public static void initImageLoader(Context context) {
		// get the width and height of the device screen
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int width=0;
		int height=0;
		if (android.os.Build.VERSION.SDK_INT >= 13){
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			 height = size.y;
		}else{
			 width = display.getWidth();  // deprecated
			 height = display.getHeight();  // deprecated
		}
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(1)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCacheExtraOptions(width, height)
				.discCacheExtraOptions(width, height, CompressFormat.JPEG, 70,null)
				.memoryCache(new WeakMemoryCache())
			//	.threadPriority(Thread.MIN_PRIORITY +3)
				.threadPriority(Thread.NORM_PRIORITY - 1) // default
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
			//	.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
    
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
     public void onCreate() {
    	 if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
 		//	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
 		//	StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
 		}
         super.onCreate();
         createUILOption();
         initImageLoader(getApplicationContext());
         setDisplayMetrics();
         new AsyncTask<Void, Void, Void>() {
             @Override
             protected Void doInBackground(Void... params) {
               return null;
             }
           }.execute(); 
     }


	private void setDisplayMetrics(){
    	displayMetrics = getApplicationContext().getResources().getDisplayMetrics();		
	}
	private void createUILOption(){
		//for webview
		   options = new DisplayImageOptions.Builder()
	 		.showStubImage(R.drawable.link_icon_grey)
	 		.showImageForEmptyUri(R.drawable.broken_link_grey)
	 		.showImageOnFail(R.drawable.broken_link_grey)
	 		.cacheInMemory(true)
	 		.cacheOnDisc(true)
	 		
	 		.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
	 		.bitmapConfig(Bitmap.Config.RGB_565)
	 		//.displayer(new RoundedBitmapDisplayer(20))
	 		.build();
	         
		   //for profile imageview
	         options_profile_img = new DisplayImageOptions.Builder()
	  		.showStubImage(R.drawable.profile)
	  		.showImageForEmptyUri(R.drawable.profile)
	  		.showImageOnFail(R.drawable.profile)
	 		.cacheInMemory(true)
	  		.cacheOnDisc(true)
	  		.bitmapConfig(Bitmap.Config.RGB_565)
	  		.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) 
	  		//.displayer(new RoundedBitmapDisplayer(20))
	  		.build();
		
		
	}
	
	

}
