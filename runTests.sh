#!/bin/bash
JUNIT_CMD="java -jar test/jars/junit-platform-console-standalone-1.1.0-M1.jar --class-path test/bin:bin"

if [ $# -gt 0 ]; then
  $JUNIT_CMD $@
else
  $JUNIT_CMD --scan-class-path
fi
