package fxreload;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	static final boolean DEBUG = false;

	@Override
	public void stop() throws Exception {
		super.stop();
		Settings.INSTANCE.close();
	}

	@Override
	public void start(Stage stage) {
		try {
			stage.setTitle("fxreload");
			stage.getIcons().add(new Image(getClass().getResourceAsStream("dn3d.png")));
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
			BorderPane root = fxmlLoader.load();
			Scene scene = new Scene(root, 1024, 768);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			MainController controller = fxmlLoader.getController();
			stage.setOnCloseRequest(e -> {
				controller.closeAll();
			});
			installKeys(scene, controller);
			installFileDnD(scene, controller);
			stage.setScene(scene);

			Processor.preload();

			stage.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static final KeyCombination KEY_OPEN = KeyCombination.keyCombination("SHORTCUT+O");
	static final KeyCombination KEY_CLOSE = KeyCombination.keyCombination("SHORTCUT+W");
	static final KeyCombination KEY_HISTORY = KeyCombination.keyCombination("SHORTCUT+H");
	static final List<KeyCode> ALL_DIGITS = Arrays.asList(KeyCode.DIGIT1, KeyCode.DIGIT2, KeyCode.DIGIT3, KeyCode.DIGIT4, KeyCode.DIGIT5, KeyCode.DIGIT6, KeyCode.DIGIT7, KeyCode.DIGIT8,
			KeyCode.DIGIT9, KeyCode.DIGIT0);

	private void installKeys(Scene scene, MainController controller) {
		scene.setOnKeyPressed(e -> {
			if (KEY_OPEN.match(e)) {
				controller.onOpen();
				e.consume();
			} else if (KEY_CLOSE.match(e)) {
				controller.onClose();
				e.consume();
			} else if (KEY_HISTORY.match(e)) {
				controller.onOpenFavs();
				e.consume();
			} else if (controller.favMenu.isShowing()) {
				KeyCode code = e.getCode();
				int index = ALL_DIGITS.indexOf(code);
				if (index >= 0) {
					List<Path> favs = Settings.INSTANCE.getFileWatches();
					if (index < favs.size()) {
						Path path = favs.get(index);
						controller.addFileTab(path);
						controller.favMenu.hide();
						e.consume();
					}
				}
			}
		});
	}

	private void installFileDnD(Scene scene, MainController controller) {
		scene.setOnDragOver(event -> {
			Dragboard db = event.getDragboard();
			if (db.hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY);
			} else {
				event.consume();
			}
		});
		scene.setOnDragDropped(e -> {
			Dragboard db = e.getDragboard();
			boolean success;
			if (success = db.hasFiles()) {
				for (File file : db.getFiles()) {
					controller.addFileTab(file.toPath());
				}
			}
			e.setDropCompleted(success);
			e.consume();
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
