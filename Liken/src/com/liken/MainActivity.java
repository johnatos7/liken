package com.liken;

import java.util.ArrayList;

import adapters.LeftDrawerAdapter;
import adapters.RightDrawerAdapter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import application.GlobalState;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.liken.R;
import com.liken.constant.Constants;
import com.liken.data.SQLiteHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends SherlockFragmentActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mLeftDrawerList,mRightDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	LeftDrawerAdapter ldAdapter;
	RightDrawerAdapter rdAdapter;
	Fragment newFragment;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mFragmentTitles;
	SQLiteHelper sqlitehelper=new SQLiteHelper(this);
	ArrayList<String> listTopics;
	FragmentManager fm;
	OnTopicChangedListener onTopicChangedListener;
	Constants constants=new Constants();
	GlobalState application;
	 protected ImageLoader imageLoader = ImageLoader.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		application=((GlobalState) this.getApplication());
		fm = getSupportFragmentManager();
		mTitle = mDrawerTitle = getTitle();
		mFragmentTitles = getResources().getStringArray(R.array.fragments);
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer);
		mLeftDrawerList = (ListView)findViewById(R.id.left_drawer_list);
		mRightDrawerList=(ListView) findViewById(R.id.right_drawer_list);
		ldAdapter=new LeftDrawerAdapter(this);
		listTopics=sqlitehelper.getTopics();
		rdAdapter=new RightDrawerAdapter(this,listTopics);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mLeftDrawerList.setAdapter(ldAdapter);
		//mLeftDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mFragmentTitles));
		mLeftDrawerList.setOnItemClickListener(new LeftDrawerItemClickListener());
		
		mRightDrawerList.setAdapter(rdAdapter);
		mRightDrawerList.setOnItemClickListener(new RightDrawerItemClickListener());
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, 
				mDrawerLayout, 
				R.drawable.ic_drawer, 
				R.string.drawer_open, 
				R.string.drawer_close){
			public void onDrawerClosed(View v){
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}
			public void onDrawerOpened(View v){
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null){
			selectItemforLeft(0);
		}
		
	}
	@Override
	  public void onStart() {
	    super.onStart();
	  
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		imageLoader.clearDiscCache();
		imageLoader.clearMemoryCache();
		 EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getSupportMenuInflater().inflate(R.menu.main, menu);
		
		 menu.add("Followed")
         .setIcon(R.drawable.right_nav_drawer_icon)
         .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mLeftDrawerList)){
				mDrawerLayout.closeDrawer(mLeftDrawerList);
			} else {
				mDrawerLayout.openDrawer(mLeftDrawerList);
			}
			return true;
		case R.id.action_settings:
			//Intent i = new Intent(MainActivity.this, Sources.class);
			//startActivity(i);
			return true;
		/*default:
			return super.onOptionsItemSelected(item);*/
		}
		if (item.getTitle().toString().equalsIgnoreCase("Followed")){
			if (mDrawerLayout.isDrawerOpen(mRightDrawerList)){
				mDrawerLayout.closeDrawer(mRightDrawerList);
			} else {
				mDrawerLayout.openDrawer(mRightDrawerList);
			}
		}
		return true;
	}
	
	private class LeftDrawerItemClickListener implements ListView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id){
			selectItemforLeft(position);
		}
	}
	private class RightDrawerItemClickListener implements ListView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id){
			selectItemforRight(position);
		}
	}
	
	private void selectItemforLeft(int position){
	newFragment = new MainFragment();
//	((GlobalState) this.getApplicationContext()).clearAllImageViewDisplay();
		switch(position){
		case 0:
			newFragment = new MainFragment();
			application.setTopic("Trending");
			break;
		case 1:
			newFragment = new LikenFragment();
			break;
		case 2:
			newFragment = new QuestionsFragment();
			break;
		case 3:
			newFragment = new AnswersFragment();
			break;
		case 4:
			newFragment = new ProfileFragment();
			break;
		case 5:
			newFragment = new SettingsFragment();
			break;
		
		}
		
		
		fm.beginTransaction()
		.replace(R.id.content_frame, newFragment)
		.commit();
		
		mLeftDrawerList.setItemChecked(position, true);
		setTitle(mFragmentTitles[position]);
		mDrawerLayout.closeDrawer(mLeftDrawerList);
	}
	public interface OnTopicChangedListener{
		void onTopicChanged(String topic);

	}
	
	public void SetOnTopicChangedListener(OnTopicChangedListener onTopicChangedListener){
		this.onTopicChangedListener= onTopicChangedListener;
		
		
	}
	private void selectItemforRight(int position){	
		mRightDrawerList.setItemChecked(position, true);
		setTitle(constants.getTopicName(Integer.parseInt(listTopics.get(position))));
		mDrawerLayout.closeDrawer(mRightDrawerList);
		application.setTopic(listTopics.get(position));
		if(mTitle.equals("Trending")){
	//		((GlobalState)this.getApplicationContext()).clearAllImageViewDisplay();
		onTopicChangedListener.onTopicChanged(listTopics.get(position));
		}else{
		fm.beginTransaction()
		.replace(R.id.content_frame,new MainFragment())
	//	.addToBackStack(null)
		.commit();
		}
	}
	
	@Override
	public void setTitle(CharSequence title){
		mTitle = title;
		getSupportActionBar().setTitle(title);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}
