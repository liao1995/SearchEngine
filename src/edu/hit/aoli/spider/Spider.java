package edu.hit.aoli.spider;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Spider {

	/**
	 * Max capacity of the set, e.g. maximum urls to be grabbed
	 */
	public static int MAX_CAPACITY = 0x70000000;

	private HashMap<String, String> title2URL;
	private TreeSet<String> urlSet;
	private HashSet<String> existed;
	private int capacity;

	public Spider() {
		this(0x010000000);
	}

	/**
	 * A simple web spider with given capacity
	 * 
	 * @param capacity
	 *            the capacity of the set
	 */
	public Spider(int capacity) {
		title2URL = new HashMap<>();
		urlSet = new TreeSet<>();
		existed = new HashSet<>();
		if (capacity > MAX_CAPACITY)
			this.capacity = MAX_CAPACITY;
		else
			this.capacity = capacity;
	}

	/**
	 * The initial URL need to be given by the command line.
	 * 
	 * @param args
	 *            first command argument refer to the initial URL
	 */
	public static void main(String[] args) {
		if (args.length < 1)
			 perror("Usage: java Spider [url]");
		Spider s = new Spider();
		//s.start("http://xueshu.baidu.com/");
		s.start(args[0]);
	}

	public void start(String urlStr) {
		clear();
		urlSet.add(urlStr);
		int iter = 0;
		while (!urlSet.isEmpty() && existed.size() < capacity) {
			String url = urlSet.pollFirst();
			System.out.println(url);
			extractTitleAndLinkFromURL(url);
			if (++iter % 1000 == 0) output("result_" + iter);
		}
		System.out.println(title2URL.size());
		output("result.txt");
	}
	
	private void clear() {
		urlSet.clear();
		title2URL.clear();
		existed.clear();
	}
	
	private void output(String filename) {
		try {
			BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
			for (String key : title2URL.keySet()) {
				bfw.write(key + "\t" + title2URL.get(key));
				bfw.newLine();
			}
			bfw.close();
		} catch (IOException e) {
			perror(e.getMessage());
		}
	}

	/**
	 * Extract the title and all links from the given urlStr. Add this map of
	 * title to urlStr, add all links in this page to set
	 * 
	 * @param urlStr
	 *            URL to be parse
	 */
	private void extractTitleAndLinkFromURL(String urlStr) {
		try {
			URL urlPage = new URL(urlStr); 
			HttpURLConnection conn = (HttpURLConnection) urlPage.openConnection();   
			conn.setConnectTimeout(100);  	// 20 ms time limit  
			conn.setReadTimeout(100);  		// 100 ms time limit
			Parser parser = new Parser(conn);
			parser.setEncoding("utf-8");
			NodeFilter filter = new TagNameFilter("html");
			NodeList nodes = parser.parse(filter);
			// extract the title of page, build a map from title to url
			title2URL.put(nodes.extractAllNodesThatMatch(new TagNameFilter("title"), true).asString(), urlStr);
			String content = nodes.toHtml();
			// extract all links of this page, add them to set
			String regex = "https?://\\w+\\.\\w+\\.\\w+";
			Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
			Matcher ma = pa.matcher(content);
			while (ma.find()) {	
				String newUrl = ma.group();
				if (!existed.contains(newUrl)) {
					urlSet.add(newUrl);
					existed.add(newUrl);
				}
			}
		} catch (ParserException | IOException e) {
			return ;	// ignore the bad url
//			perror(e.getMessage() + " url: " + urlStr);
		}
	}

	/**
	 * Print the error message to standard error and exit. Just like the perror
	 * function in c.
	 * 
	 * @param msg
	 *            error message
	 */
	private static void perror(String msg) {
		System.err.println(msg);
		System.exit(-1);
	}
}
