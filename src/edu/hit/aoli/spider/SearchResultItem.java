package edu.hit.aoli.spider;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class SearchResultItem {

	private String title;
	private String url;

	public SearchResultItem(String title, String url) {
		this.title = title;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getContent() {
		BufferedReader buffr = null;
		try {
			buffr = new BufferedReader(new FileReader("pages/" + title));
			String content = "", line;
			while ((line = buffr.readLine()) != null) 
				content += line + "\n";
			return content;
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (buffr != null)
					buffr.close();
			} catch (IOException e) {
			}
		}
	}
}
