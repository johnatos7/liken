package adapters;

import java.util.ArrayList;
import java.util.HashMap;

import com.liken.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter {
	LayoutInflater inflater;
	ArrayList<String> listTopics;

	public SpinnerAdapter(Activity a, ArrayList<String> listTopicSelected) {
		this.listTopics = listTopicSelected;
		inflater = (LayoutInflater) a
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.listview_spinner, null);
		}
		
		TextView tv_topic = (TextView) vi.findViewById(R.id.text1);
		
	String topic=listTopics.get(position);
	int topicToInt=Integer.parseInt(topic);
	switch(topicToInt){
	case 0:
		tv_topic.setText("Design");
		break;
	case 1:
		tv_topic.setText("Culture");
		break;
	case 2:
		tv_topic.setText("Movies");
		break;
	case 3:
		tv_topic.setText("Music");
		break;
	case 4:
		tv_topic.setText("Tech");
		break;
	case 5:
		tv_topic.setText("Random");
		break;
	case 6:
		tv_topic.setText("Philosophy");
		break;
	case 7:
		tv_topic.setText("Sports");
		break;
	case 8:
		tv_topic.setText("Business");
		break;
	case 9:
		tv_topic.setText("Games");
		break;
	case 10:
		tv_topic.setText("Science");
		break;
	case 11:
		tv_topic.setText("Literature");
		break;
	
		
		
		
		
		
	}


		return vi;
	}

	@Override
	public int getCount() {
		return listTopics.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
