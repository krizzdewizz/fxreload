package fxreload;

import java.nio.file.Path;

import javafx.scene.control.Tooltip;

public class FileTab extends ATab {
	public final Path file;

	public FileTab(Watch watch, Path file) {
		super(watch);
		this.file = file;
		setText(file.getFileName().toString());
		setTooltip(new Tooltip(file.toAbsolutePath().toString()));
	}
}
