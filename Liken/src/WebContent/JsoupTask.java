package WebContent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.util.Log;

public class JsoupTask extends AsyncTask<String, String, HashMap<String,String>> {
	String URL;	
JsoupTaskListener jsoupTaskListener;
	public JsoupTask(String URL){
		this.URL=URL;	
	}

	@Override
	protected HashMap<String,String> doInBackground(String... arg0) {
		HashMap<String,String> webData= new HashMap<String,String>();
	        Document document=null;

	        try {
	        	//check whether the input is valid url format
	          URL url = new URL(URL);
	          try {
					document = Jsoup.connect(URL).get();
					
					  String title = document.title();
					  System.out.println("Title: " + title);
					
					  Element content = document.getElementById("content");
					  if(content!=null){
					  System.out.println(content.text());
					  webData.put("Content", content.text());
					  }		        
				      String img_url=grabImages(document,title);	
				      webData.put("Title", title);
				      webData.put("img_url", img_url);    
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
	        } catch (MalformedURLException e) {
	        	//invalid format
	        }
			
		return webData;
	}
	
	@Override
	protected void onPostExecute(HashMap<String,String> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		jsoupTaskListener.onCompleted(result);
	}

	private String grabImages(Document document, String title) {
		int largestPicSize=0;
		String largest_img_link="";
		Elements images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
		for (Element image : images) {
			String alt=image.attr("alt");
			boolean Match=StringMatch(alt,title);
			if(Match){
		
				Log.i("match",title+" \n"+alt);
				return image.attr("src");
			}else{
				String height=image.attr("height");
				String width=image.attr("width");
				int picSize= sizeOfPic(height,width);
				if(largestPicSize<picSize){
					largestPicSize=picSize;
					largest_img_link=image.attr("src");
				}
			}
		}
		
		return processImgLink(largest_img_link);
		
	}

	private String processImgLink(String largest_img_link) {
		String expression = "http"; 
		 Pattern pattern = Pattern.compile(expression);
		 CharSequence img_link=largest_img_link;
		 Matcher matcher = pattern.matcher(img_link);
		 if (matcher.find())
		 {	
			 return largest_img_link;
		 }
		
		 largest_img_link="http:"+largest_img_link;
		 
		 return largest_img_link;
	}

	

	private int sizeOfPic(String height, String width) {
		if(height.trim().length()>0){
		int Height=Integer.parseInt(height);
		int Width=Integer.parseInt(width);
		int PicSize=Height*Width;
		return PicSize;
		}
		return 0;
	}

	private boolean StringMatch(String alt, String title) {
		double count = 0;
		  String[] words = title.split(" ");
		  for(String word : words) {
		    if(alt.indexOf(word) != -1) {
		      count++;
		    }
		  }
		  if(count>1){
			  
			  return true;
		  }
		  return false;	
	}
public static interface JsoupTaskListener {
        void onCompleted(HashMap<String, String> result);    
    }
 public void setJsoupTaskListener(JsoupTaskListener jsoupTaskListener) {
        this.jsoupTaskListener = jsoupTaskListener;
    }
}
