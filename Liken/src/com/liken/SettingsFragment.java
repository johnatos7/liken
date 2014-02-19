package com.liken;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.liken.R;
import com.facebook.Session;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.liken.data.SQLiteHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingsFragment extends SherlockFragment {
	SQLiteHelper sqlhelper;
	 protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sqlhelper=new SQLiteHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View vi=inflater.inflate(R.layout.fragment_setting, container,false);
		Button bLogout=(Button) vi.findViewById(R.id.b_logout);
		
		bLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				logOutClicked();
				
			}
		});
		
		return vi;
	}
	
	@Override
	public void onStart() {
	  super.onStart();
	  
	  EasyTracker tracker = EasyTracker.getInstance(getActivity());
	    tracker.set(Fields.SCREEN_NAME, "Settings Fragment");
	    tracker.send(MapBuilder.createAppView().build());
	}

	protected void logOutClicked() {
		Session session = Session.getActiveSession();
		session.closeAndClearTokenInformation();
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
		sqlhelper.deleteAllData();
		Intent i=new Intent(getActivity(),LoginActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
				);
			startActivity(i);
		
		
	}
	
	
	

}
