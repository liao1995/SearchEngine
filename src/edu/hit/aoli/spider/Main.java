package edu.hit.aoli.spider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class);
	public static InvertIndexTable table;

	/**
	 * grab pages from the Internet from the initial URL NOTE THAT this spider
	 * grab the certain format content of baike, http://baike.baidu.com/. You
	 * can change the regular expression of {@link Spider} to do specific job
	 * 
	 * @param initURL
	 *            initial URL
	 * @param capacity
	 *            maximum URLs the spider will visit
	 */
	public static void spider(String initURL, int capacity) {
		if (null == initURL || initURL.trim().isEmpty())
			return; // ignore empty input
		table = new InvertIndexTable();
		Page page = new Page(table, true);
		Spider spider = new Spider(page, capacity, initURL);
		spider.start(); // start grabbing
		table.store(); // store the inverted index table
	}

	public static void main(String args[]) {
		// spider("https://baike.baidu.com/", 200000);
		String query = "花果山猴";
		SearchResultItem[] results = search(query);
		writeToHTML(query, results, Search.getQueryWords());
		// for (int i = 0; i < results.length; ++i)
		// System.out.println(results[i].getTitle() + "\n" +
		// results[i].getUrl());
	}

	public static void writeToHTML(String query, SearchResultItem[] results, ArrayList<String> qryWords) {
		String header = "<html><head><title>" + query + "_搜索结果</title>";
		StringBuilder sb = new StringBuilder();
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		sb.append("<style type=\"text/css\">");
		sb.append("。tit {font:normal 15px; } .con{font:normal 12px;} #key { color: red;}");
		sb.append("</style></head>");
		sb.append("<body >");
		sb.append("<div>");
		sb.append("<br/>");
		for (int i = 0; i < results.length; ++i) {
			String title = results[i].getTitle();
			String url = results[i].getUrl();
			String content = results[i].getContent();
			// show keyword area in details
			int j;
			for (j = 0; j < qryWords.size(); ++j)
				if (content.contains(qryWords.get(j)))
					break;
			if (j < qryWords.size()) {
				int start = content.indexOf(qryWords.get(j));
				content = content.substring(start - 10 < 0 ? 0 : start - 10,
						start + 190 > content.length() ? content.length() : start + 190);
			} else
				content = content.substring(0, content.length() < 200 ? content.length() : 200);
			sb.append("<div class=\"tit\"><a href=\"");
			sb.append(url + "\">" + title + "</a></div><br/>");
			sb.append("<div class=\"con\">" + content + "</div><br>");
		}
		sb.append("</div></body>");
		// highlight the keyword
		String str = sb.toString();
		for (int i = 0; i < qryWords.size(); ++i)
			str = str.replaceAll(qryWords.get(i), "<span id=\"key\">" + qryWords.get(i) + "</span>");
		PrintStream ps = null;
		try {
			ps = new PrintStream(new FileOutputStream("index.html"), true, "utf-8");
			ps.print(header + str);
			ps.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			logger.error(e.getMessage());
			return;
		}
		logger.info("written web page OK!");
	}

	/**
	 * Do query, return the sorted search result
	 * 
	 * @param query
	 *            query string
	 * @return sorted search result {@link SearchResultItem}
	 */
	public static SearchResultItem[] search(String query) {
		if (null == table) {
			ObjectInputStream ois;
			try {
				logger.info("start reading inverted index table from file...");
				ois = new ObjectInputStream(new FileInputStream("invert.table"));
				table = (InvertIndexTable) ois.readObject();
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				logger.error("Can not start query: " + e.getMessage());
				return null;
			}
		}
		logger.info("start searching " + query);
		int[] pageOrders = table.search(query);
		SearchResultItem[] results = new SearchResultItem[pageOrders.length];
		for (int i = 0; i < pageOrders.length; ++i)
			results[i] = new SearchResultItem(table.getTitles().get(pageOrders[i]), table.getUrls().get(pageOrders[i]));
		return results;
	}
}
