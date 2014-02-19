package com.liken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import urlpattern.URLPattern;
import WebContent.JsoupTask;
import WebContent.JsoupTask.JsoupTaskListener;
import WebContent.SimpleYouTubeHelper;
import WebContent.SimpleYouTubeHelper.YouTubeHelperListener;
import adapters.SpinnerAdapter;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import application.GlobalState;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.liken.R;
import com.liken.data.SQLiteHelper;
import com.liken.dialogs.ProcessDialog;
import com.liken.photo_util.PhotoProcessor;
import com.liken.webservice.JSONParser;
import com.liken.webservice.S3MediaUtil;
import com.liken.webservice.WebTask;
import com.liken.webservice.S3MediaUtil.S3MediaTaskListener;
import com.liken.webservice.S3MediaUtil.S3TaskResult;
import com.liken.webservice.WebTask.WebTaskListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AskActivity extends SherlockActivity implements OnClickListener {
	Bitmap mBitmap;
	LinearLayout linkView, llwidget;
	LayoutInflater inflater;
	SQLiteHelper sqlitehelper = new SQLiteHelper(this);
	ArrayList<String> topicsList;
	SpinnerAdapter spadapter;
	HashMap<String, String> webParams = new HashMap<String, String>();
	EditText et_link,et_Question;
	String URL = "";
	Spinner spinner_topic;
	int SelectedTopicPosition = 0;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	ProgressBar progressBar;
	URLPattern urlPattern=new URLPattern();
	Uri chosenImageUri;
	int inputLength;
	ProcessDialog mWebProcessDialog;
	//webparam -Questions, Answer, User_id,media_type,Media_link, Category
	//media type 0- no media,media
	//media type 1- photo media
	//media type 2- link media
	//media type 3- youtube media
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		topicsList = sqlitehelper.getTopics();
		initwebParam();
		spadapter = new SpinnerAdapter(this, topicsList);
		setContentView(R.layout.activity_ask);
		et_Question=(EditText)findViewById(R.id.editText1);
		progressBar=(ProgressBar)findViewById(R.id.progressBar);
		linkView = (LinearLayout) findViewById(R.id.ll_linkView);
		linkView.setOnClickListener(this);
		llwidget = (LinearLayout) findViewById(R.id.ll_widgets);
		ImageView ib_photo = (ImageView) findViewById(R.id.ib_photo);
		ImageView ib_link = (ImageView) findViewById(R.id.ib_link);
		ImageView ib_topic = (ImageView) findViewById(R.id.ib_topic);
		ib_photo.setOnClickListener(this);
		ib_link.setOnClickListener(this);
		ib_topic.setOnClickListener(this);
		TextWatcher textWatcher = new TextWatcher() {
		    @SuppressLint("NewApi")
			public void onTextChanged(CharSequence s, int start, int before, int count) {
		      inputLength = s.length();
		     // invalidateOptionsMenu();  
		      supportInvalidateOptionsMenu();
		    }

		    public void afterTextChanged(Editable s) {}
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		};

		et_Question.addTextChangedListener(textWatcher);
		
		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		 Intent intent = getIntent();
		    String action = intent.getAction();
		    String type = intent.getType();

		    if (Intent.ACTION_SEND.equals(action) && type != null) {
		        if ("text/plain".equals(type)) {
		        handleSendText(intent);
		        } else if (type.startsWith("image/")) {
		        	handleSendImage(intent);
		        	    }
		        
		    }
	
	}
	
	@Override
	  public void onStart() {
	    super.onStart();
	  
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	   
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
	
	void handleSendImage(Intent intent){
		 chosenImageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
 	    if (chosenImageUri != null) {
 	    	generateMediaView();
 	    	/*try {
 				mBitmap = Media.getBitmap(this.getContentResolver(),chosenImageUri);
 				generateMediaView();
 			} catch (FileNotFoundException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			} catch (IOException e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}*/
 	    }
	}
	public boolean onPrepareOptionsMenu(Menu menu) {

	    MenuItem item=menu.getItem(0);
	    item.setTitle((100-inputLength)+"");
	    return true;
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			chosenImageUri = data.getData();
			generateMediaView();
	/*		try {
				mBitmap = Media.getBitmap(this.getContentResolver(),
						chosenImageUri);
				generateMediaView();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}
	void handleSendText(Intent intent){
		
		String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
	      URL=sharedText;
	       processWeb();			
	}

	private void initwebParam() {
		webParams.put("Questions",
				"");
		webParams.put("Answer",
				"");
		webParams.put("User_id",
				 "");
		webParams.put("media_type",
				 "0");
		webParams.put("media_link",
				 "");
		if(topicsList.size()>0){
		webParams.put("Category",
				 topicsList.get(0));
		}else{
			
			webParams.put("Category",
					 "0");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("100").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("Done").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("Done")) {
		if(et_Question.getText().toString().trim().length()>0){
				webParams.put("Answer","");
				webParams.put("Questions",et_Question.getText().toString());
				processUploadPost();
			
			}else{
				Toast.makeText(this,"You have not ask a question.", 1).show();
			}
		}
		
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}
	private void processUploadPost() {
		/*if(et_Opinion.getText().toString().trim().length()<=0){
		Toast.makeText(this, "Please enter your opinion", 1).show();
		return;
		}*/
	 mWebProcessDialog=new ProcessDialog(this,"Uploading...");
		mWebProcessDialog.start();
	
		if(webParams.get("media_type").equals("1")){
		
		S3MediaUtil s3MediaUtil=new S3MediaUtil(this);
		s3MediaUtil.setS3MediaTaskListener(new S3MediaTaskListener() {       
				@Override
				public void onCompleted(S3TaskResult data) {
					processS3Result(data);			
				}       
	    });
		s3MediaUtil.S3PutObject(chosenImageUri);
		}else{
			processWebforUpload();
		}
	}
	private void PostUploadSucess(){
		Intent i = new Intent(this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		
		
	}
	public void processS3Result(Object result) {
		//put picture name into webparams
		
		webParams.put("media_link",((S3TaskResult) result).getPictureName()); 
		processWebforUpload();
	}
	
	private void processWebforUpload(){
		List<NameValuePair> data=new ArrayList<NameValuePair>();
	//	data.add(new BasicNameValuePair("Question", webParams.get("Questions")));
		data.add(new BasicNameValuePair("Question", webParams.get("Questions")));
		data.add(new BasicNameValuePair("Answer", webParams.get("Answer")));
		data.add(new BasicNameValuePair("UserID", sqlitehelper.getUserID()));
		data.add(new BasicNameValuePair("MediaType", webParams.get("media_type")));
		data.add(new BasicNameValuePair("MediaLink", webParams.get("media_link")));
		data.add(new BasicNameValuePair("Category", webParams.get("Category")));		
		WebTask webTask=new WebTask(data,new JSONParser().NEW_QUESTION_URL);
		webTask.setWebTaskListener(new WebTaskListener()
		{
			@Override
			public void onCompleted(String data) {
			processWebResult(data);		
			}		   
		});
		webTask.execute();
	}
	public void processWebResult(String result) {
		mWebProcessDialog.end();
		try {
			JSONObject job = new JSONObject(result);
			int success = job.getInt("success");
			if (success == 1) {
				PostUploadSucess();
			} else {
				Toast.makeText(this,
						"Oops, something is wrong. Please try again later.", 1)
						.show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	private void proceedNextPage(){
		if(et_Question.getText().toString().trim().length()>0){
			webParams.put("Questions",et_Question.getText().toString());
			
			Intent i=new Intent(this,AskNextActivity.class);
			i.putExtra("data", webParams);
			if(webParams.get("media_type").equals("1")){
			i.putExtra("imageUri", chosenImageUri.toString());
			System.out.println("imageUri");
			}
			startActivity(i);
			}else{	
				Toast.makeText(this, "Please post a question to proceed", 1).show();
			}	
	}
	public void processWebData(HashMap<String, String> data) {
		generateMediaViewforLink(data);
	}

	@Override
	public void onClick(View arg0) {
		int Id = arg0.getId();
		switch (Id) {
		case R.id.ib_photo:
			Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 1);

			break;

		case R.id.ib_link:
			generateWidgetViewforLink();
			break;

		case R.id.ib_topic:
			generateWidgetViewforTopic();
			break;
		case R.id.button1:
			URL = et_link.getText().toString();
			processWeb();
			break;
		case R.id.b_topicDone:
			saveTopic();
			break;
		case R.id.ll_linkView:
			viewInBrowser();
			break;

		}

	}

	private void viewInBrowser() {
		// Display in Browser.
		Uri uri =  Uri.parse(URL );
		startActivity(new Intent(Intent.ACTION_VIEW, uri));
		System.out.println("view in browser");
	}

	private void saveTopic() {
		if(topicsList.size()>0){
		webParams.put("Category",
				topicsList.get(spinner_topic.getSelectedItemPosition()));
		SelectedTopicPosition = spinner_topic.getSelectedItemPosition();
		}
		resumeDefaultView();
	}

	public void processWeb()  {
	
		if (URL.trim().length() > 0) {
			progressBar.setVisibility(View.VISIBLE);
			
			if(urlPattern.URLType(URL)==0){
				webParams.put("media_type",
						 "2");
				webParams.put("media_link",
						 URL);
			JsoupTask jsoupTask= new JsoupTask(URL);
			jsoupTask.setJsoupTaskListener(new JsoupTaskListener(){
				@Override
				public void onCompleted(HashMap<String, String> result) {
					processWebData(result);
				}});
			jsoupTask.execute();
			}else{
				webParams.put("media_type",
						 "3");
				webParams.put("media_link",
						 URL);
			SimpleYouTubeHelper youtubehelper=new SimpleYouTubeHelper(URL);
			youtubehelper.setYouTubeHelperListener(new YouTubeHelperListener(){
				@Override
				public void onCompleted(HashMap<String, String> result) {
					processWebData(result);					
				}});
			youtubehelper.execute();
			}
		}
		resumeDefaultView();
	}
	
	

	

	private void generateMediaView() {
		linkView.removeAllViewsInLayout();
		linkView.setVisibility(View.VISIBLE);
		View v = inflater.inflate(R.layout.view_weblink, null);
		ImageView image = (ImageView) v.findViewById(R.id.imageView1);
		TextView tv_title = (TextView) v.findViewById(R.id.tv_title);
		TextView tv_content = (TextView) v.findViewById(R.id.tv_content);
		tv_title.setVisibility(View.GONE);
		tv_content.setVisibility(View.GONE);
		ProcessDialog processDialog=new ProcessDialog(this,"Processing...");
		processDialog.start();
		mBitmap=new PhotoProcessor().adjustPhotoRotation(chosenImageUri,this);
		processDialog.end();
		image.setImageBitmap(mBitmap);
		linkView.addView(v);
		webParams.put("media_type",
				 "1");
		webParams.put("media_link",
				 "");
		setClickListener();
		
	}
	
	protected void setClickListener() {
		final String uri=chosenImageUri.toString();
		linkView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i=new Intent(AskActivity.this,PhotoActivity.class);
				i.putExtra("data", uri);
				startActivity(i);
				
			}});
		
	}

	private void generateMediaViewforLink(HashMap<String, String> data) {
		progressBar.setVisibility(View.GONE);
		linkView.removeAllViewsInLayout();
		linkView.setVisibility(View.VISIBLE);
		View v = inflater.inflate(R.layout.view_weblink, null);
		ImageView image = (ImageView) v.findViewById(R.id.imageView1);
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
						((GlobalState) this.getApplicationContext()).options,
						((GlobalState) this.getApplicationContext()).animateFirstListener);

		linkView.addView(v);
	
	}

	private void generateWidgetViewforTopic() {
		llwidget.removeAllViewsInLayout();
		View v = inflater.inflate(R.layout.widget_category, null);
		spinner_topic = (Spinner) v.findViewById(R.id.spinner_topic);
		Button b_topicDone = (Button) v.findViewById(R.id.b_topicDone);
		b_topicDone.setOnClickListener(this);
		spinner_topic.setAdapter(spadapter);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		spinner_topic.setSelection(SelectedTopicPosition);
		v.setLayoutParams(layoutParams);
		llwidget.addView(v);

	}

	private void generateWidgetViewforLink() {
		llwidget.removeAllViewsInLayout();
		View v = inflater.inflate(R.layout.widget_link, null);
		Button b_done = (Button) v.findViewById(R.id.button1);
		et_link = (EditText) v.findViewById(R.id.editText1);
		et_link.setText(URL);
		b_done.setOnClickListener(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		v.setLayoutParams(layoutParams);
		llwidget.addView(v);
	}

	private void resumeDefaultView() {
		llwidget.removeAllViewsInLayout();
		View v = inflater.inflate(R.layout.view_widgets, null);
		ImageView ib_photo = (ImageView) v.findViewById(R.id.ib_photo);
		ImageView ib_link = (ImageView) v.findViewById(R.id.ib_link);
		ImageView ib_topic = (ImageView) v.findViewById(R.id.ib_topic);
		ib_photo.setOnClickListener(this);
		ib_link.setOnClickListener(this);
		ib_topic.setOnClickListener(this);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		v.setLayoutParams(layoutParams);
		llwidget.addView(v);
	}

}
