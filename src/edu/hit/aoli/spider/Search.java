package edu.hit.aoli.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * search from invert index table based on Vector Space Models
 * 
 * @author liao 2017.4.22
 */
class Search {
	private static Logger logger = Logger.getLogger(Page.class);

	public static int[] search(HashMap<String, LinkedList<DocItem>> table, String query) {
		if (table == null)
			Utils.perror("empty invert index table");
		// split the query
		BufferedReader buffr = new BufferedReader(new StringReader(query));
		IKSegmenter seg = new IKSegmenter(buffr, true);
		Lexeme lex = null;
		ArrayList<Integer> qryTF = new ArrayList<>();
		ArrayList<String> qryWords = new ArrayList<>();
		try {
			while ((lex = seg.next()) != null) {
				String word = lex.getLexemeText();
				int i;
				for (i = 0; i < qryWords.size(); ++i)
					if (word.trim().equals(qryWords.get(i))) {
						qryTF.set(i, qryTF.get(i) + 1);
						break;
					}
				if (i >= qryWords.size()) {
					qryWords.add(word);
					qryTF.add(1);
				}
			}
		} catch (IOException e) {
			// exception handle: do not split the word
			qryTF.add(1);
			qryWords.add(query);
			logger.error("error when split the query: " + e.getMessage());
		}
		// extract doc item related to query words from table
		TreeSet<Integer> tree = new TreeSet<>(); // hold all the doc IDs
		ArrayList<HashMap<Integer, DocItem>> tmpMatrix = new ArrayList<>();
		for (int i = 0; i < qryWords.size(); ++i) {
			LinkedList<DocItem> list = table.get(qryWords.get(i));
			HashMap<Integer, DocItem> map = new HashMap<>();
			for (int j = 0; j < list.size(); ++j) {
				map.put(list.get(j).getDocID(), list.get(j));
				tree.add(list.get(j).getDocID());
			}
			tmpMatrix.add(map);
		}
		// build the doc item matrix without query which hold qryWords and qryTF
		int numWords = qryWords.size();
		int numDocs = tree.size();
		Integer[] docIDs = tree.toArray(new Integer[0]); // hold doc IDs for
															// each row
		DocItem[][] matrix = new DocItem[numDocs][numWords];
		for (int j = 0; j < numWords; ++j) { // for each word
			HashMap<Integer, DocItem> map = tmpMatrix.get(j);
			for (int i = 0; i < numDocs; ++i) // for each doc
				matrix[i][j] = map.get(docIDs[i]);
		}
		debug(qryWords, docIDs, matrix);
		// calculate the jaccard similarity, sort the retrieval result
		double[] dis = jaccard(qryTF, matrix);
		ArrayList<Pair<Integer, Double>> pairs = new ArrayList<>();
		for (int i = 0; i < dis.length; ++i)
			pairs.add(new Pair<Integer, Double>(docIDs[i], dis[i]));
		pairs.sort(new Comparator<Pair<Integer, Double>>() {
			@Override
			public int compare(Pair<Integer, Double> p1, Pair<Integer, Double> p2) {
				// sort by jaccard distance
				return p1.second - p2.second < 0 ? -1 : 1;
			}
		});
		int[] pageIDs = new int[pairs.size()];
		for (int i = 0; i < pairs.size(); ++i)
			pageIDs[i] = pairs.get(i).first;
		return pageIDs;
	}

	/**
	 * Calculate the Jaccard distance between query sentence and all contents
	 * 
	 * @param qryTF
	 *            TF of query words
	 * @param items
	 *            array of pages, each page hold all related words TF
	 * @return the similarity vector between query sentence and each page
	 */
	private static double[] jaccard(ArrayList<Integer> qryTF, DocItem[][] items) {
		int numDocs = items.length, numWords = qryTF.size();
		double[] dis = new double[numDocs];
		for (int i = 0; i < numDocs; ++i) {
			double a_2 = 0, b_2 = 0, a_b = 0;
			for (int j = 0; j < numWords; ++j) {
				a_2 += qryTF.get(j) * qryTF.get(j);
				if (items[i][j] != null) {
					b_2 += items[i][j].getTF() * items[i][j].getTF();
					a_b += items[i][j].getTF() * qryTF.get(j);
				}
			}
			dis[i] = a_b / (a_2 + b_2 - a_b);
		}
		return dis;
	}

	/**
	 * print the debug information, e.g. the query matrix
	 */
	public static void debug(ArrayList<String> qryWords, Integer[] docIDs, DocItem[][] matrix) {
		int numWords = qryWords.size();
		int numDocs = docIDs.length;
		System.out.print("words:\t\t");
		for (int j = 0; j < numWords; ++j)
			System.out.print(qryWords.get(j) + "\t\t");
		System.out.println();
		for (int i = 0; i < numDocs; ++i) {
			System.out.print(docIDs[i] + ":\t\t");
			for (int j = 0; j < numWords; ++j) {
				if (null == matrix[i][j])
					System.out.print("null|null|null\t\t");
				else
					System.out.print(matrix[i][j].getDocID() + "|" + matrix[i][j].getStartPos() + "|"
							+ matrix[i][j].getTF() + "\t\t");
			}
			System.out.println();
		}
	}
}
