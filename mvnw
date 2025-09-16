#!/bin/sh

# Simple Maven Wrapper script
MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-.}
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

# Find Java command
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
else
    JAVACMD=`which java`
fi

if [ ! -x "$JAVACMD" ] ; then
    echo "Error: JAVA_HOME is not set and no 'java' command could be found in your PATH."
    echo "Please set the JAVA_HOME variable in your environment to match the location of your Java installation."
    exit 1
fi

# Download wrapper jar if it doesn't exist
if [ ! -r "$WRAPPER_JAR" ]; then
    echo "Downloading Maven Wrapper..."
    mkdir -p .mvn/wrapper
    curl -o "$WRAPPER_JAR" "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"
fi

# Run Maven
exec "$JAVACMD" \
    -classpath "$WRAPPER_JAR" \
    "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
    $WRAPPER_LAUNCHER "$@"
