#!/bin/bash
# SPDX-License-Identifier: MIT

export SARIF_VERSION="2.1.0"
# replace all . with _ :
export SARIF_VERSION_WITH_UNDERSCORES="${SARIF_VERSION//./_}"

export SARIF_FOLDER_NAME="sarif-$SARIF_VERSION"
export SARIF_GEN_FOLDER_NAME="gen/$SARIF_FOLDER_NAME"

function help_and_exit() {    
cat <<'USAGE'
# Please set the environment variables:

export SARIF_GENERATED_LIB_RELEASE_VERSION=<release-version>
# specify java version like (1.6, 1.8, 9, 11, etc) the generated library should be compatible with
export GEN_LIBRARY_JAVA_VERSION=<gen-library-java-version>

# Example:
export SARIF_GENERATED_LIB_RELEASE_VERSION="1.0.1"
export GEN_LIBRARY_JAVA_VERSION="1.8"
USAGE
exit 1
}

if [ -z "$SARIF_GENERATED_LIB_RELEASE_VERSION" ]
then
	help_and_exit
fi

if [ -z "$GEN_LIBRARY_JAVA_VERSION" ]
then
	export GEN_LIBRARY_JAVA_VERSION="1.8"
fi

../gradlew generateJsonSchema2Pojo

./generate-build.gradle.sh

cd "$SARIF_GEN_FOLDER_NAME"

../../../gradlew build sourcesJar
