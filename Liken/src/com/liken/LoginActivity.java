package com.liken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liken.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.analytics.tracking.android.EasyTracker;
import com.liken.data.SQLiteHelper;
import com.liken.webservice.JSONParser;

public class LoginActivity extends Activity {
	EditText etEmail, etPassword;
	ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	private UiLifecycleHelper uiHelper;
	String Email;
	String facebookId="";
	SQLiteHelper SQLiteHelper=new SQLiteHelper(this);
	String Username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		pDialog = new ProgressDialog(LoginActivity.this);
		uiHelper.onCreate(savedInstanceState);
		if(SQLiteHelper.getProfile().get("username")!=null){
		//	Intent i = new Intent(this, TopicsActivity.class);
			Intent i=new Intent(this,MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
					);
			startActivity(i);	
		}else{
			
		setContentView(R.layout.activity_login);
		Button SignUp = (Button) findViewById(R.id.bSignUp);
		TextView bLogin = (TextView) findViewById(R.id.bLogin);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etPassword = (EditText) findViewById(R.id.etPassword);
		LoginButton authButton = (LoginButton) findViewById(R.id.fblogin_button);
		authButton.setReadPermissions(Arrays.asList("email"));
		SignUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginActivity.this,
						RegisterActivity.class);
				startActivity(i);
			}
		});
		bLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loginClicked();
			}
		});
		}
	}

	protected void loginClicked() {
		String password = etPassword.getText().toString();
		 Email = etEmail.getText().toString();
		if (!isEmptyEditText(password, Email)) {

		} else {
			new WebLoginTask(password, Email).execute();
			System.out.println("Registering");
		}

	}

	@Override
	  public void onStart() {
	    super.onStart();
	  
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	   
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
	class WebLoginTask extends AsyncTask<String, String, String> {
		String password, email;

		public WebLoginTask(String password, String email) {
			this.password = password;
			this.email = email;
		}

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Please wait..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {

			String result;
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("Password", password));
			params.add(new BasicNameValuePair("Email", email));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(
					JSONParser.USERLOGIN_URL, "POST", params);
			result = json.toString();
			// check log cat fro response
			Log.d("Login", result);

			return result;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String result) {
			pDialog.dismiss();
			processresult(result);
		}

	}
	
	class facebookLoginTask extends AsyncTask<String, String, String> {
		String function,username,email,facebookId,facebookToken;

		public facebookLoginTask(String function, String username,String email,String facebookId,String facebookToken) {
			this.function=function;
			this.username=username;
			this.email=email;
			this.facebookId=facebookId;
			this.facebookToken=facebookToken;
		}

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}

		/**
		 * Creating product
		 * */
		protected String doInBackground(String... args) {

			String result;
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("Function", function));
			params.add(new BasicNameValuePair("Username", username));
			params.add(new BasicNameValuePair("Email", email));
			params.add(new BasicNameValuePair("FacebookId", facebookId));
			params.add(new BasicNameValuePair("facebookToken", facebookToken));
			params.add(new BasicNameValuePair("ProfilePicPath", ""));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(
					JSONParser.FACEBOOK_URL, "POST", params);
			result = json.toString();
			// check log cat fro response
			Log.i("FacebookLogin", result);
			
			return result;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String result) {
			//endDialog();
			processresultforFacebook(result);
		}

	}
	private void startDialog(){
	
		pDialog.setMessage("Please wait..");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}
	
	private void endDialog(){	
		
		pDialog.dismiss();
	}
	private void processresult(String result) {
		try {
			JSONObject job = new JSONObject(result);

			int success = job.getInt("success");

			if (success == 1) {
				String strUser = job.getString("user");
				JSONArray userArray = new JSONArray(strUser);
				JSONObject user = userArray.getJSONObject(0);
				String id = user.getString("id");
				String ProfilePicPath = user.getString("ProfilePicPath");
				String Username = user.getString("Username");

				HashMap<String, String> userData = new HashMap<String, String>();
				userData.put("id", id);
				userData.put("ProfilePicPath", ProfilePicPath);
				userData.put("username", Username);
				userData.put("facebookid", facebookId);
				userData.put("email", Email);
				Intent i = new Intent(this, SplashActivity.class);
				i.putExtra("userData", userData);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
						);
				startActivity(i);
			} else {
				Toast.makeText(this, "Invalid Email or Password", 1).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	private void processresultforFacebook(String result) {
		try {
			JSONObject job = new JSONObject(result);

			int success = job.getInt("success");

			if (success == 1) {
				String strUser = job.getString("user");
				JSONArray userArray = new JSONArray(strUser);
				JSONObject user = userArray.getJSONObject(0);
				String id = user.getString("id");
				String ProfilePicPath = user.getString("ProfilePicPath");
				

				HashMap<String, String> userData = new HashMap<String, String>();
				userData.put("id", id);
				userData.put("ProfilePicPath", ProfilePicPath);
				userData.put("username", Username);
				userData.put("facebookid", facebookId);
				userData.put("email", Email);
				Intent i = new Intent(this, SplashActivity.class);
				i.putExtra("userData", userData);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK
						);
				startActivity(i);
			} else {
				Toast.makeText(this, "Invalid Email or Password", 1).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private boolean isEmptyEditText(String password, String Email) {
		if (password.trim().length() == 0 || Email.trim().length() == 0) {
			Toast.makeText(this, "Please fill in the details completely", 1)
					.show();
			return false;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);

	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			startDialog();
			final String accesstoken = session.getAccessToken();
			// Request user data and show the results
			Request.executeMeRequestAsync(session,
					new Request.GraphUserCallback() {

						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							// TODO Auto-generated method stub
							if (user != null) {

							 facebookId = user.getId();
								Email= (String) user
										.getProperty("email");
								String firstname = user.getFirstName();
								String lastname = user.getLastName();
								Username= firstname+" "+lastname;
								new facebookLoginTask("Login", Username,Email,facebookId,accesstoken).execute();

							} else {

							}

						}
					});
		} else if (state.isClosed()) {

		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	protected void onResume() {
		super.onResume();
		uiHelper.onResume();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();

	}

}
