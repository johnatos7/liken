package adapters;

import java.util.ArrayList;

import com.liken.R;
import com.liken.data.SQLiteHelper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RightDrawerAdapter extends BaseAdapter {
	LayoutInflater inflater;
	SQLiteHelper sqlitehelper;
	ArrayList<String> listTopics;
	public RightDrawerAdapter(Activity a,ArrayList<String> topics){
		inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		sqlitehelper=new SQLiteHelper(a);
		listTopics=topics;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listTopics.size();
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
			vi = inflater.inflate(R.layout.gridview_topics_profile, null);
		}
		ImageView image = (ImageView) vi.findViewById(R.id.imageView1);
		TextView tv_topic = (TextView) vi.findViewById(R.id.tv_topic);
		String topicID=listTopics.get(position);
		int id= Integer.parseInt(topicID);
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
				tv_topic.setText("Literature");
		}
		
		return vi;
	}

}
