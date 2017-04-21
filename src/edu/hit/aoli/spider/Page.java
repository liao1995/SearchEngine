package edu.hit.aoli.spider;

import java.util.HashMap;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.NodeList;


/**
 * Do page segmentation
 * 
 * @author liao 2017.4.21
 */
class Page {
	private HashMap<String, String> title2URL;

	public Page() {
		title2URL = new HashMap<>();
	}

	public void segment(NodeList nodes, String urlStr) {
		// extract the title of page, build a map from title to url
		String title = nodes.extractAllNodesThatMatch(new TagNameFilter("title"), true).asString();
		title2URL.put(title, urlStr);
		// extract the keywords, description 
		AndFilter keywordFilter = new AndFilter(new TagNameFilter("meta"), new HasAttributeFilter("name", "keywords"));
		AndFilter descrpFilter = new AndFilter(new TagNameFilter("meta"),
				new HasAttributeFilter("name", "description"));
		NodeList metaList = nodes.extractAllNodesThatMatch(new OrFilter(keywordFilter, descrpFilter), true);
		String metaContent = "";
		for (int i = 0; i < metaList.size(); ++i) {
			MetaTag m = (MetaTag)metaList.elementAt(i);
			metaContent += m.getMetaContent() + "\n";
		}
		// extract the content
		AndFilter paraFilter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "para"));
		AndFilter lemmaFilter = new AndFilter(new TagNameFilter("div"),
				new HasAttributeFilter("class", "lemma-summary"));
		OrFilter filter = new OrFilter(
				new NodeFilter[] { new TagNameFilter("h1"), new TagNameFilter("h2"), paraFilter, lemmaFilter });
		String content = title + "\n" + metaContent + nodes.extractAllNodesThatMatch(filter, true).asString();
		Utils.output(content, "pages/" + title);
	}

	public void clear() {
		title2URL.clear();
	}
}
