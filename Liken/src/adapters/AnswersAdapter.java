package adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liken.R;
import com.liken.AnswersReceivedActivity;
import com.liken.AskerAnswerActivity;
import com.liken.ProfileOtherActivity;
import com.liken.contentinflater.QuestionInflaterforAnswersPage;
import com.liken.contentinflater.UserAnswersPageInflater;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

public class AnswersAdapter extends BaseAdapter {
	private static final int SWITCHTYPE = 0;
	LayoutInflater inflater;
	ArrayList<HashMap<String, String>> data;
	private Activity activity;
	HashMap<String,String>tempdata;
	ActionSlideExpandableListView mlvExp;
	LinearLayout ll_Questions = null;
	public AnswersAdapter(ActionSlideExpandableListView mlvExp, Activity activity,ArrayList<HashMap<String,String>> data) {
		this.data=data;
		this.mlvExp=mlvExp;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.activity=activity;
		
	}
	
	private void updateView(int index){
		
	    View v = mlvExp.getChildAt(index - 
	     mlvExp.getFirstVisiblePosition());
	    TextView tv_Question=(TextView) v.findViewById(R.id.expandable_toggle_button);
		Button b_switchcontext=(Button) v.findViewById(R.id.button_switchcontext);
		TextView tv_Topic=(TextView) v.findViewById(R.id.tv_topic);
		TextView tv_AskerName=(TextView) v.findViewById(R.id.tv_askerName);
		LinearLayout ll_Questions=(LinearLayout) v.findViewById(R.id.ll_questions);
		LinearLayout ll_askername=(LinearLayout) v.findViewById(R.id.ll_askername);
		LinearLayout ll_topic=(LinearLayout) v.findViewById(R.id.ll_topic);
		ll_askername.setVisibility(View.VISIBLE);
		ll_topic.setVisibility(View.VISIBLE);
		
		tempdata=data.get(index);
		tv_AskerName.setTag(tempdata.get("User_ID"));
		tv_AskerName.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				goToProfile((String) v.getTag());
				
			}

		});
		 QuestionInflaterforAnswersPage mquestionInflater=new QuestionInflaterforAnswersPage(ll_Questions,tv_Question,tv_Topic, tv_AskerName, tempdata,inflater,activity);	
			mquestionInflater.inflate();
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
	
	private class contextState {
		int STATE=0;
		void setState(int STATE){
			this.STATE=STATE;
		}
		int getState(){
			return STATE;
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
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
		
	
		View vi = convertView;
		TextView tv_Question = null;
		Button b_switchcontext;
		final Button b_Answers;
		ImageView b_star = null;
	//	LinearLayout ll_Questions = null;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.listview_answers, null);
		}
			tv_Question=(TextView) vi.findViewById(R.id.expandable_toggle_button);
			b_Answers=(Button) vi.findViewById(R.id.b_answers);
			b_star=(ImageView) vi.findViewById(R.id.b_star);
			b_switchcontext=(Button) vi.findViewById(R.id.button_switchcontext);
			ll_Questions=(LinearLayout) vi.findViewById(R.id.ll_questions);
			final LinearLayout ll_askername=(LinearLayout) vi.findViewById(R.id.ll_askername);
			final LinearLayout ll_topic=(LinearLayout) vi.findViewById(R.id.ll_topic);
			ll_askername.setVisibility(View.GONE);
			ll_topic.setVisibility(View.GONE);
			
			tempdata=data.get(position);
			 final UserAnswersPageInflater contentInflater=new UserAnswersPageInflater(ll_Questions,tv_Question,b_Answers,tempdata,inflater,activity);
			b_Answers.setTag(tempdata);
			contextState cs=	new contextState();
			b_star.setTag(tempdata);
			b_switchcontext.setTag(cs);
			
			b_switchcontext.setOnClickListener(new OnClickListener(){
				
				@Override
				public void onClick(View v) {
				contextState cs=(contextState) v.getTag();
				if(cs.getState()==1){
					((Button) v).setText("Q");
				
					 contentInflater.inflate();
					 ll_askername.setVisibility(View.GONE);
					ll_topic.setVisibility(View.GONE);
					 cs.setState(0);
					 v.setTag(cs);
				}else{		
					((Button) v).setText("A");
					updateView(position);
					cs.setState(1);
					 v.setTag(cs);
				}
					
				}});
			
		/*	if(tempdata.get("Asker_like").equals("1")){
				b_star.setVisibility(View.VISIBLE);
				b_star.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						Intent i=new Intent(activity,AskerAnswerActivity.class);
						i.putExtra("data", (HashMap<String,String>) v.getTag());
						activity.startActivityForResult(i,111);
					}});
			}*/
			b_Answers.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
				HashMap<String, String> hmdata = (HashMap<String, String>) v.getTag();
				Intent i =new Intent(activity,AnswersReceivedActivity.class);
				i.putExtra("data", hmdata);
				activity.startActivity(i);
				}});
			contentInflater.inflate();	
		return vi;
	}
	
	protected void goToProfile(String tag) {
		Intent i=new Intent(activity,ProfileOtherActivity.class);
		i.putExtra("data", tag);
		activity.startActivity(i);
	}

}
