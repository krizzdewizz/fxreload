# fxreload
JavaFX app that watches local files for changes, and displays their (processed) content in a web view.

* Watch a Markdown or Asciidoc file and display it's processed HTML output.
* Watch multiple files and reload a web page.

## Build

* cd into the project dir.
* run `install_deps` (the created jar `target/fxreload-1.0-SNAPSHOT-jar-with-dependencies.jar` is also used in Eclipse `.classpath`). 

## Run

`java -jar target/fxreload-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Development
* Import project into [e(fx)clipse](http://www.eclipse.org/efxclipse).