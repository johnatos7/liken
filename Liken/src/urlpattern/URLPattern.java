package urlpattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//Type 0 Normal Link
//Type 1 Youtube
public class URLPattern {
	String url;
	
	
	public int URLType(String URL){
		this.url=URL;
		int Type=0;
		boolean isYouTube=YouTubeLink();
		if(isYouTube){
			Type=1;			
		}
	return Type;		
	}
	
	private boolean YouTubeLink(){		 
			String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
			 CharSequence input = url;
			 Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
			 Matcher matcher = pattern.matcher(input);
			 if (matcher.matches())
			 {
				 return true;			 
			 }	
		return false;

	}

}
