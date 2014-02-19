package com.liken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.liken.R;
import com.liken.constant.Constants;
import com.liken.data.SQLiteHelper;
import com.liken.dialogs.ProcessDialog;
import com.liken.webservice.JSONParser;
import com.liken.webservice.WebTask;
import com.liken.webservice.WebTask.WebTaskListener;

public class AskerAnswerActivity extends SherlockActivity {
	 ArrayList<HashMap<String,String>> data= new ArrayList<HashMap<String,String>>();
	  List<NameValuePair> wp_QueryQuestions=new ArrayList<NameValuePair>();
	  SQLiteHelper sqlitehelper;
	  ArrayList<WebTask> listofTask=new ArrayList<WebTask>();
	private String UserID; 
	TextView tv_Answer;
	TextView tv_Topic;
	TextView tv_AskerName;
	Button b_like;
	LinearLayout ll_MediaView;
	HashMap<String,String> hmData;
	ProcessDialog progressDialog;
	int Status=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_askeranswer);
		ll_MediaView= (LinearLayout) findViewById(R.id.ll_questions);
		tv_Answer=(TextView) findViewById(R.id.tv_answer);
		tv_Topic=(TextView) findViewById(R.id.tv_topic);
		tv_AskerName=(TextView) findViewById(R.id.tv_askerName);
		b_like=(Button) findViewById(R.id.b_answer);
		progressDialog=new ProcessDialog(this,"processing...");
		sqlitehelper = new SQLiteHelper(this);
		UserID=	sqlitehelper.getUserID();
		hmData=(HashMap<String, String>) getIntent().getSerializableExtra("data");
	
		 List<NameValuePair> wp_QueryQuestions=new ArrayList<NameValuePair>();
			wp_QueryQuestions.add(new BasicNameValuePair("QuestionID", hmData.get("Question_ID")));
			wp_QueryQuestions.add(new BasicNameValuePair("function", "GetAskerAnswer"));
			wp_QueryQuestions.add(new BasicNameValuePair("AnswerID", hmData.get("ID")));
			WebTask mwebTask=new WebTask(wp_QueryQuestions, JSONParser.ANSWER_NEW_URL);
			mwebTask.setWebTaskListener(new WebTaskListener(){

				@Override
				public void onCompleted(String data) {
					processresult(data);
					
				}});
			mwebTask.execute();
			listofTask.add(mwebTask);
	}
	
	protected void goToProfile(String tag) {
		Intent i=new Intent(this,ProfileOtherActivity.class);
		i.putExtra("data", tag);
		startActivity(i);
	}
	protected void AnswerLike( final View v) {
		progressDialog.start();
		Status=1;
		 List<NameValuePair> wp_QueryQuestions=new ArrayList<NameValuePair>();
			wp_QueryQuestions.add(new BasicNameValuePair("AnswerID", hmData.get("ID")));
			wp_QueryQuestions.add(new BasicNameValuePair("Status", "1"));
			wp_QueryQuestions.add(new BasicNameValuePair("function", "LikeorDisLikeAnswersfromAnswerer"));
			WebTask mwebTask=new WebTask(wp_QueryQuestions, JSONParser.LIKEMIND_URL);
			mwebTask.setWebTaskListener(new WebTaskListener(){

				@Override
				public void onCompleted(String data) {
					processresult(data,v);
					
				}});
			mwebTask.execute();
			
		
	}
	
	@Override
	  public void onStart() {
	    super.onStart();
	  
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }


	
	protected void processresult(String data2, View v) {
		try {
			JSONObject job = new JSONObject(data2);
			int success = job.getInt("success");
			if (success == 1) {
			b_like.setText("Liked");
			b_like.setTextColor(Color.parseColor("#929292"));
			b_like.setClickable(false);
			} else {
			
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		progressDialog.end();
	}

	@Override
	protected void onStop() {
		super.onStop();
		 Intent returnIntent = new Intent();
		 returnIntent.putExtra("result",Status);
		 setResult(RESULT_OK,returnIntent);     
		 finish();
		 EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}
	protected void processresult(String result) {
//Question_ID,Questions,Answer,Media_Type,Media_Link,Category,User_ID
		System.out.println(result);
		try {
			JSONObject job = new JSONObject(result);
			int success = job.getInt("success");
			if (success == 1) {
			String answerlike=job.getString("answer_like");
			String jsonArray=job.getString("answers");
			JSONArray jsonarray=new JSONArray(jsonArray);
			for(int i=0;i<jsonarray.length();i++){
			JSONObject object=jsonarray.getJSONObject(i);
			HashMap<String,String> hmData=new HashMap<String,String>();
			hmData.put("Answer", object.getString("Answer"));
			hmData.put("Question_ID", object.getString("Question_ID"));
			hmData.put("Media_Type", object.getString("Media_Type"));
			hmData.put("Media_Link", object.getString("Media_Link"));
			hmData.put("Questions", object.getString("Questions"));
			hmData.put("Category", object.getString("Category"));
			hmData.put("User_ID", object.getString("User_ID"));
			hmData.put("answerlike", answerlike);
			data.add(hmData);
			}
			} else {
			
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ContentInflate();
		
		
	}
	private void ContentInflate() {
		tv_Answer.setText(data.get(0).get("Answer"));
		String topic=data.get(0).get("Category");
		String topicName=new Constants().getTopicName(Integer.parseInt(topic));
		tv_Topic.setText(topicName);
		tv_AskerName.setText(hmData.get("Username"));
		tv_AskerName.setTag(hmData.get("User_ID"));
		tv_AskerName.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				goToProfile((String) v.getTag());
				
			}

		});
		if(data.get(0).get("answerlike").equals("1") ||data.get(0).get("answerlike").equals("2") ){
			b_like.setText("Liked");
			b_like.setTextColor(Color.parseColor("#929292"));
			b_like.setClickable(false);
		}else{
			b_like.setText("Like");
			b_like.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
				AnswerLike(v);
					
				}});
			
		}
		
	}	

}
