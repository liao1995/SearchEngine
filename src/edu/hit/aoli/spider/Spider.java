package edu.hit.aoli.spider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

class Spider {

	private static Logger logger = Logger.getLogger(Spider.class);
	/**
	 * Max capacity of the set, e.g. maximum urls to be grabbed
	 */
	public static int MAX_CAPACITY = 0x70000000;
	public static String DEFAULT_INIT_URL = "https://baike.baidu.com/";
	private Queue<String> urlQueue;
	private HashSet<String> existed;
	private int capacity;
	private Page pageSeg; // page segmentation
	private String initURL; 	// initial URL

	public Spider() {
		this(null, 100000, DEFAULT_INIT_URL);
	}

	/**
	 * @see #Spider(Page, int)
	 */
	public Spider(int capacity) {
		this(null, capacity, DEFAULT_INIT_URL);
	}

	/**
	 * @see #Spider(Page, int)
	 */
	public Spider(Page page) {
		this(page, 100000, DEFAULT_INIT_URL);
	}

	/**
	 * A simple web spider with given capacity and a given page segmentation all
	 * pages which this spider get will be feed to page segmentation tool, if
	 * given null to page, all content this spider grabbed will be written to a
	 * directory named "spider" in current path
	 * 
	 * @param capacity
	 *            the capacity of the set
	 * @param page
	 *            the page segmentation tool
	 */
	public Spider(Page page, int capacity, String initURL) {
		this.pageSeg = page;
		if (null == initURL) initURL = DEFAULT_INIT_URL;
		this.initURL = initURL;
		urlQueue = new LinkedList<>();
		existed = new HashSet<>();
		if (capacity > MAX_CAPACITY || capacity < 0)
			this.capacity = MAX_CAPACITY;
		else
			this.capacity = capacity;
	}

	public void start() {
		clear();
		urlQueue.add(initURL);
//		urlQueue.add("http://baike.baidu.com/item/%E5%93%88%E5%B0%94%E6%BB%A8%E5%B7%A5%E4%B8%9A%E5%A4%A7%E5%AD%A6");
		int iter = 0;
		// String regex =
		// "https?://baike\\.baidu\\.com/[\\w\\-_]+/[\\w\\-\\.,@?^=%&amp;:/~\\+#]*";
		String regex = "https?://baike\\.baidu\\.com/item/[\\w\\-\\.,@?^=%&amp;:/~\\+#]*";
		String prefix = "http://baike.baidu.com/";
		logger.info("using regex: " + regex);
		logger.info("using prefix: " + prefix);
		while (!urlQueue.isEmpty() && existed.size() < capacity) {
			String url = urlQueue.poll();
			extractTitleAndLinkFromURL(url, regex, prefix);
			if (++iter % 1000 == 0)
				logger.info("solved " + iter);
		}
	}

	private void clear() {
		urlQueue.clear();
		existed.clear();
	}

	/**
	 * Extract the title and all links from the given urlStr. Add this map of
	 * title to urlStr, add all links in this page to set
	 * 
	 * @param urlStr
	 *            URL to be parse
	 * @param absLinkRegex
	 *            regular expression used to match the link from pages see as
	 *            absolute path
	 * @param relLinkPrefix
	 *            prefix use to fill relative URL to absolute, if given null,
	 *            use urlStr by default
	 * 
	 */
	private void extractTitleAndLinkFromURL(String urlStr, String absLinkRegex, String relLinkPrefix) {
		if (relLinkPrefix == null)
			relLinkPrefix = urlStr;
		try {

			// set connect and read time limit
			URL urlPage = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) urlPage.openConnection();
			conn.setConnectTimeout(2000);
			conn.setReadTimeout(3000);
			// parser
			Parser parser = new Parser(conn);
			parser.setEncoding("utf-8");
			NodeFilter filter = new TagNameFilter("html");
			NodeList nodes = parser.parse(filter);
			if (nodes.asString().trim().isEmpty())
				return; // filter HTML file
			String content = nodes.toHtml();
			// Do page segmentation
			if (pageSeg != null)
				pageSeg.segment(nodes, urlStr);
			else {
				String title = nodes.extractAllNodesThatMatch(new TagNameFilter("title"), true).asString();
				Utils.output(content, "spider/" + title);
			}
			// extract all ABSOLUTE links of this page, add them to set
			Pattern pa = Pattern.compile(absLinkRegex, Pattern.DOTALL);
			Matcher ma = pa.matcher(content);
			while (ma.find()) {
				String newUrl = ma.group();
				if (!existed.contains(newUrl)) {
					urlQueue.add(newUrl);
					existed.add(newUrl);
				}
			}
			// extract all RELATIVE links of this page, add them to set
			pa = Pattern.compile("href=\"/item[^\"]+\"");
			ma = pa.matcher(content);
			while (ma.find()) {
				String newUrl = ma.group();
				int start = newUrl.indexOf('/'), end = newUrl.lastIndexOf('\"');
				if (start != -1 && end != -1 && (end - start) > 1) {
					String absUrl = relLinkPrefix + newUrl.substring(newUrl.indexOf('/') + 1, newUrl.lastIndexOf('\"'));
					if (!existed.contains(absUrl)) {
						urlQueue.add(absUrl);
						existed.add(absUrl);
					}
				}
			}

		} catch (ParserException | IOException e) {
			return; // ignore the bad url
		}
	}
}
