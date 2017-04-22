package edu.hit.aoli.spider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;

public class Demo {
	
	/**
	 * The initial URL need to be given by the command line.
	 * 
	 * @param args
	 *            first command argument refer to the initial URL
	 */
	public static void main(String[] args) {
		// if (args.length < 1)
		// perror("Usage: java Spider [url]");

//		InvertIndexTable table = new InvertIndexTable();
//		Page page = new Page(table);
//		Spider s = new Spider(page, 100000);
//		s.start("https://baike.baidu.com/");
//		table.store();
		
		HashMap<String, LinkedList<DocItem>> table = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream("invert.table"));
			table = (HashMap<String, LinkedList<DocItem>>) ois.readObject();
			ois.close();
			Search.search(table, "ÆëÌì´óÊ¥ËïÎò¿Õ");
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		// s.start(args[0]);
	}
}
