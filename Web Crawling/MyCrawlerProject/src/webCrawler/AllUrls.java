package webCrawler;
import java.io.IOException;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;


public class AllUrls {

	public void writeAllUrls(Page page,WebURL URL)
	{
		String url = URL.getURL();
		
		String indicator = new String();

		if(url.startsWith("https://www.washingtonpost.com/"))
		{
			indicator = "OK";
			MyCrawler.news_Wesite.add(url);
		}
		else
		{
			indicator = "N_OK";
			MyCrawler.news_Outside.add(url);
		}

		String[] values = {url, indicator};
		
		Controller.allCSV.writeNext(values);
	}
	
	public void closeFile()
	{
		try 
		{
			Controller.allCSV.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
