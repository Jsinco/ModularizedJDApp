#!/bin/bash

projectName="%PLACEHOLDER%"
projectVersion="%PLACEHOLDER%"

JAR_NAME="$projectName-$projectVersion.jar"

# Find the process ID (PID) of the running Java application
PID=$(pgrep -f $JAR_NAME)

if [ -z "$PID" ]; then
    echo "No running process found for $JAR_NAME."
else
    echo "Killing process $PID..."
    kill -9 "$PID"

    # Wait for the process to exit
    while kill -0 "$PID" 2>/dev/null; do
        sleep 1
    done
fi

echo "Starting $JAR_NAME..."
nohup java -jar $JAR_NAME &