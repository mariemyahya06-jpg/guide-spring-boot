#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper (only-script) pour Linux / macOS
# Usage : ./mvnw spring-boot:run
# ----------------------------------------------------------------------------
set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
PROPS="$SCRIPT_DIR/.mvn/wrapper/maven-wrapper.properties"

[ -f "$PROPS" ] || { echo "[ERREUR] Fichier introuvable : $PROPS"; exit 1; }

DISTRIBUTION_URL=$(grep '^distributionUrl=' "$PROPS" | cut -d'=' -f2- | tr -d '\r')
[ -n "$DISTRIBUTION_URL" ] || { echo "[ERREUR] distributionUrl manquant"; exit 1; }

ZIP_NAME=$(basename "$DISTRIBUTION_URL")
MAVEN_BASENAME=$(printf '%s' "$ZIP_NAME" | sed 's/-bin\.zip$//')

WRAPPER_DIR="$HOME/.m2/wrapper/dists/$MAVEN_BASENAME"
MVN="$WRAPPER_DIR/$MAVEN_BASENAME/bin/mvn"

if [ ! -x "$MVN" ]; then
  echo "Telechargement de Maven : $DISTRIBUTION_URL"
  mkdir -p "$WRAPPER_DIR"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL -o "$WRAPPER_DIR/maven.zip" "$DISTRIBUTION_URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -q -O "$WRAPPER_DIR/maven.zip" "$DISTRIBUTION_URL"
  else
    echo "[ERREUR] curl ou wget requis."; exit 1
  fi
  unzip -q "$WRAPPER_DIR/maven.zip" -d "$WRAPPER_DIR"
fi

exec "$MVN" "$@"
