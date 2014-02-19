package com.liken.webservice;


 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
 
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.util.Log;
 
public class JSONParser {
	public static final String NEW_USER_URL="http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/new_user.php";
	public static final String USERLOGIN_URL = "http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/userlogin.php";
	public static final String FACEBOOK_URL = "http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/Facebook.php";
	public static final String INSERT_COMMUNITY_URL = "http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/Insert_Community.php";
	public static final String GET_COMMUNITY_URL = "http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/Get_Community.php";
	public static final String NEW_QUESTION_URL = "http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/New_Question.php";
	public static final String QUERY_QUESTION_URL = "http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/Query_Question.php";
	public static final String ANSWER_URL = "http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/Answer.php";
	public static final String ANSWER_NEW_URL = "http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/answer_new.php";
	public static final String PROFILE_URL = "http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/profile.php";
	public static final String LIKEMIND_URL = "http://ec2-54-214-167-236.us-west-2.compute.amazonaws.com/likemind.php";
	static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
 
    // constructor
    public JSONParser() {
 
    }
 
    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,
            List<NameValuePair> params) {
 
        // Making HTTP request
        try {
 
            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));
 
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
 
            }else if(method == "GET"){
                // request method is GET
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);
 
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }           
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            
           
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try {
            
        	System.out.println(json);
        	jObj = new JSONObject(json);
        	
          // jObj = new JSONObject(json.substring(3));
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        return jObj;
 
    }
}