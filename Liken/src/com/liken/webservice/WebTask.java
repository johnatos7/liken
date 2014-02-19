package com.liken.webservice;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class WebTask extends AsyncTask<String,String,String> {
	JSONParser jsonParser = new JSONParser();
	List<NameValuePair> data;
	String URL;
	WebTaskListener webTaskListener;

	public WebTask(List<NameValuePair> data,String URL){
		this.data=data;	
		this.URL=URL;
	}
	

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
	String result;
		JSONObject json = jsonParser.makeHttpRequest(
				URL, "POST", data);
	if(json==null){
		return "";
	}
		result = json.toString();
		// check log cat fro response
	//	Log.i("result", result);
		return result;
	}
	
	protected boolean isJSONValid(String test)
	{
	    boolean valid = false;
	    try {
	        new JSONObject(test);
	        valid = true;
	    }
	    catch(JSONException ex) { 
	        valid = false;
	    }
	    return valid;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		webTaskListener.onCompleted(result);
		
	}

	 public static interface WebTaskListener {
	        void onCompleted(String data);
	       
	    }
	 public void setWebTaskListener(WebTaskListener webTaskListener) {
	        this.webTaskListener = webTaskListener;
	    }
}
