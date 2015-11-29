package fxreload;

import java.io.Closeable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javafx.application.Platform;

public class Watch implements Closeable {

	private class WatchThread extends Thread {

		public WatchThread() {
		}

		@Override
		public void run() {

			long t0 = System.currentTimeMillis();

			try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
				Path folder = file.getParent();
				/* final WatchKey watchKey = */ folder.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
				setName(String.format("Watch(%s)", folder));
				while (true) {
					final WatchKey wk = watchService.take();
					for (WatchEvent<?> event : wk.pollEvents()) {
						final Path changed = (Path) event.context();
						if (folder.resolve(changed).equals(file)) {

							long t1 = System.currentTimeMillis();
							long diff = t1 - t0;
							if (diff > 20) {
								// event sometimes occurs twice
								if (Main.DEBUG) {
									System.out.println("changed: " + file);
								}
								Platform.runLater(Watch.this::notifyChanged);
							}

							t0 = t1;
							break;
						}
					}
					// reset the key
					/* boolean valid = */ wk.reset();
					// if (!valid) {
					// System.out.println("Key has been unregistered");
					// }
				}
			} catch (Exception e) {
				if (!(e instanceof InterruptedException)) {
					e.printStackTrace();
				}
			}
		}
	}

	interface ChangedHandler {
		void changed(Path file, String processedContent);
	}

	private Processor processor;
	private ChangedHandler onChanged;
	Path file;
	private WatchThread thread;

	public Watch(Path file, Processor processor, ChangedHandler onChanged) {
		this.file = file;
		this.processor = processor;
		this.onChanged = onChanged;
		setupWatchService();
	}

	private void setupWatchService() {
		this.thread = new WatchThread();
		this.thread.start();
	}

	void notifyChanged() {
		onChanged.changed(file, processFile());
	}

	private String processFile() {
		try {
			if (Files.exists(file) && processor.needsContent()) {
				int c = 0;
				while (true) {
					if (c > 10 || Files.isWritable(file)) {
						break;
					}
					System.out.println("file seems to be locked. retry in 100ms.");
					Thread.sleep(100);
					c++;
				}
			}
			return processor.process(processor.needsContent() ? new String(Files.readAllBytes(file), "UTF-8") : "");
		} catch (Exception e) {
			throw new RuntimeException(String.format("error while processing file '%s': %s", file, e), e);
		}
	}

	@Override
	public void close() {
		try {
			thread.interrupt();
		} catch (Exception e) {
			// ignore
		}
	}
}
