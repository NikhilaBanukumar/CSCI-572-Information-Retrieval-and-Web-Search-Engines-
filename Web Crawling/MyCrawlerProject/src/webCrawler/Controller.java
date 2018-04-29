package webCrawler;
import java.io.FileWriter;

import au.com.bytecode.opencsv.CSVWriter;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;


//code to configure the crawler and define the seed URLs
public class Controller {
	
	public final static int maxDepthOfCrawling = 16;
	public final static int maxpagesToFetch = 20000;
	public final static int politenessDelay = 1000;
	public final static int maxDownloadSize = 100000000;
	public final static String setUserAgentString = "crawler4j (Nikhila Banukumar)";
	
	//Objects of CSV writer
	public static CSVWriter visitCSV;
	public static CSVWriter fetchCSV;
	public static CSVWriter allCSV;
	public static CSVWriter extraCSV;
	//public static CSVWriter pageRank;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		//Open three CSVs writer and initialize the headers
		String directory = "E:\\Study\\Academics\\Spring 2017\\CSCI572\\WebCrawlNikhila";
		visitCSV = new CSVWriter(new FileWriter(directory+"/visit.csv", true), ',');
		fetchCSV = new CSVWriter(new FileWriter(directory+"/fetch.csv", true), ',');
		allCSV = new CSVWriter(new FileWriter(directory+"/urls.csv", true), ',');
		extraCSV = new CSVWriter(new FileWriter(directory+"/extras.csv", true), ',');
		//pageRank = new CSVWriter(new FileWriter(directory+"/pageRank.csv", true), ',');

		String[] values_all = {"URls","Indicator"};
		allCSV.writeNext(values_all);

		String[] values_fetch = {"URls","HTTP Status Code"};
		fetchCSV.writeNext(values_fetch);
		
		String[] values_visit = {"URls","Size","# of Outlinks","Content-Type"};
		visitCSV.writeNext(values_visit);
		
		String crawlStorageFolder = "E:\\Study\\Academics\\Spring 2017\\CSCI572\\WebCrawlNikhila\\CrawlData";
		int numberOfCrawlers = 1;
		
		//CrawlConfig specifies the configuration of crawl
		CrawlConfig config = new CrawlConfig();
		
		/*
         * You can set the location of the folder where you want your crawled
         * data to be stored
         */
        config.setCrawlStorageFolder(crawlStorageFolder);
        
        /*
         * You can set the maximum crawl depth here. The default value is -1 for
         * unlimited depth
         */
        config.setMaxDepthOfCrawling(maxDepthOfCrawling);
        
        /*
         * You can set the maximum number of pages to crawl. The default value
         * is -1 for unlimited number of pages
         */
        config.setMaxPagesToFetch(maxpagesToFetch);
        
        /*
         * Be polite: Make sure that we don't send more than 1 request per
         * second (1000 milliseconds between requests).
         */
        //config.setPolitenessDelay(politenessDelay);
        
        /*
         * Be polite: Make sure that we don't send more than 1 request per
         * second (1000 milliseconds between requests).
         */
        config.setUserAgentString(setUserAgentString);
        
        /*
         * This config parameter can be used to set your crawl to be resumable
         * (meaning that you can resume the crawl from a previously
         * interrupted/crashed crawl). Note: if you enable resuming feature and
         * want to start a fresh crawl, you need to delete the contents of
         * rootFolder manually.
         */
        config.setResumableCrawling(false);
        
        /*
         * @param maxDownloadSize Max allowed size of a page. Pages larger than this size will not be fetched.
        */
        config.setMaxDownloadSize(maxDownloadSize);
        
       /* 
        * Should we fetch binary content such as images, audio, ...?
        */
        config.setIncludeBinaryContentInCrawling(true);
        
        /*
         * Instantiate the controller for this crawl.
         * set up pagefetcher and robots.txt handlers
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        
        try
        {
	        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
	
	        /*
	         * For each crawl, you need to add some seed urls. These are the first
	         * URLs that are fetched and then the crawler starts following links
	         * which are found in these pages
	         */
	        controller.addSeed("https://www.washingtonpost.com/");

	        /*
	         * Start the crawl. This is a blocking operation, meaning that your code
	         * will reach the line after this only when crawling is finished.
	         */
	        controller.start(MyCrawler.class, numberOfCrawlers);
        }
        catch(Exception e)
        {
        	System.out.println("Exception caught :"+e.getMessage());
        }
        
        //Close all three CSVs writer
        allCSV.close();
        fetchCSV.close();
        visitCSV.close();
        extraCSV.close();
        //pageRank.close();
	}
}
