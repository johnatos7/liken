package com.liken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import application.GlobalState;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.liken.R;
import com.liken.data.SQLiteHelper;
import com.liken.webservice.JSONParser;
import com.liken.webservice.WebTask;
import com.liken.webservice.WebTask.WebTaskListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ProfileOtherActivity extends SherlockActivity {

	
	LinearLayout firstRow, secondRow, thirdRow, fourthRow;
	LayoutInflater inflater;
	int screenWidth;
	TextView tv_numberAsked, tv_numberAnswered,tv_name;
	ImageView iv_person;
	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	List<NameValuePair> wp_QueryQuestions = new ArrayList<NameValuePair>();
//	HashMap<String,String> userinfo=new HashMap<String,String>();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	String userID;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_profile);
		iv_person=(ImageView) findViewById(R.id.iv_person);
		tv_numberAsked = (TextView) findViewById(R.id.tv_numberAsked);
		tv_numberAnswered = (TextView) findViewById(R.id.tv_numberAnswered);
		tv_name=(TextView) findViewById(R.id.tv_name);
		firstRow = (LinearLayout) findViewById(R.id.firstRow);
		secondRow = (LinearLayout) findViewById(R.id.secondRow);
		thirdRow = (LinearLayout) findViewById(R.id.thirdRow);
		fourthRow = (LinearLayout) findViewById(R.id.fourthRow);
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int width=0;
		int height=0;
		if (android.os.Build.VERSION.SDK_INT >= 13){
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			 height = size.y;
		}else{
			 width = display.getWidth();  // deprecated
			 height = display.getHeight();  // deprecated
		}
		screenWidth =width;
		inflater = (LayoutInflater) this.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
	
		userID=getIntent().getStringExtra("data");
		
		
		/*
		 * $function = $_POST['function']; if($function=="getProfile"){ $UserID
		 * = $_POST['UserID']; getProfile($UserID); }
		 */
		wp_QueryQuestions.add(new BasicNameValuePair("UserID",userID));
		wp_QueryQuestions.add(new BasicNameValuePair("function", "getOtherProfile"));
		WebTask mwebTask = new WebTask(wp_QueryQuestions,
				JSONParser.PROFILE_URL);
		mwebTask.setWebTaskListener(new WebTaskListener() {

			@Override
			public void onCompleted(String data) {
				processresult(data);

			}
		});
		mwebTask.execute();
		
		
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


	private void getProfilePic() {
				setClickListener(data.get(0).get("ProfilePicPath"));
				imageLoader
				.displayImage(
						data.get(0).get("ProfilePicPath"),
						iv_person,
						((GlobalState) this.getApplicationContext()).options_profile_img,
						((GlobalState)this.getApplicationContext()).animateFirstListener);
	
		
		
	}
	
	protected void setClickListener(final String uri) {
		iv_person.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent i=new Intent(ProfileOtherActivity.this,PhotoActivity.class);
				i.putExtra("data", uri);
				startActivity(i);
				
			}});
		
	}

	protected void processresult(String result) {
	//	u.ProfilePicPath,u.Username,u.Answer_Count,u.Question_Count
		//get userinfo
		System.out.println(result);
			try {
				JSONObject job = new JSONObject(result);
				int success = job.getInt("success");
				if (success == 1) {
				String jsonArray=job.getString("profile");
				JSONArray jsonarray=new JSONArray(jsonArray);
				
				for(int i=0;i<jsonarray.length();i++){
				JSONObject object=jsonarray.getJSONObject(i);
				HashMap<String,String> hmData=new HashMap<String,String>();
				hmData.put("ProfilePicPath", object.getString("ProfilePicPath"));
				hmData.put("Username", object.getString("Username"));
				hmData.put("Answer_Count", object.getString("Answer_Count"));
				hmData.put("Question_Count", object.getString("Question_Count"));
				data.add(hmData);
				setProfileInfoView(hmData);
				}
				
				//get list of topics
						try{
						String topicArray=job.getString("topics");
						JSONArray topicjsonarray=new JSONArray(topicArray);
						ArrayList<String> listTopics=new ArrayList<String>();
						for(int i=0;i<topicjsonarray.length();i++){
						
									String topic=	topicjsonarray.getString(i);
									listTopics.add(topic);	
							}
						generateFollowingView(listTopics);
						}catch(JSONException e){
							
						}
				} else {
				
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		
	}

	private void setProfileInfoView(HashMap<String, String> hmData) {
		tv_numberAsked.setText(hmData.get("Question_Count"));
		tv_numberAnswered.setText(hmData.get("Answer_Count"));
		tv_name.setText(hmData.get("Username"));
		getProfilePic();
	}

	private void generateFollowingView(ArrayList<String> listTopics) {
	
		int numberOfColumns = 3; // number of columns in the grid view
		int padding = 2; // padding space in the grid view
		int columnWidth = (screenWidth - (numberOfColumns + 1) * padding)
				/ numberOfColumns;
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				columnWidth, columnWidth);

		for (int i = 0; i < listTopics.size(); i++) {
			View vi = inflater.inflate(R.layout.gridview_topics_profile, null);
			ImageView image = (ImageView) vi.findViewById(R.id.imageView1);
			TextView tv_topic = (TextView) vi.findViewById(R.id.tv_topic);
			LinearLayout llayout = (LinearLayout) vi.findViewById(R.id.llayout);
			llayout.setLayoutParams(layoutParams);
			String topic = listTopics.get(i);
			int id = Integer.parseInt(topic);
			if (id == 0) {
				image.setImageResource(R.drawable.design);
				tv_topic.setText("Design");
			} else if (id == 1) {

				image.setImageResource(R.drawable.culture);
				tv_topic.setText("Culture");
			} else if (id == 2) {

				image.setImageResource(R.drawable.movies);
				tv_topic.setText("Movies");
			} else if (id == 3) {

				image.setImageResource(R.drawable.music);
				tv_topic.setText("Music");
			} else if (id == 4) {

				image.setImageResource(R.drawable.tech);
				tv_topic.setText("Tech");
			} else if (id == 5) {

				image.setImageResource(R.drawable.random);
				tv_topic.setText("Random");
			} else if (id == 6) {

				image.setImageResource(R.drawable.philosophy);
				tv_topic.setText("Philosophy");
			} else if (id == 7) {

				image.setImageResource(R.drawable.sports);
				tv_topic.setText("Sports");
			} else if (id == 8) {

				image.setImageResource(R.drawable.business);
				tv_topic.setText("Business");
			} else if (id == 9) {

				image.setImageResource(R.drawable.games);
				tv_topic.setText("Games");
			} else if (id == 10) {

				image.setImageResource(R.drawable.science);
				tv_topic.setText("Science");
			} else if (id == 11) {
				image.setImageResource(R.drawable.literature);
				tv_topic.setText("literature");
			}
			if (i >= 0 && i < 3) {
				firstRow.addView(vi);
			} else if (i >= 3 && i < 6) {
				secondRow.addView(vi);
			} else if (i >= 6 && i < 9) {
				thirdRow.addView(vi);
			} else {
				fourthRow.addView(vi);
			}

		}

	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
