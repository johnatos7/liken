package com.liken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.liken.R;
import com.liken.data.SQLiteHelper;
import com.liken.dialogs.ProcessDialog;
import com.liken.webservice.JSONParser;
import com.liken.webservice.S3MediaUtil;
import com.liken.webservice.WebTask;
import com.liken.webservice.S3MediaUtil.S3MediaTaskListener;
import com.liken.webservice.S3MediaUtil.S3TaskResult;
import com.liken.webservice.WebTask.WebTaskListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AskNextActivity extends SherlockActivity {
	Uri chosenImageUri;
	HashMap<String, String> webParams;
	EditText et_Opinion;
	SQLiteHelper sqlitehelper=new SQLiteHelper(this);
	ProcessDialog mWebProcessDialog;
	int inputLength;
	//webparam -Questions, Answer, User_id,media_type,Media_link, Category
	//Answer,Question,MediaLink,UseriID,MediaType,Category
		//media type 0- no media,media
		//media type 1- photo media
		//media type 2- link media
		//media type 3- youtube media
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_ask_next);
		TextView tv_Question = (TextView) findViewById(R.id.tv_Question);
		et_Opinion = (EditText) findViewById(R.id.editText1);
		Intent intent = getIntent();
		webParams = (HashMap<String, String>) intent
				.getSerializableExtra("data");
		Log.i("webParams",webParams.toString());
		tv_Question.setText(webParams.get("Questions"));
		if(webParams.get("media_type").equals("1")){
		chosenImageUri = Uri.parse(intent.getStringExtra("imageUri"));
		}
		
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

		et_Opinion.addTextChangedListener(textWatcher);
		
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
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("Done")) {
			if (et_Opinion.getText().toString().trim().length() > 0) {
				webParams.put("Answer",et_Opinion.getText().toString());
				processUploadPost();
			}else{
				Toast.makeText(this, "Please enter your opinion", 1).show();
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
			processWeb();
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
		processWeb();
	}
	
	private void processWeb(){
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
}
