package webCrawler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class Visit {
	private final static String pattern = ".*(\\.(html|htm|doc|pdf|doc|docx|png|jpeg|jpg|gif))$";
	private final static Pattern FILTERS = Pattern.compile(pattern);
	private final String storageFolder = "E:\\Study\\Academics\\Spring 2017\\CSCI572\\WebCrawlNikhila\\downloads";
	
	public void writeVisitUrls(Page page) 
	{
		String url = page.getWebURL().getURL();
		String contentType = page.getContentType();
	
		if (contentType.contains("text/html")
				|| contentType.contains("application/msword")
				|| contentType.contains("application/pdf")) 
		{	
			processPage(page, url, contentType);
//			try 
//			{
//				savePage(page);
//			} 
//			catch (IOException e) 
//			{
//				e.printStackTrace();
//			}
		}
		else if (FILTERS.matcher(url).matches()) 
		{
			processPage(page, url, contentType);
//			try 
//			{
//				savePage(page);
//			} 
//			catch (IOException e) 
//			{
//				e.printStackTrace();
//			}
		}
		else
		{
			String[] values = {url, contentType};
			Controller.extraCSV.writeNext(values);
		}
	}

	public static void processPage(Page page, String url, String contentType) 
	{
		if (page.getParseData() instanceof HtmlParseData || page.getParseData() instanceof BinaryParseData) 
		{
			HtmlParseData htmlParseData;
			BinaryParseData binaryParseData;
			byte[] x = page.getContentData();
			
			String arr[] = page.getContentType().split(";");
			
			Set<WebURL> links = null;
			
			if(page.getParseData() instanceof BinaryParseData)
			{
				binaryParseData = (BinaryParseData) page.getParseData();
				links = binaryParseData.getOutgoingUrls();
			}
			else if(page.getParseData() instanceof HtmlParseData)
			{
				htmlParseData = (HtmlParseData) page.getParseData();
				links = htmlParseData.getOutgoingUrls();
			}
			
			//saves the content type in a hash map
			if(!MyCrawler.contentType.containsKey(arr[0]))
				MyCrawler.contentType.put(arr[0], 1);
            else
            	MyCrawler.contentType.put(arr[0], MyCrawler.contentType.get(arr[0])+1);
			
			String[] values = { url, Integer.toString(x.length),Integer.toString(links.size()), contentType };
			Controller.visitCSV.writeNext(values);
			
			//store count corresponding to different File sizes
			storeFileSizes(x);
			
			//store the URLs for pageRank calculation
			String[] valuesPR = new String[links.size()+1];
			valuesPR[0] = url;
			int index =1;
			for(WebURL URL : links)
			{
				valuesPR[index] = URL.getURL();
				index++;
			}
			//Controller.pageRank.writeNext(valuesPR);
		}
		
	}

	public static void storeFileSizes(byte[] x) 
	{
		if(x.length < 1024) 
		{
            if(!MyCrawler.fileSizes.containsKey("<1KB"))
            	MyCrawler.fileSizes.put("<1KB", 1);
            else
            	MyCrawler.fileSizes.put("<1KB", MyCrawler.fileSizes.get("<1KB")+1);
        }
        else if(x.length >= 1024 && x.length < 10240) 
        {
            if(!MyCrawler.fileSizes.containsKey("1KB ~ <10KB"))
            	MyCrawler.fileSizes.put("1KB ~ <10KB", 1);
            else
            	MyCrawler.fileSizes.put("1KB ~ <10KB", MyCrawler.fileSizes.get("1KB ~ <10KB")+1);
        }
        else if(x.length >= 10240 && x.length < 102400) 
        {
            if(!MyCrawler.fileSizes.containsKey("10KB ~ <100KB"))
            	MyCrawler.fileSizes.put("10KB ~ <100KB", 1);
            else
            	MyCrawler.fileSizes.put("10KB ~ <100KB", MyCrawler.fileSizes.get("10KB ~ <100KB")+1);
        }
        else if(x.length >= 102400 && x.length < 1048576) 
        {
            if(!MyCrawler.fileSizes.containsKey("100KB ~ <1MB"))
            	MyCrawler.fileSizes.put("100KB ~ <1MB", 1);
            else
            	MyCrawler.fileSizes.put("100KB ~ <1MB", MyCrawler.fileSizes.get("100KB ~ <1MB")+1);
        }
        else 
        {
            if(!MyCrawler.fileSizes.containsKey(">1MB"))
            	MyCrawler.fileSizes.put(">1MB", 1);
            else
            	MyCrawler.fileSizes.put(">1MB", MyCrawler.fileSizes.get(">1MB")+1);
        }

	}

	public void savePage(Page page) throws IOException 
	{
		String url = page.getWebURL().getURL().replaceAll("/$", "");
		url = url.replaceAll("/", "--");

		String filePath = storageFolder + url;
		FileOutputStream fileOutputStream = new FileOutputStream(filePath,false);
		fileOutputStream.write(page.getContentData());
		fileOutputStream.close();

	}

	public void closeFile() 
	{
		try 
		{
			Controller.visitCSV.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
