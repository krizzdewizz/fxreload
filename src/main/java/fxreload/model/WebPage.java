package fxreload.model;

import java.util.List;

public class WebPage {

	private String url;
	private List<String> file;

	public WebPage() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getFile() {
		return file;
	}

	public void setFile(List<String> file) {
		this.file = file;
	}
}
