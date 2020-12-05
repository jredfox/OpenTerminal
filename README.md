# SelfCommandPrompt
do you have a java command line program? then this library is for you. Simply call SelfCommandPrompt#runWithCMD for jars that handle 0 arguments with scanners as input or call SelfCommandPrompt#wrapWithCMD for strictly only command line jars. wrapWithCMD will returned the modified args by the user if any detected and enforce it's booted up with a command prompt terminal or jconsole depending on os and settings. Supports native terminals: windows, mac linux. Tested on (windows 10, mac osx 2016, linux(mint & ubuntu)

Technical:
- SelfCommandPrompt#runWithCMD opens a native command line terminal based on your os once the user double clicks the jar
- SelfCommandPrompt#wrapWithCMD opens a native command line terminal, gets user args(if any) before executing a strictly command line jar that doesn't handle 0 args with a scanner.

Disadvanteges:
- on jar double click the args are always 0 by default and user will have to input the entrie commmand each time. This particuallar issue isn't really fixable due to the java PrintStream api always enforcing the text is not editable. I may find a way around this but, it's an issue with the language itself rn. So I simply just can't do System.out.printInput(String text) since it doesn't exist yet.
- there is no tabbing function / keybinds fucntion yet. I have an idea of how to get it working but, it may require fixing the above issue first. This is due to java api not supporting keybind functions for console's and ides.

This program is free of charge and open source. On top of that you are also allowed to embedd this into your application programs but, I ask that you update when bugs are fixed
