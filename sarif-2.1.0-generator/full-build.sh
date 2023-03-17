#!/bin/bash
# SPDX-License-Identifier: MIT

export GENERATOR_WORK_DIRECTORY=$(pwd)

export SARIF_VERSION="2.1.0"
# replace all . with _ :
export SARIF_VERSION_WITH_UNDERSCORES="${SARIF_VERSION//./_}"

export SARIF_FOLDER_NAME="sarif-$SARIF_VERSION"
export SARIF_GEN_FOLDER="$GENERATOR_WORK_DIRECTORY/gen/$SARIF_FOLDER_NAME"


function help_and_exit() {    
cat <<'USAGE'
# Please set the environment variables:

export SARIF_GENERATED_LIB_RELEASE_VERSION=<release-version>
# specify java version like (1.6, 1.8, 9, 11, etc) the generated library should be compatible with
export GEN_LIBRARY_JAVA_VERSION=<gen-library-java-version>

# Example:
export SARIF_GENERATED_LIB_RELEASE_VERSION="1.0.1"
export GEN_LIBRARY_JAVA_VERSION="1.8"

# Additional parameters:
--clean - cleans all former generated data
--help - shows this help
USAGE
exit 1
}

if [[ "$1" = "--help" ]]; then
    help_and_exit
fi

if [ -z "$SARIF_GENERATED_LIB_RELEASE_VERSION" ]
then
	help_and_exit
fi

if [ -z "$GEN_LIBRARY_JAVA_VERSION" ]
then
	export GEN_LIBRARY_JAVA_VERSION="1.8"
fi

if [[ "$1" = "--clean" ]]; then
    echo "CLEAN selected. Remove complete content of $SARIF_GEN_FOLDER" 
    rm -r $SARIF_GEN_FOLDER
fi

../gradlew generateJsonSchema2Pojo 

cd $GENERATOR_WORK_DIRECTORY/../shared
./copy-impl-into-generated.sh
./generate-build.gradle.sh

cd $GENERATOR_WORK_DIRECTORY

cd "$SARIF_GEN_FOLDER"


../../../gradlew build sourcesJar
