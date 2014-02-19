package com.liken.contentinflater;

import java.util.HashMap;

import WebContent.JsoupTask;
import WebContent.SimpleYouTubeHelper;
import WebContent.JsoupTask.JsoupTaskListener;
import WebContent.SimpleYouTubeHelper.YouTubeHelperListener;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import application.GlobalState;

import com.liken.R;
import com.liken.PhotoActivity;
import com.liken.constant.Constants;
import com.liken.webservice.S3MediaUtil;
import com.liken.webservice.S3MediaUtil.S3MediaTaskListener;
import com.liken.webservice.S3MediaUtil.S3TaskResult;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;

public class UserAnswersPageInflater {
	TextView tv_Question;
	Button tv_AnswerCount;

	HashMap<String, String> tempData;
	LayoutInflater inflater;
	LinearLayout ll_MediaView;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	Activity activity;
	public UserAnswersPageInflater(LinearLayout ll_MediaView, TextView tv_Question, Button tv_AnswerCount,
			HashMap<String, String> tempData, LayoutInflater inflater, Activity activity) {
		this.ll_MediaView=ll_MediaView;
		this.tv_Question=tv_Question;
		this.tv_AnswerCount=tv_AnswerCount;
		
		this.tempData=tempData;
		this.inflater=inflater;
		this.activity=activity;
	
	}

	

	/*		a.Answer,
	a.ID,
	a.Media_Type AS a_Media_Type,
	a.Media_Link AS a_Media_Link,
	lm.Asker_like,
	lm.Answer_like,
	q.Questions,
	q.Media_Type AS q_Media_Type,
	q.Media_Link AS q_Media_Link,
	q.Category,
	u.Username */
	public void inflate(){
		InflateTextContent();
		if(!tempData.get("a_Media_Type").equals("0")){
			ll_MediaView.removeAllViews();
			ll_MediaView.setVisibility(View.GONE);
		}
		if(tempData.get("a_Media_Type").equals("1")){
			generateMediaView();
		}else if(tempData.get("a_Media_Type").equals("2")){
			processURL(2);
		}else if(tempData.get("a_Media_Type").equals("3")){
			processURL(3);
		}	
	}
	private void InflateTextContent() {
		tv_Question.setText(tempData.get("Answer"));
		if(tempData.get("Category")==null){
		//tv_Question.setTextColor(
		}else{
		tv_Question.setTextColor(new Constants().getTopicColor(Integer.parseInt(tempData.get("Category"))));
		}
	
}

	public void processURL(int Type)  {	
			if(Type==2){
			JsoupTask jsoupTask= new JsoupTask(tempData.get("a_Media_Link"));
			jsoupTask.setJsoupTaskListener(new JsoupTaskListener(){
				@Override
				public void onCompleted(HashMap<String, String> result) {
					generateMediaViewforLink(result);
				}});
			jsoupTask.execute();
			}else{
			SimpleYouTubeHelper youtubehelper=new SimpleYouTubeHelper(tempData.get("a_Media_Link"));
			youtubehelper.setYouTubeHelperListener(new YouTubeHelperListener(){
				@Override
				public void onCompleted(HashMap<String, String> result) {
					generateMediaViewforLink(result);				
				}});
			youtubehelper.execute();
			}
		
		
	}
	
	private void generateMediaView() {
		ll_MediaView.setVisibility(View.VISIBLE);
		ll_MediaView.removeAllViews();
		//ll_MediaView.removeAllViewsInLayout();
		View v = inflater.inflate(R.layout.view_weblink, null);
		final ImageView image = (ImageView) v.findViewById(R.id.imageView1);
		ImageSizeUtils.defineTargetSizeForView(image, image.getWidth(),800 );
		TextView tv_title = (TextView) v.findViewById(R.id.tv_title);
		TextView tv_content = (TextView) v.findViewById(R.id.tv_content);
		tv_title.setVisibility(View.GONE);
		tv_content.setVisibility(View.GONE);
		S3MediaUtil s3MediaUtil=new S3MediaUtil(activity);
		s3MediaUtil.setS3MediaTaskListener(new S3MediaTaskListener(){

			@Override
			public void onCompleted(S3TaskResult data) {
				String Uri=data.getUri().toString();
				setClickListener(Uri);
				imageLoader
				.displayImage(
						Uri,
						image,
						((GlobalState) activity.getApplicationContext()).options,
						((GlobalState)activity.getApplicationContext()).animateFirstListener);
		//		((GlobalState) activity.getApplicationContext()).addUIL(image);
			}});
		s3MediaUtil.S3GetImageUri(tempData.get("a_Media_Link"));
		ll_MediaView.addView(v);
		}

		protected void setClickListener(final String uri) {
			ll_MediaView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent i=new Intent(activity,PhotoActivity.class);
					i.putExtra("data", uri);
					activity.startActivity(i);
					
				}});
			
		}

	private void generateMediaViewforLink(HashMap<String, String> data) {
		ll_MediaView.setVisibility(View.VISIBLE);
		ll_MediaView.removeAllViews();
		//	ll_MediaView.removeAllViewsInLayout();
		View v = inflater.inflate(R.layout.view_weblink, null);
		ImageView image = (ImageView) v.findViewById(R.id.imageView1);
		ImageSizeUtils.defineTargetSizeForView(image, image.getWidth(),800 );

		TextView tv_title = (TextView) v.findViewById(R.id.tv_title);
		TextView tv_content = (TextView) v.findViewById(R.id.tv_content);
		tv_title.setVisibility(View.VISIBLE);
		tv_content.setVisibility(View.VISIBLE);
		tv_title.setText(data.get("Title"));
		tv_content.setText(data.get("Content"));
		imageLoader
				.displayImage(
						data.get("img_url"),
						image,
						((GlobalState) activity.getApplicationContext()).options,
						((GlobalState)activity.getApplicationContext()).animateFirstListener);
	//	((GlobalState) activity.getApplicationContext()).addUIL(image);

		ll_MediaView.addView(v);
		ll_MediaView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				viewInBrowser();
				
			}
			
		});
	}
	
	private void viewInBrowser() {
		// Display in Browser.
		Uri uri =  Uri.parse(tempData.get("a_Media_Link") );
		activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
		System.out.println("view in browser");
	}

}
