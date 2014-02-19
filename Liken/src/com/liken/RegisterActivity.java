package com.liken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

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
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.liken.R;
import com.liken.webservice.JSONParser;

public class RegisterActivity extends Activity {
	EditText etUsername,etEmail,etPassword,etPasswordReenter;
	ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	String Username,Email,password;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		etUsername=(EditText)findViewById(R.id.etUsername);
		etEmail=(EditText)findViewById(R.id.etEmail);
		etPassword=(EditText)findViewById(R.id.etPassword);
		etPasswordReenter=(EditText)findViewById(R.id.etPasswordReenter);
		Button bRegister= (Button) findViewById(R.id.bRegister);
		bRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Registerclicked();
			}
		});
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

	protected void Registerclicked() {
		 password=etPassword.getText().toString();
		String reenter_password=etPasswordReenter.getText().toString();
		 Email=etEmail.getText().toString();
		 Username=etUsername.getText().toString();
		if(!isEmptyEditText() ||!isValidEmail(Email)||!isValidPasswordFormat(password)||!password.equals(reenter_password)){
			
		}else{
		new	WebRegisterTask().execute();
			System.out.println("Registering");
		}
		
	}
	private boolean isEmptyEditText() {
		String password=etPassword.getText().toString();
		String reenter_password=etPasswordReenter.getText().toString();
		String Email=etEmail.getText().toString();
		String Username=etUsername.getText().toString();
		
		if(password.trim().length()==0||reenter_password.trim().length()==0||Email.trim().length()==0||Username.trim().length()==0){
			Toast.makeText(this, "Please fill in the details completely", 1).show();
			return false;
		}
		return true;
	}
	public void checkEmailFormat(String email){
	
	}
	private final static boolean isValidEmail(CharSequence target) {
	    if (target == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}
	private boolean isValidPasswordFormat(String password) {
		
		//check password length
		if (password.length() < 8) {
			Toast.makeText(this, "Password length should be at least 8 characters", Toast.LENGTH_SHORT).show();
			return false;
		} else {
			//check if password only contain digits
			if (Pattern.matches("\\d+", password)) {
				Toast.makeText(this, "Password should contain at least 1 letter", Toast.LENGTH_SHORT).show();
				return false;
			} else {
				//check whether it matches characters in [a-zA-Z_0-9]
	 			if (!Pattern.matches("\\w+", password)) {
	 				Toast.makeText(this, "Password should contain only characters in [a-zA-Z_0-9]", Toast.LENGTH_SHORT).show();
	 				return false;
				}
			}
		}
		return true;
	}

	 class WebRegisterTask extends AsyncTask<String, String, String> {
		 
	        /**
	         * Before starting background thread Show Progress Dialog
	         * */
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(RegisterActivity.this);
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
	            params.add(new BasicNameValuePair("Username", Username));
	            params.add(new BasicNameValuePair("Password", password));
	            params.add(new BasicNameValuePair("Email",Email ));
	            params.add(new BasicNameValuePair("FacebookId", "0"));
	            params.add(new BasicNameValuePair("ProfilePicPath", ""));
	           
	            // getting JSON Object
	            // Note that create product url accepts POST method
	            JSONObject json = jsonParser.makeHttpRequest(JSONParser.NEW_USER_URL,
	                    "POST", params);
	           result= json.toString();
	            // check log cat fro response
	            Log.d("Register", result);
	 
	          
	 
	            return json.toString();
	        }
	 
	        /**
	         * After completing background task Dismiss the progress dialog
	         * **/
	        protected void onPostExecute(String result) {
	            pDialog.dismiss();
	            processresult(result);
	        }
	 
	    }

	public void processresult(String result) {
		try {
			JSONObject job = new JSONObject(result);
			
			int success = job.getInt("success");
			String message = job.getString("message");
			
			
			if(success==1){
				String strUser=job.getString("user");
				JSONArray userArray=new JSONArray(strUser);
				JSONObject user=userArray.getJSONObject(0);
				String id=user.getString("id");
				HashMap<String,String> userData=new HashMap<String,String>();
				userData.put("id", id);
				userData.put("ProfilePicPath", "");
				userData.put("username", etUsername.getText().toString());
				userData.put("email", etEmail.getText().toString());
				Intent i=new Intent(this,SplashActivity.class);
				i.putExtra("userData", userData);
				i.putExtra("activity", "register");
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK ); 
				startActivity(i);
			}else{
				Toast.makeText(this, message, 1).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
