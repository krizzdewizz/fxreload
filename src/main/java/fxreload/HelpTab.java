package fxreload;

import java.awt.Desktop;
import java.io.InputStream;

import netscape.javascript.JSObject;

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

	@Override
	void onLoad() {
		super.onLoad();
		JSObject win = (JSObject) webView.getEngine().executeScript("window");
		win.setMember("fxreloadHelp", this);
	}

	public void openSettings() {
		try {
			Desktop.getDesktop().open(Settings.INSTANCE.getFile().toFile());
		} catch (Exception e1) {
		}
	}
}