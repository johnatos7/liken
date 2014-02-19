package com.liken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import urlpattern.URLPattern;
import WebContent.JsoupTask;
import WebContent.SimpleYouTubeHelper;
import WebContent.JsoupTask.JsoupTaskListener;
import WebContent.SimpleYouTubeHelper.YouTubeHelperListener;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class AnswerActivity extends SherlockActivity implements OnClickListener {
LinearLayout linkView,llwidget;
EditText et_Answer,et_link;
ProgressBar progressBar;
ProcessDialog  mWebProcessDialog;
Bitmap mBitmap;
SQLiteHelper sqlitehelper = new SQLiteHelper(this);
String URL = "";
URLPattern urlPattern=new URLPattern();
Uri chosenImageUri;
HashMap<String,String> hmData;

String MediaType="0";
String MediaLink="";
protected ImageLoader imageLoader = ImageLoader.getInstance();
LayoutInflater inflater;
int inputLength;
/*$QuestionID = $_POST['QuestionID'];
$Answer = $_POST['Answer'];
$UserID = $_POST['UserID'];
$MediaType = $_POST['MediaType'];
$MediaLink = $_POST['MediaLink'];*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_answer);
		et_Answer=(EditText)findViewById(R.id.editText1);
		progressBar=(ProgressBar)findViewById(R.id.progressBar);
		linkView = (LinearLayout) findViewById(R.id.ll_linkView);
		llwidget = (LinearLayout) findViewById(R.id.ll_widgets);
		ImageView ib_photo = (ImageView) findViewById(R.id.ib_photo);
		ImageView ib_link = (ImageView) findViewById(R.id.ib_link);
		ib_photo.setOnClickListener(this);
		ib_link.setOnClickListener(this);	
		linkView.setVisibility(View.GONE);
		hmData=(HashMap<String, String>) getIntent().getSerializableExtra("data");
		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
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

		et_Answer.addTextChangedListener(textWatcher);
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
	
	public boolean onPrepareOptionsMenu(Menu menu) {

	    MenuItem item=menu.getItem(0);
	    item.setTitle((200-inputLength)+"");
	    return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("200").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("Done").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			chosenImageUri = data.getData();
			generateMediaView();
			/*try {
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
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("Done")) {
			if (et_Answer.getText().toString().trim().length() > 0) {
				processUploadAnswer();
			}
		}
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	private void processUploadAnswer() {
		 mWebProcessDialog=new ProcessDialog(this,"Processing...");
			mWebProcessDialog.start();
		
			if(MediaType.equals("1")){
			
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
			MediaLink=((S3TaskResult) result).getPictureName(); 
			processWebforUpload();
		}
		
		private void processWebforUpload(){
			/*$QuestionID = $_POST['QuestionID'];
			$Answer = $_POST['Answer'];
			$UserID = $_POST['UserID'];
			$MediaType = $_POST['MediaType'];
			$MediaLink = $_POST['MediaLink'];*/
			List<NameValuePair> data=new ArrayList<NameValuePair>();
			data.add(new BasicNameValuePair("QuestionID", hmData.get("QuestionID")));
			data.add(new BasicNameValuePair("Answer", et_Answer.getText().toString()));
			data.add(new BasicNameValuePair("UserID", sqlitehelper.getUserID()));
			data.add(new BasicNameValuePair("MediaType",MediaType ));
			data.add(new BasicNameValuePair("MediaLink",MediaLink));
			WebTask webTask=new WebTask(data,new JSONParser().ANSWER_URL);
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

	@Override
	public void onClick(View v) {
		int Id = v.getId();
		switch (Id) {
		case R.id.ib_photo:
			Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 1);

			break;

		case R.id.ib_link:
			generateWidgetViewforLink();
			break;	
		case R.id.button1:
			processWeb();
			break;
		}
	
		
	}
		
		public void processWeb()  {
			URL = et_link.getText().toString();
			if (URL.trim().length() > 0) {
				progressBar.setVisibility(View.VISIBLE);
				
				if(urlPattern.URLType(URL)==0){
					MediaType="2";
					MediaLink=URL;
				JsoupTask jsoupTask= new JsoupTask(URL);
				jsoupTask.setJsoupTaskListener(new JsoupTaskListener(){
					@Override
					public void onCompleted(HashMap<String, String> result) {
						processWebData(result);
					}});
				jsoupTask.execute();
				}else{
					MediaType="3";
					MediaLink=URL;
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
		
		public void processWebData(HashMap<String, String> data) {
			generateMediaViewforLink(data);
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
			MediaType="1";
			setClickListener();
		
		}
		
		protected void setClickListener() {
			final String uri=chosenImageUri.toString();
			linkView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent i=new Intent(AnswerActivity.this,PhotoActivity.class);
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
