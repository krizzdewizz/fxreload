package fxreload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import fxreload.model.FxReload;
import fxreload.model.WebPage;

public enum Settings {
	INSTANCE;

	Path getFile() {
		try {
			Path root = Paths.get(System.getProperty("user.home"), ".fxreload");
			if (!Files.exists(root)) {
				Files.createDirectory(root);
			}
			Path settings = root.resolve("settings.txt");
			if (!Files.exists(settings)) {
				save(settings);
			}
			return settings;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private FxReload fxReload;
	private boolean needsSave;
	private Watch watch;

	private Settings() {
		fxReload = new FxReload();
		load();
	}

	void load() {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FxReload.class);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			fxReload = (FxReload) unmarshaller.unmarshal(getFile().toFile());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void pickFileWatch(Path file) {
		String filee = file.toString();
		List<String> all = fxReload.getFile();
		all.remove(filee);
		all.add(0, filee);

		int MAX = 12;
		if (all.size() > MAX) {
			all = all.subList(0, MAX);
		}
		needsSave = true;
	}

	public List<WebPage> getWebPages() {
		return fxReload.getWebPage();
	}

	public List<Path> getFileWatches() {
		if (Main.DEBUG) {
			return Arrays.asList(//
					// Paths.get("D:\\prg\\eclipsefx\\fxreload\\src_test\\fxreload\\READadsdME.md"),
					// //
					Paths.get("D:\\prg\\eclipsefx\\fxreload\\src_test\\fxreload\\README.md"), //
					Paths.get("D:\\prg\\eclipsefx\\fxreload\\src_test\\fxreload\\README.adoc") //
			);
		}
		return fxReload.getFile().stream().map(it -> Paths.get(it)).collect(Collectors.toList());
	}

	public void close() {
		if (watch != null) {
			watch.close();
			watch = null;
		}
		if (!needsSave) {
			return;
		}
		save(getFile());
	}

	private void save(Path file) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(FxReload.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(fxReload, file.toFile());
			needsSave = false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addWebPage(WebPage webPage) {
		getWebPages().add(webPage);
		save(getFile());
	}

	public Watch getWatch() {
		return watch;
	}

	public void setWatch(Watch watch) {
		this.watch = watch;
	}
}
