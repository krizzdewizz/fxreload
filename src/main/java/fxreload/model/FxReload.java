package fxreload.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FxReload {
	private List<String> file;
	private List<WebPage> webPage;

	public List<String> getFile() {
		if (file == null) {
			file = new ArrayList<>();
		}
		return file;
	}

	public void setFile(List<String> file) {
		this.file = file;
	}

	public void setWebPage(List<WebPage> webPage) {
		this.webPage = webPage;
	}

	public List<WebPage> getWebPage() {
		if (webPage == null) {
			webPage = new ArrayList<>();
		}
		return webPage;
	}
}
