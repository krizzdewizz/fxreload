package fxreload;

import java.nio.file.Path;
import java.util.Collections;

import org.asciidoctor.Asciidoctor;
import org.markdown4j.Markdown4jProcessor;

public enum Processor {
	MARKDOWN {
		Markdown4jProcessor markdown;

		@Override
		public String process(String content) throws Exception {
			if (markdown == null) {
				markdown = new Markdown4jProcessor();
			}
			return markdown.process(content);
		}
	},
	ASCIIDOC {
		Asciidoctor asciiDoc;

		public String process(String content) throws Exception {
			if (asciiDoc == null) {
				asciiDoc = Asciidoctor.Factory.create();
			}
			return asciiDoc.convert(content, Collections.emptyMap());
		}
	},
	NO_PROCESSOR {
		public String process(String content) throws Exception {
			return null;
		}

		@Override
		public boolean needsContent() {
			return false;
		}
	};

	public abstract String process(String content) throws Exception;

	public static Processor find(Path path) {
		String pathh = path.toString();
		if (pathh.endsWith(".md")) {
			return MARKDOWN;
		} else if (pathh.endsWith(".adoc")) {
			return ASCIIDOC;
		}

		return NO_PROCESSOR;
	}

	public static void preload() {
		new Thread("Processor preload") {
			@Override
			public void run() {
				try {
					for (Processor it : values()) {
						it.process("a");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public boolean needsContent() {
		return true;
	}
}
