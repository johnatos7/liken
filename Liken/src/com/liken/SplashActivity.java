package com.liken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.liken.data.SQLiteHelper;
import com.liken.webservice.JSONParser;
import com.liken.webservice.WebTask;
import com.liken.webservice.WebTask.WebTaskListener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends Activity {
	SQLiteHelper SQLiteHelper;
	JSONParser jsonparser=new JSONParser();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SQLiteHelper= new SQLiteHelper(this);
		HashMap<String,String> userData=(HashMap<String, String>) getIntent().getSerializableExtra("userData");
		String activity=getIntent().getStringExtra("activity");
		saveUserDatatoDB(userData);	
		if(activity!=null){
			//new user
		Intent i= new Intent(this,TopicsActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
			);
		startActivity(i);
		}else{
			//old user
			getTopics(userData);
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
	
	private void goToMain(){
		
		Intent i= new Intent(this,MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
			);
		startActivity(i);
		
	}

	private void getTopics(HashMap<String, String> userData) {
		String id=userData.get("id");
		List<NameValuePair>param=new ArrayList<NameValuePair>();
		param.add(new BasicNameValuePair("UserID",id));		
		WebTask webTask=new WebTask(param,  jsonparser.GET_COMMUNITY_URL);
		webTask.setWebTaskListener(new WebTaskListener()
		{
			@Override
			public void onCompleted(String data) {
			processWebResult(data);		
			}		   
		});
		webTask.execute();
	}
	
	public void processWebResult(String result){
		try {
			JSONObject job = new JSONObject(result);
			int success = job.getInt("success");
			if (success == 1) {
				
				JSONArray data=(JSONArray) job.getJSONArray("data");
				saveTopicstoDB(data);
				
			} else if (success==0){
				Intent i= new Intent(this,TopicsActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
					);
				startActivity(i);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void saveTopicstoDB(JSONArray data2) {
		ArrayList<ContentValues> data=new ArrayList<ContentValues>();
		if(data2.length()==0){
			Intent i= new Intent(this,TopicsActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
				);
			startActivity(i);
		}else{
		for (Integer i = 0; i < data2.length(); i++) {			
				ContentValues topic=new ContentValues();
				try {
					topic.put("id",(String) data2.get(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				data.add(topic);		
		}
		SQLiteHelper.insertTopics(data);
		goToMain();
		}
	}

	private void saveUserDatatoDB(HashMap<String, String> userData) {
		String id=userData.get("id");
		String profilepicpath=userData.get("ProfilePicPath");
		String username=userData.get("username");
		String facebookid=userData.get("facebookid");
		String email=userData.get("email");
		ContentValues dbData=new ContentValues();
		dbData.put("id", id);
		dbData.put("profilepicpath", profilepicpath);
		dbData.put("username", username);
		dbData.put("facebookid", facebookid);
		dbData.put("email", email);
		SQLiteHelper.insertprofile(dbData);
		
		
	}

}
