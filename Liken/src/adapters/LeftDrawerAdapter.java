package adapters;

import com.liken.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LeftDrawerAdapter extends BaseAdapter {
LayoutInflater inflater;
	public LeftDrawerAdapter(Activity a){
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.listview_leftdrawer, null);
		}
		ImageView ivIcon=(ImageView) vi.findViewById(R.id.imageView1);
		TextView tvIcon=(TextView) vi.findViewById(R.id.textView1);
		if(position==0){
			ivIcon.setImageResource(R.drawable.trending_icon);
			tvIcon.setText("Trending");
		}else if(position==1){
			ivIcon.setImageResource(R.drawable.likemind_icon);
			tvIcon.setText("Likenings");
		}else if(position==2){
			ivIcon.setImageResource(R.drawable.questions_icon);
			tvIcon.setText("Questions");
		}else if(position==3){
			ivIcon.setImageResource(R.drawable.answers_icon);
			tvIcon.setText("Answers");
		}else if(position==4){		
			ivIcon.setImageResource(R.drawable.profile);
			tvIcon.setText("Profile");	
			
		}else if(position==5){		
			ivIcon.setImageResource(R.drawable.settings);
			tvIcon.setText("Settings");			
			
		}
		return vi;
	}

}
