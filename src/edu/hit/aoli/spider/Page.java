package edu.hit.aoli.spider;

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
	private InvertIndexTable table;
	private boolean storePage;

	/**
	 * @see #Page(InvertIndexTable, boolean)
	 * @param table
	 */
	public Page(InvertIndexTable table) {
		this(table, false);
	}

	/**
	 * Create page class, for doing page segmentation, and build the invert
	 * index table
	 * 
	 * @param table
	 *            the invert index table to be built, if given null, then will
	 *            not build invert index table
	 * @param storePage
	 *            store the grabbed pages or not, default not
	 */
	public Page(InvertIndexTable table, boolean storePage) {
		this.table = table;
		this.storePage = storePage;
	}

	public void segment(NodeList nodes, String urlStr) {
		// extract the title of page, build a map from title to url
		String title = nodes.extractAllNodesThatMatch(new TagNameFilter("title"), true).asString();
		// extract the keywords, description
		AndFilter keywordFilter = new AndFilter(new TagNameFilter("meta"), new HasAttributeFilter("name", "keywords"));
		AndFilter descrpFilter = new AndFilter(new TagNameFilter("meta"),
				new HasAttributeFilter("name", "description"));
		NodeList metaList = nodes.extractAllNodesThatMatch(new OrFilter(keywordFilter, descrpFilter), true);
		String metaContent = "";
		for (int i = 0; i < metaList.size(); ++i) {
			MetaTag m = (MetaTag) metaList.elementAt(i);
			metaContent += m.getMetaContent() + "\n";
		}
		// extract the content
		AndFilter paraFilter = new AndFilter(new TagNameFilter("div"), new HasAttributeFilter("class", "para"));
		AndFilter lemmaFilter = new AndFilter(new TagNameFilter("div"),
				new HasAttributeFilter("class", "lemma-summary"));
		OrFilter filter = new OrFilter(
				new NodeFilter[] { new TagNameFilter("h1"), new TagNameFilter("h2"), paraFilter, lemmaFilter });
		String content = title + "\n" + metaContent + nodes.extractAllNodesThatMatch(filter, true).asString();
		// to help build the invert index table
		if (table != null)
			table.insertTableByNewPage(urlStr, title, content);
		if (storePage)
			Utils.output(content, "pages/" + title);
	}
}
