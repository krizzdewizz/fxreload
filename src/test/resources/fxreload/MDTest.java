package fxreload;

import org.junit.Test;
import org.markdown4j.Markdown4jProcessor;

public class MDTest {
	@Test
	public void test() throws Exception {
		Markdown4jProcessor pd = new Markdown4jProcessor();
		String html = pd.process(IndexHtml.convertStreamToString(getClass().getResourceAsStream("README.md")));
		System.out.println(html);
		// Files.write(Paths.get(System.getProperty("user.dir"),
		// "src_test/fxreload/readme.html"), html.getBytes("UTF-8"));
	}

}
