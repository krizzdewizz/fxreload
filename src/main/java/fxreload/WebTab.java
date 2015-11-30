package fxreload;

import fxreload.model.WebPage;

public class WebTab extends ATab {

	public final WebPage webPage;

	public WebTab(Watch watch, WebPage webPage) {
		super(watch);
		this.webPage = webPage;
		setText(webPage.getUrl());
	}
}
