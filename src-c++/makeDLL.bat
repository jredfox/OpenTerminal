g++ -c -I "%JAVA_HOME%\include" -I "%JAVA_HOME%\include\win32" jmln_PID.cpp -o jmln_PID.o
g++ -shared -o native.dll jmln_PID.o -Wl,--add-stdcall-alias