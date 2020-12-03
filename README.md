# SelfCommandPrompt
do you have a java command line program? then this library is for you. Simply call SelfCommandPrompt#runWithCMD for jars that handle 0 arguments with scanners as input or call SelfCommandPrompt#wrapWithCMD for strictly only command line jars. wrapWithCMD will returned the modified args by the user if any detected and enforce it's booted up with a command prompt terminal or jconsole depending on os and settings. Supports native terminals: windows, mac linux. Tested on (windows 10, mac osx 2016, linux(mint & ubuntu)

Technical:
- SelfCommandPrompt#runWithCMD opens a native command line terminal based on your os once the user double clicks the jar
- SelfCommandPrompt#wrapWithCMD opens a native command line terminal, gets user args(if any) by user input on user double clicking the jar

Disadvanteges:
- on jar double click the args are always 0 by default and user will have to input the entrie commmand each time. To get around this you could load a config on initial boot to fix the args and save them somewhere.

This program is free of charge and open source. On top of that you are also allowed to embedd this into your application programs but, I ask that you update when bugs are fixed
