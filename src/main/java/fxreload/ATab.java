package fxreload;

import javafx.concurrent.Worker;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;

public class ATab extends Tab {
	public final WebView webView;
	int scrollY;
	private Watch watch;

	public ATab(Watch watch) {
		this.watch = watch;
		webView = new WebView();
		webView.getEngine().getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				onLoad();
			}
		});
		setContent(webView);
	}

	public void reload() {
		watch.notifyChanged();
	}

	private void onLoad() {
		webView.getEngine().executeScript(String.format("window.scrollTo(0, %s);", scrollY));
	}

	public void loadUrl(String url) {
		memoizeScrollPos();
		webView.getEngine().load(url);
	}

	public void load(String content) {
		memoizeScrollPos();
		webView.getEngine().loadContent(IndexHtml.get(content));
	}

	private void memoizeScrollPos() {
		Object sy = webView.getEngine().executeScript("window.scrollY");
		if (sy instanceof Integer) {
			scrollY = (int) sy;
		} else {
			scrollY = 0;
		}
	}

}
