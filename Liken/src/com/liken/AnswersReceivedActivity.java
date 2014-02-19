package com.liken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import adapters.AnswersAdapter;
import adapters.AnswersReceivedAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import application.GlobalState;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.google.analytics.tracking.android.EasyTracker;
import com.liken.R;
import com.liken.data.SQLiteHelper;
import com.liken.webservice.JSONParser;
import com.liken.webservice.WebTask;
import com.liken.webservice.WebTask.WebTaskListener;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

public class AnswersReceivedActivity extends SherlockActivity {
	 ActionSlideExpandableListView mlvExp;
	  AnswersReceivedAdapter mAnswersAdapter;
	  ArrayList<HashMap<String,String>> data= new ArrayList<HashMap<String,String>>();
	  List<NameValuePair> wp_QueryQuestions=new ArrayList<NameValuePair>();
	  SQLiteHelper sqlitehelper;
	  ArrayList<WebTask> listofTask=new ArrayList<WebTask>();
	  private String UserID; 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sqlitehelper = new SQLiteHelper(this);
		UserID=	sqlitehelper.getUserID();
		setContentView(R.layout.activity_answeredreceived);
		mlvExp= (ActionSlideExpandableListView) findViewById(R.id.list);
		mAnswersAdapter= new AnswersReceivedAdapter(this, data);
		mlvExp.setAdapter(mAnswersAdapter);
	HashMap<String,String> data=	(HashMap<String, String>) getIntent().getSerializableExtra("data");
		 List<NameValuePair> wp_QueryQuestions=new ArrayList<NameValuePair>();
			wp_QueryQuestions.add(new BasicNameValuePair("QuestionID", data.get("QuestionID")));
			wp_QueryQuestions.add(new BasicNameValuePair("function", "getQuestionAnswers"));
			WebTask mwebTask=new WebTask(wp_QueryQuestions, JSONParser.ANSWER_NEW_URL);
			mwebTask.setWebTaskListener(new WebTaskListener(){

				@Override
				public void onCompleted(String data) {
					processresult(data);
					
				}});
			mwebTask.execute();
			listofTask.add(mwebTask);
	}
	@Override
	  public void onStart() {
	    super.onStart();
	  
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }


	protected void processresult(String result) {
	//	u.Username,a.Answer,a.ID,a.User_ID,a.Media_Type,a.Media_Link,lm.Asker_like 
				try {
					JSONObject job = new JSONObject(result);
					int success = job.getInt("success");
					if (success == 1) {
					String jsonArray=job.getString("answers");
					JSONArray jsonarray=new JSONArray(jsonArray);
					for(int i=0;i<jsonarray.length();i++){
					JSONObject object=jsonarray.getJSONObject(i);
					HashMap<String,String> hmData=new HashMap<String,String>();
					hmData.put("Answer", object.getString("Answer"));
					hmData.put("User_ID", object.getString("User_ID"));
					hmData.put("ID", object.getString("ID"));
					hmData.put("a_Media_Type", object.getString("Media_Type"));
					hmData.put("a_Media_Link", object.getString("Media_Link"));
					hmData.put("Asker_like", object.getString("Asker_like"));
					hmData.put("Username", object.getString("Username"));
					data.add(hmData);
					}
					} else {
					
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.i("DATA SIZE",data.size()+"");
				mAnswersAdapter.notifyDataSetChanged();
				
			}	
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		for(int i=0;i<listofTask.size();i++){
			WebTask webTask=listofTask.get(i);
		if(!webTask.isCancelled()){
			webTask.cancel(true);
		}
		}
//		((GlobalState)this.getApplicationContext()).clearAllImageViewDisplay();
		 EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		return super.onMenuItemSelected(featureId, item);
	}

}
