package edu.hit.aoli.spider;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Demo {
	public static void main (String[] args) {
		String urlStr = "http://baike.baidu.com/item/%E7%9F%B3%E5%AE%B6%E5%BA%84%E4%B8%89%E9%B9%BF%E9%9B%86%E5%9B%A2%E8%82%A1%E4%BB%BD%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8/846407";
		try {
			Parser parser = new Parser(urlStr);
			parser.setEncoding("utf-8");
			NodeFilter filter = new TagNameFilter("dd");
			NodeList nodes = parser.parse(filter);
			System.out.println(nodes.asString());
		} catch (ParserException e) {
			e.printStackTrace();
		}

	}
}
