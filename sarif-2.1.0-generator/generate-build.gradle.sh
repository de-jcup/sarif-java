#!/bin/bash
# SPDX-License-Identifier: MIT

if [ ! -d "$SARIF_GEN_FOLDER_NAME" ]
then
	mkdir -p "$SARIF_GEN_FOLDER_NAME"
fi

# read the template file, replace content with environment variables
# and generate a build gradle file
eval "cat <<EOF
$(<template-build.gradle)
EOF
" > $SARIF_GEN_FOLDER_NAME/build.gradle
