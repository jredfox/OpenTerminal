# OpenTerminal
do you have a java command line program? then this library is for you. Simply call SelfCommandPrompt#runWithCMD for a program that handles 0 args or SelfCommandPrompt#wrapWithCMD to get wrapped args before executing. Tested on (windows 10, mac osx 2016, linux(mint & ubuntu)

Screenshots:
<img width="1280" alt="Screen_Shot_2021-07-18_at_9 11 00_PM" src="https://user-images.githubusercontent.com/9736369/127045068-1477cc30-8b73-48e4-8b00-92e0e8062e24.png">
![Screenshot_from_2021-07-18_21-42-00](https://user-images.githubusercontent.com/9736369/127045071-e1a7e3ab-6ad9-48f7-907c-bc82e578d81b.png)
![Screenshot_from_2021-07-18_21-39-00](https://user-images.githubusercontent.com/9736369/127045075-2a8cfc43-bb7e-4f9c-aa7f-592e8b9930cc.png)


# SelfCommandPrompt(Legacy)
Note: SelfCommandPrompt is now getting replaced with OpenTerminal to fix unfixable issues with the SelfCommandPrompt's current design. It's still avalible for download in the release page

Features:
- SelfCommandPrompt#runWithCMD opens a native command line terminal based on your os once the user double clicks the jar
- SelfCommandPrompt#wrapWithCMD opens a native command line terminal, gets user args(if any) before executing a strictly command line jar that doesn't handle 0 args with a scanner.

Disadvanteges:
- on jar double click the args are always 0. In the future it is planned to edit the terminal's text field so users can save their last command and input it in on startup.
This library is free of charge and open source. On top of that you are also allowed to embedd this into your application programs but, I ask that you update when bugs are fixed
