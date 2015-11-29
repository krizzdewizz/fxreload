package fxreload;

import java.io.InputStream;

public class HelpTab extends ATab {
	public HelpTab() {
		super(null);
		setText("Help");
		try (InputStream in = getClass().getResourceAsStream("README.md")) {
			load(Processor.MARKDOWN.process(IndexHtml.convertStreamToString(in)));
		} catch (Exception e) {
			// ignore
		}
	}
}