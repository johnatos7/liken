package com.liken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import adapters.TrendingAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import application.GlobalState;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.liken.R;
import com.liken.data.SQLiteHelper;
import com.liken.webservice.JSONParser;
import com.liken.webservice.WebTask;
import com.liken.webservice.WebTask.WebTaskListener;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

public class QuestionsFragment extends SherlockFragment {
	 ActionSlideExpandableListView mlvExp;
	  TrendingAdapter mTrendingAdapter;
	  ArrayList<HashMap<String,String>> data= new ArrayList<HashMap<String,String>>();
	  List<NameValuePair> wp_QueryQuestions=new ArrayList<NameValuePair>();
	  SQLiteHelper sqlitehelper;
	  ArrayList<WebTask> listofTask=new ArrayList<WebTask>();
	private String UserID; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sqlitehelper = new SQLiteHelper(getActivity());
			UserID=	sqlitehelper.getUserID();
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
	//	((GlobalState) getActivity().getApplicationContext()).clearAllImageViewDisplay();
	}
	@Override
	public void onStart() {
	  super.onStart();
	  
	  EasyTracker tracker = EasyTracker.getInstance(getActivity());
	    tracker.set(Fields.SCREEN_NAME, "Questions Fragment");
	    tracker.send(MapBuilder.createAppView().build());
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_trending, container, false);
				mlvExp= (ActionSlideExpandableListView) v.findViewById(R.id.list);
				mTrendingAdapter= new TrendingAdapter(getActivity(), data);
		mlvExp.setAdapter(mTrendingAdapter);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		wp_QueryQuestions.add(new BasicNameValuePair("UserID", UserID));
		wp_QueryQuestions.add(new BasicNameValuePair("RequestType", "getPostedQuestions"));
		WebTask mwebTask=new WebTask(wp_QueryQuestions, JSONParser.QUERY_QUESTION_URL);
		mwebTask.setWebTaskListener(new WebTaskListener(){

			@Override
			public void onCompleted(String data) {
				processresult(data);
				
			}});
		mwebTask.execute();
		listofTask.add(mwebTask);
	}

	protected void processresult(String result) {
	//	q.Question_ID,q.Questions,q.Answer,q.User_ID,q.Media_Type,q.Media_Link,q.Category,ac.Count
		try {
			JSONObject job = new JSONObject(result);
			int success = job.getInt("success");
			if (success == 1) {
			String jsonArray=job.getString("questions");
			JSONArray jsonarray=new JSONArray(jsonArray);
			for(int i=0;i<jsonarray.length();i++){
			JSONObject object=jsonarray.getJSONObject(i);
			HashMap<String,String> hmData=new HashMap<String,String>();
			hmData.put("QuestionID", object.getString("Question_ID"));
			hmData.put("Questions", object.getString("Questions"));
			hmData.put("Answer", object.getString("Answer"));
			hmData.put("User_ID", object.getString("User_ID"));
			hmData.put("Media_Type", object.getString("Media_Type"));
			hmData.put("Category", object.getString("Category"));
			hmData.put("Media_Link", object.getString("Media_Link"));
			hmData.put("Count", object.getString("Count"));
			data.add(hmData);
			}
			} else {
			
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("DATA SIZE",data.size()+"");
		mTrendingAdapter.notifyDataSetChanged();
		
	}	

}
