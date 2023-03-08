#!/bin/bash

# SPDX-License-Identifier: MIT

GENERATED_DIRECTORY=gen/sarif-210/

if [ ! -d "$GENERATED_DIRECTORY" ]
then
	mkdir -p "$GENERATED_DIRECTORY"
fi

cat <<EOF > gen/sarif-210/build.gradle
// Use the java plugin 
apply plugin: 'java'

buildscript {
  apply from: "../../libraries.gradle"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation library.jackson_databind
}

jar() {
	archiveFileName = "sarif210-$SARIF_GENERATED_LIB_RELEASE_VERSION.jar"
}

task sourcesJar(type: Jar, dependsOn: classes) {
    classifier = 'sources'
    archiveFileName = "sarif210-$SARIF_GENERATED_LIB_RELEASE_VERSION-sources.jar"
    from sourceSets.main.allSource
}
EOF