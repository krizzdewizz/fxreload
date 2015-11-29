package fxreload;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class IndexHtml {
	public static String get(String content) {
		String pageName = "index.html";
		URL res = IndexHtml.class.getResource(pageName);
		try (InputStream in = res.openStream()) {
			String tpl = convertStreamToString(in);
			String uri = res.toString();
			// base must end with /
			String base = uri.substring(0, uri.length() - pageName.length());
			String all = tpl.replace("{{BASE}}", base).replace("{{CONTENT}}", content);
			// Files.write(Paths.get("d:/downloads/x.html"),
			// all.getBytes("UTF-8"));
			return all;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String convertStreamToString(InputStream is) {
		try (Scanner s = new Scanner(is)) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}
}
