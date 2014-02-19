package com.liken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import adapters.TopicsAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.liken.R;
import com.liken.data.SQLiteHelper;
import com.liken.dialogs.ProcessDialog;
import com.liken.webservice.JSONParser;
import com.liken.webservice.WebTask;
import com.liken.webservice.WebTask.WebTaskListener;

public class TopicsActivity extends SherlockActivity  {
	TopicsAdapter topicsAdapter;
	HashMap<Integer, Integer> listTopicSelected = new HashMap<Integer, Integer>();
	GridView gv_topics;
	SQLiteHelper sqlitehelper = new SQLiteHelper(this);
	JSONParser jsonParser = new JSONParser();
	 ProcessDialog processdialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_choosetopic);
		gv_topics = (GridView) findViewById(R.id.gridView1);
		topicsAdapter = new TopicsAdapter(this, listTopicSelected);
		gv_topics.setAdapter(topicsAdapter);
		gv_topics.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				topicSelected(pos);
			}
		});
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
	private void init() {
		for (int i = 0; i < 12; i++) {
			listTopicSelected.put(i, 0);
		}

	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		System.out.println("Please press Next on the top right screen to proceed");
	}

	private void topicSelected(int pos) {
		if (listTopicSelected.get(pos) != 1) {
			// selected
			listTopicSelected.put(pos, 1);
		} else {
			// unselected
			listTopicSelected.put(pos, 0);
		}
		topicsAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("DONE").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("DONE")) {
			topicSelectionDone();
		}
		return super.onOptionsItemSelected(item);
	}

	private void topicSelectionDone() {
		JSONArray jsonarray = GenerateJSONArray(listTopicSelected);
		if (jsonarray.length() > 0) {
			processdialog=new ProcessDialog(this, "Please Wait...");
			processdialog.start();
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			String userID = sqlitehelper.getUserID();
			data.add(new BasicNameValuePair("UserID", userID));
			data.add(new BasicNameValuePair("CommunityList", jsonarray
					.toString()));
			WebTask webTask=new WebTask(data,
					jsonParser.INSERT_COMMUNITY_URL);
			webTask.setWebTaskListener(new WebTaskListener()
			{
				@Override
				public void onCompleted(String data) {
					processdialog.end();
				processWebResult(data);		
				}		   
			});
			webTask.execute();
		} else {
			Toast.makeText(this, "Please choose at least 1 topic to follow", 1)
					.show();
		}
	}

	private JSONArray GenerateJSONArray(
			HashMap<Integer, Integer> listTopicSelected2) {
		JSONArray jsonarray = new JSONArray();
		for (int i = 0; i < 12; i++) {
			if (listTopicSelected2.get(i) == 1) {
				jsonarray.put(i);
			}
		}
		return jsonarray;
	}

	public void processWebResult(String result) {
		try {
			JSONObject job = new JSONObject(result);
			int success = job.getInt("success");
			if (success == 1) {
				saveTopicstoDB();
				goToMainPage();
			} else {
				Toast.makeText(this,
						"Oops, something is wrong. Please try again later.", 1)
						.show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void saveTopicstoDB() {
		ArrayList<ContentValues> data=new ArrayList<ContentValues>();
		for (Integer i = 0; i < 12; i++) {
			if (listTopicSelected.get(i) == 1) {
				ContentValues topic=new ContentValues();
				topic.put("id",i);
				data.add(topic);	
			}
		}
		sqlitehelper.insertTopics(data);	
	}

	private void goToMainPage() {
		Intent i = new Intent(this, MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
	}

}
