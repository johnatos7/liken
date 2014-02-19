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
import adapters.LikenAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

public class LikenFragment extends SherlockFragment {

	ListView mlvExp;
	LikenAdapter mLikenAdapter;
	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	List<NameValuePair> wp_QueryQuestions = new ArrayList<NameValuePair>();
	SQLiteHelper sqlitehelper;
	ArrayList<WebTask> listofTask = new ArrayList<WebTask>();
	private String UserID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sqlitehelper = new SQLiteHelper(getActivity());
		UserID = sqlitehelper.getUserID();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_liken, container, false);
		mlvExp = (ListView) v.findViewById(R.id.listView1);
		mLikenAdapter = new LikenAdapter(mlvExp, getActivity(), data);
		mlvExp.setAdapter(mLikenAdapter);

		return v;

	}
	@Override
	public void onStart() {
	  super.onStart();
	  
	  EasyTracker tracker = EasyTracker.getInstance(getActivity());
	    tracker.set(Fields.SCREEN_NAME, "Liken Fragment");
	    tracker.send(MapBuilder.createAppView().build());
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		for (int i = 0; i < listofTask.size(); i++) {
			WebTask webTask = listofTask.get(i);
			if (!webTask.isCancelled()) {
				webTask.cancel(true);
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		List<NameValuePair> wp_QueryQuestions = new ArrayList<NameValuePair>();
		wp_QueryQuestions.add(new BasicNameValuePair("UserID", UserID));
		wp_QueryQuestions.add(new BasicNameValuePair("function",
				"getLikeMindedPeople"));
		WebTask mwebTask = new WebTask(wp_QueryQuestions,
				JSONParser.LIKEMIND_URL);
		mwebTask.setWebTaskListener(new WebTaskListener() {

			@Override
			public void onCompleted(String data) {
				processresult(data);

			}
		});
		mwebTask.execute();
		listofTask.add(mwebTask);
	}

	protected void processresult(String result) {
		// {"Question_ID":"30","Questions":"black market level elken market level let key lock will welcome vehicle",
		//"Answer":"","User_ID":"161","Media_Type":"1","Media_Link":"16120130921_005622","Category":"0","people":"answerer",
		//"answerername":"Lue Hong","askername":"Lue Hong",
		//"answererpic":"http:\/\/s3.amazonaws.com\/likemindasulue_profilepictures\/LueHong_548392913.jpeg",
		//"askerpic":"http:\/\/s3.amazonaws.com\/likemindasulue_profilepictures\/LueHong_548392913.jpeg",
		//"askerid":"161","answererid":"161"
		try {
			JSONObject job = new JSONObject(result);
			int success = job.getInt("success");
			if (success == 1) {
				String jsonArray = job.getString("people");
				JSONArray jsonarray = new JSONArray(jsonArray);
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject object = jsonarray.getJSONObject(i);
					HashMap<String, String> hmData = new HashMap<String, String>();
					hmData.put("Question_ID", object.getString("Question_ID"));
					hmData.put("Questions", object.getString("Questions"));
					hmData.put("Answer", object.getString("Answer"));
					hmData.put("Media_Type", object.getString("Media_Type"));
					hmData.put("Media_Link", object.getString("Media_Link"));
					hmData.put("Category", object.getString("Category"));
					hmData.put("people", object.getString("people"));
					hmData.put("answerername", object.getString("answerername"));
					hmData.put("askername", object.getString("askername"));
					hmData.put("answererpic", object.getString("answererpic"));
					hmData.put("askerpic", object.getString("askerpic"));
					hmData.put("askerid", object.getString("askerid"));
					hmData.put("answererid", object.getString("answererid"));
					data.add(hmData);
				}
			} else {

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i("DATA SIZE", data.size() + "");
		mLikenAdapter.notifyDataSetChanged();

	}

}
