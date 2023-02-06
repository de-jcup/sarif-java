#!/bin/bash

# SPDX-License-Identifier: MIT

function help_and_exit() {    
cat <<'USAGE'
# Please set the environment variables:

export SARIF_GENERATED_LIB_RELEASE_VERSION=<release-version>
# specify java version like (1.6, 1.8, 9, 11, etc) the generated library should be compatible with
export GEN_LIBRARY_JAVA_VERSION=<gen-library-java-version>

# Example:
export SARIF_GENERATED_LIB_RELEASE_VERSION="0.1"
export GEN_LIBRARY_JAVA_VERSION="11"
USAGE
exit 1
}

if [ -z "$SARIF_GENERATED_LIB_RELEASE_VERSION" ]
then
	help_and_exit
fi

if [ -z "$GEN_LIBRARY_JAVA_VERSION" ]
then
	help_and_exit
fi

../gradlew generateJsonSchema2Pojo

./gen-build-gradle-file.sh

cd gen/sarif-210

../../../gradlew build sourcesJar
