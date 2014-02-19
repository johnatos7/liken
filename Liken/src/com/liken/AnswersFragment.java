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

public class AnswersFragment extends SherlockFragment {
	 ActionSlideExpandableListView mlvExp;
	  AnswersAdapter mAnswersAdapter;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_answers, container, false);
		mlvExp= (ActionSlideExpandableListView) v.findViewById(R.id.list);
		mAnswersAdapter= new AnswersAdapter(mlvExp,getActivity(), data);
		mlvExp.setAdapter(mAnswersAdapter);
		
		return v;
		
	}
	@Override
	public void onStart() {
	  super.onStart();
	  
	  EasyTracker tracker = EasyTracker.getInstance(getActivity());
	    tracker.set(Fields.SCREEN_NAME, "Answers Fragment");
	    tracker.send(MapBuilder.createAppView().build());
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
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		 List<NameValuePair> wp_QueryQuestions=new ArrayList<NameValuePair>();
		wp_QueryQuestions.add(new BasicNameValuePair("UserID", UserID));
		wp_QueryQuestions.add(new BasicNameValuePair("function", "GetPostedAnswers"));
		WebTask mwebTask=new WebTask(wp_QueryQuestions, JSONParser.ANSWER_NEW_URL);
		mwebTask.setWebTaskListener(new WebTaskListener(){

			@Override
			public void onCompleted(String data) {
				processresult(data);
				
			}});
		mwebTask.execute();
		listofTask.add(mwebTask);
	}

	protected void processresult(String result) {
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
			hmData.put("ID", object.getString("ID"));
			hmData.put("Question_ID", object.getString("Question_ID"));
			hmData.put("a_Media_Type", object.getString("a_Media_Type"));
			hmData.put("a_Media_Link", object.getString("a_Media_Link"));
			hmData.put("Asker_like", object.getString("Asker_like"));
			hmData.put("Answer_like", object.getString("Answer_like"));
			hmData.put("Questions", object.getString("Questions"));
			hmData.put("q_Media_Type", object.getString("q_Media_Type"));
			hmData.put("q_Media_Link", object.getString("q_Media_Link"));
			hmData.put("Category", object.getString("Category"));
			hmData.put("Username", object.getString("Username"));
			hmData.put("User_ID", object.getString("User_ID"));
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

}
