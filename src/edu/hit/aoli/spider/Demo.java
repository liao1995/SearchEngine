package edu.hit.aoli.spider;


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
		InvertIndexTable table = new InvertIndexTable();
		Page page = new Page(table);
		Spider s = new Spider(page, 100000);
		s.start("https://baike.baidu.com/");
		table.store();
		// s.start(args[0]);
	}
}
