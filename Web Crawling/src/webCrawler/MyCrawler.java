package webCrawler;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpStatus;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
	
	//Objects to access the Fetch, Visit and AllUrls class methods
	AllUrls allObj;
	Fetch fetchObj;
	Visit visitObj;
	pageRank pageRankObj;
	
	public static long fetches = 0;         		//Total no of URLs that started with the given seed
	public static long fetches_succ = 0;           	//Total no of URLs that were successfully downloaded
	public static long fetches_aborted =0;        	//Total no of URLs that were aborted
    public static long fetches_failed =0;        	//Total no of URLs that were failed
    public static long urls_extracted = 0;         	//Total no of URLs the crawler attempted to fetch
    public static long max_fetch = 0;               //Counter to keep track of 5000 URLs
    
    //Sets to keep a track of the number of Unique URLs in News Website and Outside News Website
    public static Set<String> news_Wesite = new HashSet<String>();
    //public static Set<String> school_USC = new HashSet<String>();
    public static Set<String> news_Outside = new HashSet<String>();
    
    //Sets to keep track of Unique URLs extracted
    public static Set<String> unique_URLS_extracted = new HashSet<String>();
    
    //File Sizes : HashMap to keep track of the File Sizes :: statistics about file sizes of visited URLs
    public static HashMap<String, Integer> fileSizes = new HashMap<String, Integer>();
    
    //Status Codes : HashMap to keep track of number of times various HTTP status codes were encountered during crawling
    public static HashMap<Integer, Integer> statusCodes = new HashMap<Integer, Integer>();
    
    //Content Types : HashMap to keep track of number of times various content types were encountered during crawling
    public static HashMap<String, Integer> contentType = new HashMap<String, Integer>();
	
	public MyCrawler()
	{
		try 
		{
			//instantiate other class objects so that the required files can be opened
			allObj = new AllUrls();
			fetchObj = new Fetch();
			visitObj = new Visit();
			//pageRankObj = new pageRank();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
	 @Override
	 public boolean shouldVisit(Page referringPage, WebURL url){
		String href = url.getURL();
		
		allObj.writeAllUrls(referringPage,url);
		
		//Keeps track of all the URls extracted
		urls_extracted++;
		
		//keep a count of unique URLs extracted
		unique_URLS_extracted.add(href);
		
		return href.startsWith("https://www.washingtonpost.com/");
	 }
	 
	 /**
      * This function is called when a page is fetched and ready
      * to be processed by your program.
      */
	 @Override
	 public void visit(Page page){
		
		 visitObj.writeVisitUrls(page);
	 }
	 
	 @Override
	    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		//counter to keep track of pages ready to be fetched after passing the FILTER condition
		 fetches++;
		 fetchObj.writeFetchUrls(webUrl, statusCode);
		 
		 if(statusCode != HttpStatus.SC_OK)
		 {
			 if(statusCode >= 300 && statusCode <400)
			 {
				 fetches_aborted++;
			 }
			 else
				 fetches_failed++;
		 }
		 
		if(statusCode == 301) 
		 {
	            System.out.println(statusCode +" ---- "+ webUrl.getURL());
		 }
		
		if(statusCode != 200)
		{
			if(!statusCodes.containsKey(statusCode))
	        {
				statusCodes.put(statusCode, 1);
	        }
	        else
	        {
	        	statusCodes.put(statusCode, statusCodes.get(statusCode)+1);
	        }
		}
		else
		{
			if(!statusCodes.containsKey(statusCode))
	        {
				statusCodes.put(statusCode, 1);
	        }
	        else
	        {
	        	statusCodes.put(statusCode, statusCodes.get(statusCode)+1);
	        }
			//counter to keep track of pages succeeded to be downloaded
			fetches_succ++;
		}
    }
	 
	 /**
	   * This function is called by controller before finishing the job.
	   * You can put whatever stuff you need here.
	   */
	 @Override
	    public void onBeforeExit() {
		 
		 //To write the console output to a file
		 PrintStream out = null;
		 try 
		 {
			out = new PrintStream(new FileOutputStream("E:\\Study\\Academics\\Spring 2017\\CSCI572\\WebCrawlNikhila\\output.txt"));
		 } 
		 catch (FileNotFoundException e) 
		 {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 System.setOut(out);
	    	System.out.println("Name : Nikhila BanuKumar");
	    	System.out.println("USC ID : 2623148495");
	    	System.out.println("School Crawled : Washington_Post");
	    	System.out.println();
	    	
	    	
		 	System.out.println("Fetch Statistics :");
	        System.out.println("==================");
	        System.out.println("# fetches attempted : "+fetches);
	        System.out.println("# fetches succeeded : "+fetches_succ);
	        System.out.println("# fetches aborted : "+fetches_aborted);
	        System.out.println("# fetches failed : "+fetches_failed);
	        //System.out.println("Fetches Failed :"+(fetches_aborted+fetches_failed));
	        System.out.println();
	        
	        System.out.println("Outgoing URLs :");
	        System.out.println("===============");
	        System.out.println("Total Urls extracted : "+ urls_extracted);
	        System.out.println("# unique URLs extracted :"+unique_URLS_extracted.size());
	        System.out.println("# unique URLs within news  website :" + news_Wesite.size());

	        System.out.println("# unique URLs outside news website :"+news_Outside.size());

	        System.out.println();
	        
	        System.out.println("Status Codes :");
	        System.out.println("==============");
	        for(Integer i:statusCodes.keySet()) 
	        {
	            Integer val = statusCodes.get(i);
	            System.out.println(i+" :"+val);
	        }
	        
	        System.out.println();
	        System.out.println("File Sizes :");
	        System.out.println("=============");
	        for(String i:fileSizes.keySet()) 
	        {
	            Integer val = fileSizes.get(i);
	            System.out.println(i+" :"+val);
	        }
	        
	        System.out.println();
	        System.out.println("Content Types :");
	        System.out.println("================");
	        for(String i:contentType.keySet()) {
	            Integer val = contentType.get(i);
	            System.out.println(i+" "+val);
	        }
	 }
}
