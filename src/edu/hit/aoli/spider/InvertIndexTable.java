package edu.hit.aoli.spider;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

class InvertIndexTable {
	private static Logger logger = Logger.getLogger(Page.class);
	private static HashSet<String> stopWordSet;
	private HashMap<String, LinkedList<DocItem>> table;
	private ArrayList<String> titles; // title of each page
	private ArrayList<String> urls; // URL of each page, one to one
									// corresponding with titles

	public InvertIndexTable() {
		table = new HashMap<>();
		titles = new ArrayList<>();
		urls = new ArrayList<>();
		stopWordSet = new HashSet<>();
		// read stop words from file
		BufferedReader buffr = null;
		try {
			buffr = new BufferedReader(new FileReader("stopword.dic"));
			String word;
			while ((word = buffr.readLine()) != null)
				stopWordSet.add(word.trim());
		} catch (IOException e) {
			logger.warn("no stop word was loaded: " + e.getMessage());
		} finally {
			try {
				if (null != buffr)
					buffr.close();
			} catch (IOException e) {
				logger.warn(e.getMessage());
			}
		}
	}

	/**
	 * Increase the invert index table given the new page
	 * 
	 * @param urlStr
	 *            url of this page
	 * @param title
	 *            title of this page
	 * @param content
	 *            content of this page
	 */
	public void insertTableByNewPage(String urlStr, String title, String content) {
		logger.info("build index for " + title);
		int pageIdx = titles.size();
		titles.add(title); // store the title of this page
		urls.add(urlStr); // store the url of this page for retrieval
		HashMap<String, DocItem> map = new HashMap<>(); // template map for
														// statistic of this
														// page
		// split word, build table
		BufferedReader buffr = new BufferedReader(new StringReader(content));
		IKSegmenter seg = new IKSegmenter(buffr, true);
		Lexeme lex = null;
		try {
			while ((lex = seg.next()) != null) {
				String word = lex.getLexemeText().trim();
				if (stopWordSet.contains(word))
					continue; // stop word
				DocItem item = map.get(word);
				if (item == null) {
					item = new DocItem(pageIdx, lex.getBeginPosition());
					map.put(word, item);
				}
				item.increase();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		// add all new word to invert index table
		for (String key : map.keySet()) {
			LinkedList<DocItem> list = table.get(key);
			if (list == null) {
				list = new LinkedList<>();
				table.put(key, list);
			}
			list.add(map.get(key));
		}
	}

	/**
	 * Store the invert index table object to file
	 */
	public void store() {
		logger.info("try write invert index table to file...");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("invert.table"));
			oos.writeObject(table);
			oos.close();
			FileWriter fw = new FileWriter("table.txt");
			for (String key : table.keySet()) {
				fw.write(key);
				LinkedList<DocItem> list = table.get(key);
				for (int i = 0; i < list.size(); ++i) {
					fw.write(" -> " + list.get(i).getDocID() + "|" + list.get(i).getStartPos() + "|"
							+ list.get(i).getTF());
				}
				fw.write("\n");
				fw.flush();
			}
			fw.close();
		} catch (IOException e) {
			logger.error("Error when store invert index table: " + e.getMessage());
		}
		logger.info("write OK!");
	}
	
}
