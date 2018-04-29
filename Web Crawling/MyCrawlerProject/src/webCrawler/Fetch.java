package webCrawler;
import java.io.IOException;

import edu.uci.ics.crawler4j.url.WebURL;


public class Fetch {
	
	public void writeFetchUrls(WebURL webUrl, int statusCode)
	{
		String url = webUrl.getURL();
		
		String[] values = {url, Integer.toString(statusCode)};
		Controller.fetchCSV.writeNext(values);
	}
	
	public void closeFile()
	{
		try 
		{
			Controller.fetchCSV.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
