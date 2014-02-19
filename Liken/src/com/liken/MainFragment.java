package com.liken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import adapters.MainAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import application.GlobalState;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.liken.MainActivity.OnTopicChangedListener;
import com.liken.data.SQLiteHelper;
import com.liken.webservice.JSONParser;
import com.liken.webservice.WebTask;
import com.liken.webservice.WebTask.WebTaskListener;

public class MainFragment extends SherlockFragment implements OnClickListener {
	private ViewPager mPager;
	
	ArrayList<HashMap<String,String>> data=new ArrayList<HashMap<String,String>>();
	ArrayList<WebTask> listofTask=new ArrayList<WebTask>();
	List<NameValuePair> wp_QueryQuestions=new ArrayList<NameValuePair>();
	SQLiteHelper sqlitehelper;
	String UserID;
	MainAdapter mainAdapter;
	String TOPIC;
	int pagetotalscrolled=8;
	int page=0;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mainAdapter=new MainAdapter(getActivity(), data, mPager,UserID);
		mPager.setAdapter(mainAdapter);
		mPager.setPageMargin(120);
		mPager.setOffscreenPageLimit(2);
		//whenever this fragment is first created
		if(TOPIC.equals("Trending")){
		wp_QueryQuestions.add(new BasicNameValuePair("UserID", UserID));
		wp_QueryQuestions.add(new BasicNameValuePair("RequestType", "UserID"));
		getQuestions(wp_QueryQuestions);
		}else{
			wp_QueryQuestions.add(new BasicNameValuePair("UserID", UserID));
			wp_QueryQuestions.add(new BasicNameValuePair("CommunityType", TOPIC));
			wp_QueryQuestions.add(new BasicNameValuePair("RequestType", "CommunityType"));
			getQuestions(wp_QueryQuestions);
			
		}
	}
	@Override
	public void onStart() {
	  super.onStart();
	  
	  EasyTracker tracker = EasyTracker.getInstance(getActivity());
	    tracker.set(Fields.SCREEN_NAME, "Main Fragment");
	    tracker.send(MapBuilder.createAppView().build());
	}
	protected void processData(String result) {
		data.clear();
		try {
			JSONObject job = new JSONObject(result);
			int success = job.getInt("success");
			if (success == 1) {
			String jsonArray=job.getString("questions");
			JSONArray jsonarray=new JSONArray(jsonArray);
			for(int i=0;i<jsonarray.length();i++){
			JSONObject object=jsonarray.getJSONObject(i);
			HashMap<String,String> hmData=new HashMap<String,String>();
			hmData.put("QuestionID", object.getString("QuestionID"));
			hmData.put("Questions", object.getString("Questions"));
			hmData.put("Answer", object.getString("Answer"));
			hmData.put("Answered", object.getString("Answered"));
			hmData.put("User_ID", object.getString("User_ID"));
			hmData.put("Media_Type", object.getString("Media_Type"));
			hmData.put("Category", object.getString("Category"));
			hmData.put("Media_Link", object.getString("Media_Link"));
			hmData.put("Count", object.getString("Count"));
			hmData.put("Username", object.getString("Username"));
			data.add(hmData);
			}
			} else {
			
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("DATA SIZE",data.size()+"");
		mainAdapter.notifyDataSetChanged();
	}
	
	private void getQuestions(List<NameValuePair> wp_QueryQuestions){
		WebTask getQuestionsTask=new WebTask(wp_QueryQuestions, new JSONParser().QUERY_QUESTION_URL);
		listofTask.add(getQuestionsTask);
		getQuestionsTask.setWebTaskListener(new WebTaskListener(){

			@Override
			public void onCompleted(String data) {
			processData(data);	
			}});
		getQuestionsTask.execute();	
			
	}
	
	private void getMoreQuestions(List<NameValuePair> wp_QueryQuestions){
		WebTask getQuestionsTask=new WebTask(wp_QueryQuestions, new JSONParser().QUERY_QUESTION_URL);
		listofTask.add(getQuestionsTask);
		getQuestionsTask.setWebTaskListener(new WebTaskListener(){

			@Override
			public void onCompleted(String data) {
			processMoreData(data);	
			}});
		getQuestionsTask.execute();	
			
	}
	
	protected void processMoreData(String result) {
	//	data.clear();
		try {
			JSONObject job = new JSONObject(result);
			int success = job.getInt("success");
			if (success == 1) {
			String jsonArray=job.getString("questions");
			JSONArray jsonarray=new JSONArray(jsonArray);
			for(int i=0;i<jsonarray.length();i++){
			JSONObject object=jsonarray.getJSONObject(i);
			HashMap<String,String> hmData=new HashMap<String,String>();
			hmData.put("QuestionID", object.getString("QuestionID"));
			hmData.put("Questions", object.getString("Questions"));
			hmData.put("Answer", object.getString("Answer"));
			hmData.put("Answered", object.getString("Answered"));
			hmData.put("User_ID", object.getString("User_ID"));
			hmData.put("Media_Type", object.getString("Media_Type"));
			hmData.put("Category", object.getString("Category"));
			hmData.put("Media_Link", object.getString("Media_Link"));
			hmData.put("Count", object.getString("Count"));
			hmData.put("Username", object.getString("Username"));
			data.add(hmData);
			}
			} else {
			
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("DATA SIZE",data.size()+"");
		mainAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 sqlitehelper=new SQLiteHelper(getActivity());
		UserID=	sqlitehelper.getUserID();
		TOPIC=((GlobalState) getActivity().getApplication()).getTopic();
	
		//whenever the fragment is at the current container
		((MainActivity) getActivity()).SetOnTopicChangedListener(new OnTopicChangedListener(){

			@Override
			public void onTopicChanged(String topic) {
				wp_QueryQuestions.add(new BasicNameValuePair("UserID", UserID));
				wp_QueryQuestions.add(new BasicNameValuePair("CommunityType", topic));
				wp_QueryQuestions.add(new BasicNameValuePair("RequestType", "CommunityType"));
				getQuestions(wp_QueryQuestions);
				
			}});
	
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		mPager = (ViewPager) v.findViewById(R.id.kk_pager);
		ImageView iv_Ask =(ImageView) v.findViewById(R.id.iv_ask);
		
		iv_Ask.setOnClickListener(this);
		//setupEffect(TransitionEffect.CubeOut);
		return v;
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
	public void onClick(View v) {
		if(v.getId()==R.id.iv_ask){
			Intent i=new Intent(getActivity(),AskActivity.class);
			startActivity(i);
		}
		
	}






}
