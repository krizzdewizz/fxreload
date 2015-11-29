package fxreload;

import java.util.Collections;

import org.asciidoctor.Asciidoctor;
import org.junit.Test;

public class ADOCTest {
	@Test
	public void test() throws Exception {
		final Asciidoctor asciiDoc = Asciidoctor.Factory.create();
		String html = asciiDoc.convert(IndexHtml.convertStreamToString(getClass().getResourceAsStream("README.md")), Collections.emptyMap());
		System.out.println(html);
		// Files.write(Paths.get(System.getProperty("user.dir"),
		// "src_test/fxreload/readme.html"), html.getBytes("UTF-8"));
	}

}
