package adapters;

import java.util.ArrayList;
import java.util.HashMap;

import com.liken.R;
import com.liken.AnswersReceivedActivity;
import com.liken.AskerAnswerActivity;
import com.liken.ProfileOtherActivity;
import com.liken.contentinflater.UserAnswersPageInflater;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import application.GlobalState;

public class LikenAdapter extends BaseAdapter {
	private static final int SWITCHTYPE = 0;
	LayoutInflater inflater;
	ArrayList<HashMap<String, String>> data;
	private Activity activity;
	HashMap<String,String>tempdata;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	ListView mlvExp;
	public LikenAdapter(ListView mlvExp, FragmentActivity activity,
			ArrayList<HashMap<String, String>> data) {
		this.data=data;
		this.mlvExp=mlvExp;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.activity=activity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
	/*	hmData.put("Question_ID", object.getString("Qurstion_ID"));
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
		hmData.put("answererid", object.getString("answererid"));*/
		View vi = convertView;
		TextView tvNotification,tvQuestion=null;
		ImageView ivPerson=null;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.listview_liken, null);
		}
		tvNotification=(TextView) vi.findViewById(R.id.tv_notification);
		tvQuestion=(TextView) vi.findViewById(R.id.tv_Question);
		ivPerson=(ImageView) vi.findViewById(R.id.iv_person);
		tempdata=data.get(arg0);
		String picpath=null;
		String name=null;
		if (tempdata.get("people").equals("asker")){
		picpath=tempdata.get("askerpic");
		name=tempdata.get("askername");
		ivPerson.setTag(tempdata.get("askerid"));
		tvNotification.setTag(tempdata.get("askerid"));
		}else{
			picpath=tempdata.get("answererpic");
			name=tempdata.get("answerername");
			ivPerson.setTag(tempdata.get("answererid"));
			tvNotification.setTag(tempdata.get("answererid"));
		}
	
		ivPerson.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				goToProfile((String) v.getTag());
				
			}

		});
		
		tvNotification.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				goToProfile((String) v.getTag());
				
			}

		});
		imageLoader
		.displayImage(
				picpath,
				ivPerson,
				((GlobalState) activity.getApplicationContext()).options_profile_img,
				((GlobalState)activity.getApplicationContext()).animateFirstListener);
		
		tvNotification.setText(StringFormat(name));
		tvQuestion.setText(tempdata.get("Questions"));
		return vi;
	}

	protected void goToProfile(String tag) {
		Intent i=new Intent(activity,ProfileOtherActivity.class);
		i.putExtra("data", tag);
		activity.startActivity(i);
	}
	
	protected Spannable StringFormat(String text){
		String Name= text;
		String notification=  " is likeminded on question";
		
		String finalString= Name+notification;
		Spannable sb = new SpannableString( finalString );
	//	sb.setSpan(new ColorSpan(), finalString.indexOf(text), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //bold
		 sb.setSpan(new ForegroundColorSpan(Color.parseColor("#01e6e9")),  finalString.indexOf(Name), Name.length(), 0);
		
		
		
		
		return sb;

	}

}
