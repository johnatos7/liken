package adapters;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import application.GlobalState;

import com.liken.AnswerActivity;
import com.liken.ProfileOtherActivity;
import com.liken.R;
import com.liken.constant.Constants;
import com.liken.contentinflater.QuestionInflater;

public class MainAdapter extends PagerAdapter {
	private LayoutInflater inflater;
	ArrayList<HashMap<String,String>> data;
	ViewPager mPager;
	Activity activity;
	String userID;
	 DisplayMetrics displayMetrics;

	ArrayList<QuestionInflater> listMediaView=new ArrayList<QuestionInflater>();
	public MainAdapter(Activity activity,ArrayList<HashMap<String,String>> data,ViewPager mPager2, String userID){
		inflater = activity.getLayoutInflater();
		this.data=data;
		this.mPager=mPager2;
		this.activity=activity;
		this.userID=userID;
	//	((GlobalState) getApplicationContext()).displayMetrics;
		 displayMetrics=((GlobalState)activity.getApplication()).displayMetrics;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

/*	@Override
	public boolean isViewFromObject(View view, Object obj) {
		if (view instanceof OutlineContainer) {
			return ((OutlineContainer) view).getChildAt(0) == obj;
		} else {
			return view == obj;
		}
	}*/
	
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		
		View Layout= inflater.inflate(R.layout.activity_feed,container, false);
		LinearLayout ll_MediaView= (LinearLayout) Layout.findViewById(R.id.ll_questions);
		final TextView tv_Question=(TextView) Layout.findViewById(R.id.tv_question);
		final TextView tv_QuestionExtended=(TextView) Layout.findViewById(R.id.tv_questionExtended);
		TextView tv_AnswerCount=(TextView) Layout.findViewById(R.id.tv_numberAnswer);
		TextView tv_Topic=(TextView) Layout.findViewById(R.id.tv_topic);
		TextView tv_AskerName=(TextView) Layout.findViewById(R.id.tv_askerName);
		Button b_Answer=(Button) Layout.findViewById(R.id.b_answer);
		
		adjustView(ll_MediaView,b_Answer,tv_Question);
	
		HashMap<String,String> tempData=data.get(position);
		tv_AskerName.setTag(tempData.get("User_ID"));
		tv_AskerName.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				goToProfile((String) v.getTag());
				
			}

		});
		String Answered=tempData.get("Answered");
		if(Answered.equals("1") || tempData.get("User_ID").equals(userID)){
			b_Answer.setText("Answered");
			b_Answer.setTextColor(Color.parseColor("#929292"));
		}else{
		b_Answer.setTag(tempData);
		b_Answer.setText("Answer");
		b_Answer.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent i=new Intent(activity,AnswerActivity.class);
				i.putExtra("data", (HashMap<String,String>) v.getTag());
				activity.startActivity(i);
				
			}});
		}
		tv_QuestionExtended.setText(tempData.get("Questions"));
		tv_QuestionExtended.setTextColor(new Constants().getTopicColor(Integer.parseInt(tempData.get("Category"))));
		QuestionInflater mquestionInflater=new QuestionInflater(container,ll_MediaView,tv_Question,tv_AnswerCount,tv_Topic,tv_AskerName,tempData,inflater,activity);
		mquestionInflater.inflate();
		
		tv_Question.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				tv_QuestionExtended.setVisibility(View.VISIBLE);
				tv_Question.setVisibility(View.INVISIBLE);
			
				
			}});
		tv_QuestionExtended.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
			
					tv_QuestionExtended.setVisibility(View.GONE);
					tv_Question.setVisibility(View.VISIBLE);
				
				
			}});
		container.addView(Layout, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		// mPager.setObjectForPosition(Layout, position);
		
		return Layout;
	}

	private void adjustView(LinearLayout ll_MediaView, Button b_Answer, TextView tv_Question) {
		int height=displayMetrics.heightPixels;
		float density= displayMetrics.density;
	int	MediaViewHeight=(int) (height-100*density-120*density-45*density-60*density-10*density);
	RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, MediaViewHeight);
	lp.addRule(RelativeLayout.BELOW, R.id.layout_top);
	ll_MediaView.setLayoutParams(lp);
	
}

	@Override
	public void destroyItem(ViewGroup container, int position, Object obj) {
		 ((ViewPager) container).removeView((View) obj);
		/* if (slide_image!= null) {
	            slide_image.recycle();
	            System.gc();
	        }*/
	}

	protected void goToProfile(String tag) {
		Intent i=new Intent(activity,ProfileOtherActivity.class);
		i.putExtra("data", tag);
		activity.startActivity(i);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}
}
