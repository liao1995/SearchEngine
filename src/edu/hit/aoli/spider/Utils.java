package edu.hit.aoli.spider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

class Utils {
	private static Logger logger = Logger.getLogger(Utils.class);
	/**
	 * Print the error message to standard error and exit. Just like the perror
	 * function in c.
	 * 
	 * @param msg
	 *            error message
	 */
	public static void perror(String msg) {
		logger.fatal(msg);
		System.exit(-1);
	}

	/**
	 * Output the key and value pairs of map to file
	 * 
	 * @param map
	 *            map need be written
	 * @param filename
	 *            filename of written file
	 */
	public static void output(HashMap<String, String> map, String filename) {
		try {
			BufferedWriter bfw = new BufferedWriter(new FileWriter(filename));
			for (String key : map.keySet()) {
				bfw.write(key + "\t" + map.get(key));
				bfw.newLine();
			}
			bfw.close();
		} catch (IOException e) {
			perror(e.getMessage());
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
	public static void output(String content, String filename) {
		try {
			File f = new File(filename);
			if (f.getParentFile() != null && !f.getParentFile().exists())
				f.getParentFile().mkdirs();
			BufferedWriter bfw = new BufferedWriter(new FileWriter(f));
			bfw.write(content);
			bfw.close();
			logger.info("written " + filename);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return;
		}
	}
}
