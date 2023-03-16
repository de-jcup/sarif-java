#!/bin/bash 

function show_copy_help_and_exit {
    cat <<'USAGE'
# Please set the environment variables:

export SARIF_VERSION=<sarif-version>

# Example:
export SARIF_VERSION="2.1.0"
USAGE
    exit 1
}

if [ -z "$SARIF_VERSION" ]
then
    show_copy_help_and_exit
fi

cp -r $GENERATOR_WORK_DIRECTORY/impl/sarif-$SARIF_VERSION/src $SARIF_GEN_FOLDER