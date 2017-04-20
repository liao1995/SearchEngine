package edu.hit.aoli.spider;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
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
	private Queue<String> urlQueue;
	private HashSet<String> existed;
	private int capacity;

	public Spider() {
		this(100000);
	}

	/**
	 * A simple web spider with given capacity
	 * 
	 * @param capacity
	 *            the capacity of the set
	 */
	public Spider(int capacity) {
		title2URL = new HashMap<>();
		urlQueue = new LinkedList<>();
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
		// if (args.length < 1)
		// perror("Usage: java Spider [url]");

		Spider s = new Spider();
		s.start("https://baike.baidu.com/");
		// s.start(args[0]);
	}

	public void start(String urlStr) {
		clear();
		urlQueue.add(urlStr);
		int iter = 0;
		// String regex =
		// "https?://baike\\.baidu\\.com/[\\w\\-_]+/[\\w\\-\\.,@?^=%&amp;:/~\\+#]*";
		String regex = "https?://baike\\.baidu\\.com/item/[\\w\\-\\.,@?^=%&amp;:/~\\+#]*";
		String prefix = "http://baike.baidu.com/";
		while (!urlQueue.isEmpty() && existed.size() < capacity) {
			String url = urlQueue.poll();
			System.out.println(url);
			extractTitleAndLinkFromURL(url, regex, prefix);
			if (++iter % 1000 == 0)
				output("result_" + iter);
		}
		System.out.println(title2URL.size());
		output("result.txt");
	}

	private void clear() {
		urlQueue.clear();
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
			// extract the title of page, build a map from title to url
			String title = nodes.extractAllNodesThatMatch(new TagNameFilter("title"), true).asString();
			title2URL.put(title, urlStr);
			String content = nodes.toHtml();
			output(content, "pages/" + title);
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
			// perror(e.getMessage() + ": " + urlStr);
		}
	}

	/**
	 * Written content to file
	 * 
	 * @param content
	 *            Content need to be output
	 * @param filename
	 *            Filename of the written file
	 */
	private void output(String content, String filename) {
		try {
			BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
			bfw.write(content);
			bfw.close();
		} catch (IOException e) {
			return;
			// perror(e.getMessage());
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
