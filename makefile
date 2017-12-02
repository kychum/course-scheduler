# Basic makefile

SOURCES=$(shell find src -name "*.java")
TESTSOURCES=$(shell find test -name "*.java")
TESTJARS="test/jars/junit-jupiter-api-5.1.0-M1.jar:test/jars/apiguardian-api-1.0.0.jar:test/jars/junit-jupiter-params-5.1.0-M1.jar"

all:
	mkdir -p bin
	javac $(SOURCES) -d "bin" -cp "bin"

test: all
	mkdir -p test/bin
	javac $(TESTSOURCES) -d "test/bin" -cp "$(TESTJARS):bin"

release: all
	jar cfve Scheduler.jar Main bin

clean:
	rm -rf bin
	rm -rf test/bin
	rm -f Scheduler.jar

