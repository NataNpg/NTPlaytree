#!/usr/bin/env sh
# Lightweight Gradle bootstrapper for this source package.
set -eu
GRADLE_VERSION="8.11.1"
GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
INSTALL_DIR="$GRADLE_USER_HOME/ntplaytree-gradle/gradle-$GRADLE_VERSION"
if [ ! -x "$INSTALL_DIR/bin/gradle" ]; then
  mkdir -p "$GRADLE_USER_HOME/ntplaytree-gradle"
  ZIP="$GRADLE_USER_HOME/ntplaytree-gradle/gradle-$GRADLE_VERSION-bin.zip"
  if [ ! -f "$ZIP" ]; then
    if command -v curl >/dev/null 2>&1; then
      curl -fL "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -o "$ZIP"
    elif command -v wget >/dev/null 2>&1; then
      wget "https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip" -O "$ZIP"
    else
      echo "Install Gradle $GRADLE_VERSION or install curl/wget to download it." >&2
      exit 1
    fi
  fi
  if command -v unzip >/dev/null 2>&1; then
    unzip -q -o "$ZIP" -d "$GRADLE_USER_HOME/ntplaytree-gradle"
  else
    echo "The unzip command is required to install Gradle." >&2
    exit 1
  fi
fi
exec "$INSTALL_DIR/bin/gradle" "$@"
