package adapters;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liken.R;

public class TopicsAdapter extends BaseAdapter {
	LayoutInflater inflater;
	HashMap<Integer, Integer> listTopicSelected;

	public TopicsAdapter(Activity a, HashMap<Integer, Integer> listTopicSelected) {
		this.listTopicSelected = listTopicSelected;
		inflater = (LayoutInflater) a
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int numberOfColumns = 3; // number of columns in the grid view
		int padding = 2; // padding space in the grid view
		int columnWidth = (parent.getWidth() - (numberOfColumns + 1) * padding)
				/ numberOfColumns;

		View vi = convertView;
		if (convertView == null) {
			vi = inflater.inflate(R.layout.gridview_topics, null);
		}

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				columnWidth, columnWidth);
		
		LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
				columnWidth, 0);
		layoutParams2.gravity=Gravity.CENTER;
		layoutParams2.weight=(float) 0.8;
		LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
				columnWidth, 0);
		layoutParams3.gravity=Gravity.CENTER;
		layoutParams3.weight=(float) 0.2;
		ImageView image = (ImageView) vi.findViewById(R.id.imageView1);
		TextView tv_topic = (TextView) vi.findViewById(R.id.tv_topic);
		LinearLayout llayout = (LinearLayout) vi.findViewById(R.id.llayout);
		llayout.setLayoutParams(layoutParams);
		image.setLayoutParams(layoutParams2);
		tv_topic.setLayoutParams(layoutParams3);
		// image.setLayoutParams(layoutParams);
		if (position == 0) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.design);
			} else {
				image.setImageResource(R.drawable.design_white);
			}
			tv_topic.setText("Design");
		} else if (position == 1) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.culture);
			} else {
				image.setImageResource(R.drawable.culture_white);
			}
			tv_topic.setText("Culture");
		} else if (position == 2) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.movies);
			} else {
				image.setImageResource(R.drawable.movies_white);
			}
			tv_topic.setText("Movies");
		} else if (position == 3) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.music);
			} else {
				image.setImageResource(R.drawable.music_white);
			}
			tv_topic.setText("Music");
		} else if (position == 4) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.tech);
			} else {
				image.setImageResource(R.drawable.tech_white);
			}
			tv_topic.setText("Tech");
		} else if (position == 5) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.random);
			} else {
				image.setImageResource(R.drawable.random_white);
			}
			tv_topic.setText("Random");
		} else if (position == 6) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.philosophy);
			} else {
				image.setImageResource(R.drawable.philosophy_white);
			}
			tv_topic.setText("Philosophy");
		} else if (position == 7) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.sports);
			} else {
				image.setImageResource(R.drawable.sports_white);
			}
			tv_topic.setText("Sports");
		} else if (position == 8) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.business);
			} else {
				image.setImageResource(R.drawable.business_white);
			}
			tv_topic.setText("Business");
		} else if (position == 9) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.games);
			} else {
				image.setImageResource(R.drawable.games_white);
			}
			tv_topic.setText("Games");
		} else if (position == 10) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.science);
			} else {
				image.setImageResource(R.drawable.science_white);
			}

			tv_topic.setText("Science");
		} else if (position == 11) {
			if (listTopicSelected.get(position) == 1) {
				image.setImageResource(R.drawable.literature);
			} else {
				image.setImageResource(R.drawable.literature_white);
				tv_topic.setText("Literature");
			}
		}

		return vi;
	}

	@Override
	public int getCount() {
		return 12;
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
