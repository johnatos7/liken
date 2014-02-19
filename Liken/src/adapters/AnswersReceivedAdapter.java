package adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liken.R;
import com.liken.ProfileOtherActivity;
import com.liken.contentinflater.UserAnswersPageInflater;
import com.liken.customviews.TypefaceTextView;
import com.liken.dialogs.ProcessDialog;
import com.liken.webservice.JSONParser;
import com.liken.webservice.WebTask;
import com.liken.webservice.WebTask.WebTaskListener;

public class AnswersReceivedAdapter extends BaseAdapter {
	LayoutInflater inflater;
	ArrayList<HashMap<String, String>> data;
	private Activity activity;
	ProcessDialog progressDialog;
	public AnswersReceivedAdapter(Activity activity,ArrayList<HashMap<String,String>> data) {
		this.data=data;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.activity=activity;
		progressDialog=new ProcessDialog(activity,"processing...");
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		TypefaceTextView tv_Question = null;
		Button b_like;
		Button b_Answers = null;
		LinearLayout ll_Questions = null;
		TypefaceTextView tv_answererName=null;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.listview_answers_received,parent, false);
			tv_Question=(TypefaceTextView) vi.findViewById(R.id.expandable_toggle_button);
			b_Answers=(Button) vi.findViewById(R.id.b_answers);
			b_like=(Button) vi.findViewById(R.id.button_like);
			ll_Questions=(LinearLayout) vi.findViewById(R.id.ll_questions);
			tv_answererName=(TypefaceTextView) vi.findViewById(R.id.tv_answererName);
			final HashMap<String,String>tempdata=data.get(position);
			tv_answererName.setTag(tempdata.get("User_ID"));
			tv_answererName.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					goToProfile((String) v.getTag());
					
				}

			});
			if(tempdata.get("Asker_like").equals("1")||tempdata.get("Asker_like").equals("2")){
				b_like.setBackgroundResource(R.drawable.button_liked);
			}else{
				b_like.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						AnswerLike(tempdata,v);
						
					}});
			}
			tv_answererName.setText(tempdata.get("Username"));
			 new UserAnswersPageInflater(ll_Questions,tv_Question,b_Answers,tempdata,inflater,activity).inflate();
		}
		
		
		return vi;
	}
	
	protected void goToProfile(String tag) {
		Intent i=new Intent(activity,ProfileOtherActivity.class);
		i.putExtra("data", tag);
		activity.startActivity(i);
	}
	
	protected void AnswerLike(HashMap<String, String> tempdata, final View v) {
		progressDialog.start();
		 List<NameValuePair> wp_QueryQuestions=new ArrayList<NameValuePair>();
			wp_QueryQuestions.add(new BasicNameValuePair("AnswerID", tempdata.get("ID")));
			wp_QueryQuestions.add(new BasicNameValuePair("Status", "1"));
			wp_QueryQuestions.add(new BasicNameValuePair("function", "LikeorDisLikeAnswersfromAsker"));
			WebTask mwebTask=new WebTask(wp_QueryQuestions, JSONParser.LIKEMIND_URL);
			mwebTask.setWebTaskListener(new WebTaskListener(){

				@Override
				public void onCompleted(String data) {
					processresult(data,v);
					
				}});
			mwebTask.execute();
			
		
	}
	protected void processresult(String data2, View v) {
		try {
			JSONObject job = new JSONObject(data2);
			int success = job.getInt("success");
			if (success == 1) {
			v.setBackgroundResource(R.drawable.button_liked);
			v.setClickable(false);
			} else {
			
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		progressDialog.end();
	}


}
