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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liken.R;
import com.liken.AnswersReceivedActivity;
import com.liken.contentinflater.UserQuestionsPageInflater;

public class TrendingAdapter extends BaseAdapter {
	LayoutInflater inflater;
	ArrayList<HashMap<String, String>> data;
	private Activity activity;
	public TrendingAdapter(Activity activity,ArrayList<HashMap<String,String>> data) {
		this.data=data;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.activity=activity;
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
		TextView tv_Question = null;
		TextView tv_topic = null;
		Button b_Answers = null;
		LinearLayout ll_Questions = null;
		HashMap<String,String>tempdata=data.get(position);
		if (vi == null) {
			vi = inflater.inflate(R.layout.listview_trending,parent,false);
			tv_Question=(TextView) vi.findViewById(R.id.expandable_toggle_button);
			tv_topic=(TextView) vi.findViewById(R.id.tv_topic);
			b_Answers=(Button) vi.findViewById(R.id.b_answers);
			ll_Questions=(LinearLayout) vi.findViewById(R.id.ll_questions);
			 new UserQuestionsPageInflater(ll_Questions,tv_Question,b_Answers,tv_topic,tempdata,inflater,activity).inflate();
				b_Answers.setTag(tempdata);
				b_Answers.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
					HashMap<String, String> hmdata = (HashMap<String, String>) v.getTag();
					Intent i =new Intent(activity,AnswersReceivedActivity.class);
					i.putExtra("data", hmdata);
					activity.startActivity(i);
					}});
		}
		
		
	
		
		return vi;
	}

}
