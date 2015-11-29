## Settings
The settings file [settings.txt](javascript:fxreloadHelp.openSettings()) is located here:
* Windows: `%USERPROFILE%\.fxreload\settings.txt`
* Mac/Linux: `$HOME/.fxreload/settings.txt`

###Documentation
```
<fxReload>
	<!--
		Watch a file. 
		If there's a known processor for the file such as Markdown or Asciidoc, 
	    the file is processed and the tab shows the HTML output of the processor.
		For any other file, the file itself is shown in the tab.
	-->
    <file>D:\data\myproj\README.md</file>
    <file>D:\data\myproj\README.adoc</file>
    <file>D:\data\myproj\test.html</file>
	
	<!--
		Watch for many files and reload a web page.
	-->
    <webPage>
    	<!-- when any of these changes... -->
        <file>D:\data\myproj\test.html</file>
        <file>D:\data\myproj\test.css</file>
        
        <!-- ...reload this site -->           
        <url>http://localhost:8000/myproj/test.html</url>
    </webPage>
</fxReload>
```

For each `<file>` and `<webPage>` element, an entry is created in the `Open` drop down menu.
