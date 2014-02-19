package WebContent;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import WebContent.JsoupTask.JsoupTaskListener;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

public class SimpleYouTubeHelper extends
		AsyncTask<String, String, HashMap<String, String>> {
	String URL;
	YouTubeHelperListener youtubeTaskListener;
	static String video_id;

	public SimpleYouTubeHelper(String URL) {
		this.URL = URL;
		video_id = getVideoID(URL);
	}

	public static String getImageUrlQuietly(String youtubeUrl) {
		try {
			if (youtubeUrl != null) {
				return String.format("http://img.youtube.com/vi/%s/0.jpg", Uri
						.parse(youtubeUrl).getQueryParameter("v"));
			}
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getTitleQuietly(String youtubeUrl) throws IOException {
		try {
			if (youtubeUrl != null) {
				URL embededURL = new URL(
						"http://gdata.youtube.com/feeds/api/videos/" + video_id
								+ "?v=2&alt=jsonc");
				String data = new JSONObject(IOUtils.toString(embededURL))
						.getString("data");
				String title = new JSONObject(data).getString("title");
				return title;

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getVideoID(String url) {
		String id = "";
		String expression = "^.*((youtu.be"
				+ "\\/)"
				+ "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var
																							// regExp
																							// =
																							// /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(url);
		if (matcher.matches()) {
			String groupIndex1 = matcher.group(7);
			if (groupIndex1 != null && groupIndex1.length() == 11) {
				id = groupIndex1;
			}
		}
		return id;

	}

	public static String getDescriptionQuietly(String youtubeUrl)
			throws IOException {
		try {
			if (youtubeUrl != null) {

				URL embededURL = new URL(
						"http://gdata.youtube.com/feeds/api/videos/" + video_id
								+ "?v=2&alt=jsonc");
				System.out.println(IOUtils.toString(embededURL));
				String data = new JSONObject(IOUtils.toString(embededURL))
						.getString("data");
				String description = new JSONObject(data)
						.getString("description");
				return description;

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected HashMap<String, String> doInBackground(String... arg0) {
		HashMap<String, String> webData = new HashMap<String, String>();
		try {
			webData.put("Title", getTitleQuietly(URL));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			webData.put("Content", getDescriptionQuietly(URL));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webData.put("img_url", getImageUrlQuietly(URL));
		return webData;
	}

	@Override
	protected void onPostExecute(HashMap<String, String> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		youtubeTaskListener.onCompleted(result);
	}

	public static interface YouTubeHelperListener {
		void onCompleted(HashMap<String, String> result);
	}

	public void setYouTubeHelperListener(YouTubeHelperListener youtubeTaskListener) {
		this.youtubeTaskListener = youtubeTaskListener;
	}

}