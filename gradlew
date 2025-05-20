#!/bin/sh
# --------- Gradle Wrapper bootstrap script ---------
# This is a minimal version for Unix systems.
# For full features, generate via 'gradle wrapper' after installing Gradle.

DIR="$(cd "$(dirname "$0")" && pwd)"

# Set default Gradle version
GRADLE_VERSION=8.7

# Download wrapper JAR if not present
WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_PROPERTIES="$DIR/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Downloading Gradle Wrapper JAR..."
  mkdir -p "$DIR/gradle/wrapper"
  curl -fsSL -o "$WRAPPER_JAR" "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  unzip -j "$WRAPPER_JAR" gradle-$GRADLE_VERSION/bin/gradle-wrapper.jar -d "$DIR/gradle/wrapper" || true
fi

# Run the wrapper
java -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
