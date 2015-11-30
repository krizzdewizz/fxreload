package fxreload;

import java.awt.Desktop;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import fxreload.Watch.ChangedHandler;
import fxreload.model.WebPage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController implements Initializable {
	public Parent root;
	public TabPane tabPane;
	public MenuButton favMenu;
	public Label msg;
	public ToggleButton onTop;

	private File lastChosenFolder;

	void reloadFile(Path file, String processedContent) {
		FileTab tab = findFileTab(file);
		if (processedContent == null) {
			try {
				tab.loadUrl(file.toUri().toURL().toString());
			} catch (MalformedURLException e) {
				// e.printStackTrace();
			}
		} else {
			tab.load(processedContent);
		}
	}

	void reloadWebPage(WebPage webPage) {
		findWebTab(webPage).loadUrl(webPage.getUrl());
	}

	public void onClose() {
		FileTab tab = (FileTab) tabPane.getSelectionModel().getSelectedItem();
		if (tab == null) {
			return;
		}
		tab.getOnClosed().handle(null);
		tabPane.getTabs().remove(tab);
	}

	public void onReloadNow() {
		ATab tab = (ATab) tabPane.getSelectionModel().getSelectedItem();
		if (tab != null) {
			tab.reload();
		}
	}

	public void onOpen() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open file");
		fileChooser.setInitialDirectory(lastChosenFolder);
		fileChooser.getExtensionFilters().add(new ExtensionFilter("All files (*.*)", "*.*"));
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Files with processor (*.md;*.adoc)", "*.md;*.adoc"));
		List<File> files = fileChooser.showOpenMultipleDialog(getStage());
		if (files != null) {
			lastChosenFolder = files.get(0).getParentFile();
			files.stream().forEach(it -> addFileTab(it.toPath()));
			for (int i = files.size() - 1; i >= 0; i--) {
				Settings.INSTANCE.pickFileWatch(files.get(i).toPath());
			}
			buildMenu();
		}
	}

	private Stage getStage() {
		return (Stage) root.getScene().getWindow();
	}

	void addWebTab(WebPage webPage) {
		WebTab existing = findWebTab(webPage);
		if (existing != null) {
			tabPane.getSelectionModel().select(existing);
			return;
		}

		if (webPage.getFile().isEmpty()) {
			alert("No <file> found under <webPage>.");
			return;
		}

		ChangedHandler reloader = (a, b) -> reloadWebPage(webPage);

		String nonExisting = webPage.getFile().stream() //
				.map(Paths::get) //
				.filter(it -> !Files.exists(it)) //
				.map(it -> it.toString()).collect(Collectors.joining(", "));

		if (!nonExisting.isEmpty()) {
			alert(String.format("Non existing file(s) found: %s", nonExisting));
		}

		List<Watch> watches = webPage.getFile().stream() //
				.map(Paths::get) //
				.filter(Files::exists) //
				.map(it -> new Watch(it, Processor.NO_PROCESSOR, reloader)) //
				.collect(Collectors.toList());

		if (!watches.isEmpty()) {
			WebTab tab = new WebTab(watches.get(0), webPage);
			tab.setOnClosed(e -> watches.stream().forEach(it -> it.close()));
			tabPane.getTabs().add(tab);
			tabPane.getSelectionModel().select(tab);
			tab.reload(); // first load
		}
	}

	void addFileTab(Path file) {
		FileTab existing = findFileTab(file);
		if (existing != null) {
			tabPane.getSelectionModel().select(existing);
			return;
		}

		if (!Files.exists(file)) {
			alert(String.format("File '%s' could not be found.", file));
			return;
		}

		Watch watch = new Watch(file, Processor.find(file), this::reloadFile);
		FileTab tab = new FileTab(watch, file);
		tab.setOnClosed(e -> watch.close());
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tab);

		Settings.INSTANCE.pickFileWatch(file);
		tab.reload(); // initial content
		alert(String.format("Now watching for changes on '%s'", file));
	}

	private void alert(String text) {
		msg.setText(text);
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> msg.setText("")));
		timeline.play();
	}

	@SuppressWarnings("unchecked")
	<T> Stream<T> findTab(Class<T> clazz) {
		return (Stream<T>) tabPane.getTabs().stream().filter(it -> clazz.isInstance(it));
	}

	HelpTab findHelpTab() {
		return findTab(HelpTab.class).findFirst().orElse(null);
	}

	FileTab findFileTab(Path file) {
		return findTab(FileTab.class).filter(it -> it.file.equals(file)).findFirst().orElse(null);
	}

	WebTab findWebTab(WebPage webPage) {
		return (WebTab) findTab(WebTab.class).filter(it -> it.webPage.equals(webPage)).findFirst().orElse(null);
	}

	public void closeAll() {
		tabPane.getTabs().stream().forEach(it -> {
			if (it.getOnClosed() != null) {
				it.getOnClosed().handle(null);
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buildMenu();
		Settings.INSTANCE.setWatch(new Watch(Settings.INSTANCE.getFile(), Processor.NO_PROCESSOR, (a, b) -> {
			Settings.INSTANCE.load();
			buildMenu();
		}));
	}

	void buildMenu() {
		ObservableList<MenuItem> items = favMenu.getItems();
		items.clear();

		MenuItem open = new MenuItem("Open...");
		open.setOnAction(e -> onOpen());
		items.add(open);
		MenuItem settings = new MenuItem("Add Web Page...");
		settings.setOnAction(e -> onAddWebPage());
		items.add(settings);

		items.add(new SeparatorMenuItem());

		int i = 1;
		for (Path it : Settings.INSTANCE.getFileWatches()) {
			MenuItem item = new MenuItem(String.format("_%s - %s", i, it.getFileName()));
			item.setOnAction(e -> openFile(it));
			items.add(item);
			i++;
		}

		boolean needsSep = !items.isEmpty();
		boolean hadWatches = false;
		for (WebPage it : Settings.INSTANCE.getWebPages()) {
			hadWatches = true;
			if (needsSep) {
				items.add(new SeparatorMenuItem());
			} else {
				needsSep = false;
			}
			MenuItem item = new MenuItem(String.format("%s", it.getUrl()));
			item.setOnAction(e -> addWebTab(it));
			items.add(item);
		}

		if (hadWatches) {
			items.add(new SeparatorMenuItem());
		}

		MenuItem help = new MenuItem("Help");
		help.setOnAction(e -> onHelp());
		items.add(help);

		items.add(new SeparatorMenuItem());
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(e -> Platform.exit());
		items.add(exit);
	}

	private void onAddWebPage() {
		try {
			WebPage page = new WebPage();
			page.setUrl("http://localhost:8000/index.html");
			page.setFile(Arrays.asList("d:\\data\\myproj\\index.html", "d:\\data\\myproj\\main.css"));
			Settings.INSTANCE.addWebPage(page);
			buildMenu();
			Desktop.getDesktop().open(Settings.INSTANCE.getFile().toFile());
		} catch (Exception e1) {
		}
	}

	private void openFile(Path file) {
		addFileTab(file);
		buildMenu();
	}

	public void onHelp() {
		HelpTab tab = findHelpTab();
		if (tab == null) {
			tab = new HelpTab();
			tabPane.getTabs().add(tab);
		}
		tabPane.getSelectionModel().select(tab);
	}

	public void onTop() {
		getStage().setAlwaysOnTop(onTop.isSelected());
	}

	public void onOpenFavs() {
		favMenu.show();
	}
}
