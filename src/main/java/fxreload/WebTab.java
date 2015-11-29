package fxreload;

import fxreload.model.WebWatch;

public class WebTab extends ATab {

	public final WebWatch webWatch;

	public WebTab(Watch watch, WebWatch webWatch) {
		super(watch);
		this.webWatch = webWatch;
		setText(webWatch.getUrl());
	}
}
