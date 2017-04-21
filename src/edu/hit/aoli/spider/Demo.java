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
		Page page = new Page();
		Spider s = new Spider(page, 200000);
		s.start("https://baike.baidu.com/");
		// s.start(args[0]);
	}
}
