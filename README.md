# OpenTerminal
do you have a java command line program? then this library is for you. Simply call `OpenTerminal#INSTANCE#run(TerminalApp app)`. Then it opens a native command prompt terminal via double click based on your current os to execute your jar file like you just launched it with a batch file. for jars that handle 0 arguments create use `TerminalAppWrapper` as the parameter

Screenshots(double click jar):
<img width="1280" alt="Screen_Shot_2021-07-18_at_9 11 00_PM" src="https://user-images.githubusercontent.com/9736369/127045068-1477cc30-8b73-48e4-8b00-92e0e8062e24.png">
![Screenshot_from_2021-07-18_21-42-00](https://user-images.githubusercontent.com/9736369/127045071-e1a7e3ab-6ad9-48f7-907c-bc82e578d81b.png)
![Screenshot_from_2021-07-18_21-39-00](https://user-images.githubusercontent.com/9736369/127045075-2a8cfc43-bb7e-4f9c-aa7f-592e8b9930cc.png)

Features:
- `user.appdata` is now a System property
- `user.dir`, `user.home`, `java.io.tmpdir`, `user.appdata` can all be changed before calling `OpenTerminal#run`
- `JREUtil#syncUserDirWithJar` will sync the user dir with jar #call before `OpenTerminal#run`
- `TerminalApp#shouldPause` #this option will not pause with `System#exit`
- `TerminalApp#hardPause` #this option will pause with `System#exit`
- `TerminalAppWrapper` #this option will get user input args before executing your program, custom wrappers can return custom arugments with or without getting user input
- users should parse their config options for the TerminalApp during `TerminalApp#isLaunching` returns true
- users should parse their program config after OpenTerminal#run has been called or checking if `TerminalApp#canExe`

# SelfCommandPrompt(Legacy)
Note: SelfCommandPrompt is now getting replaced with OpenTerminal to fix unfixable issues with the SelfCommandPrompt's current design. It's still avalible for download in the release page

Features:
- SelfCommandPrompt#runWithCMD opens a native command line terminal based on your os once the user double clicks the jar
- SelfCommandPrompt#wrapWithCMD opens a native command line terminal, gets user args(if any) before executing a strictly command line jar that doesn't handle 0 args with a scanner.

Disadvanteges:
- on jar double click the args are always 0. In the future it is planned to edit the terminal's text field so users can save their last command and input it in on startup.
This library is free of charge and open source. On top of that you are also allowed to embedd this into your application programs but, I ask that you update when bugs are fixed
