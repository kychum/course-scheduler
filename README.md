course-scheduler
================

CPSC 433 Search system for course scheduling

Included in the package are:
	* source .java files for the searching program (in ./src)
	* precompiled .class files for running the program (in ./bin)
	* packaged .jar file as an alternative for the class files (in ./Scheduler.jar)
	* configuration file for the search (./config.ini)
	* solution for deptinst1 (./deptinst1-solution.txt)
	* solution for deptinst2 (./deptinst2-solution.txt)

The solutions for the two instances were created with weights of 1 and penalties of 1 for all parameters.


Compiling the program
---------------------

To compile the program, run the Java compiler on all .java files in the ./src directory, e.g.
```
  javac `find src -name "*.java"` -d "bin"
```


Running the program
-------------------

To run the program via the command-line, run
```
  java -jar Scheduler.jar <path/to/input.file>
```
or
```
  java -cp bin Main <path/to/input.file>
```

If running the compiled .class files, please ensure that 'config.ini' is in the current working directory

The search program will try to solve the given instance and output its results to stdout. If logging is enabled, extra information is also printed. Should the program be terminated while it is running (e.g. program interrupt via Ctrl+C), it will try to print out the last-known best schedule before terminating.


Configuring the program
-----------------------

To configure parameters for the search (and other options), edit config.ini. Comments are in the file describing the settings.

