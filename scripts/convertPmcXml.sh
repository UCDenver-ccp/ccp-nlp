#!/bin/bash

# Given an input directory containing nxml files, this script will output a plain text version of the document as well as a file containing document zone annotations.

function print_usage {
echo "Usage:"
echo "$(basename $0) [OPTIONS]"
echo "  [-i <nxml file or directory>]: Path to the NXML file or directory to process."
echo "  [-s <num to skip>]: OPTIONAL. Number of files to skip prior to processing. Default=0"
echo "  [-n <num to process>]: OPTIONAL. Number of files to process (after any that are skipped). Default = -1 (process all)."
echo "  [-o <maven offline mode>]: OPTIONAL. If set, runs Maven in its offline mode."
echo "  [-m <maven binary>]: OPTIONAL. If set, this is used as the mvn command. By default, the script assumes mvn is on your path."


}

SKIP=0
NUM_TO_PROCESS=-1
MAVEN_OFFLINE=""
MAVEN_BIN=mvn

while getopts "i:n:s:m:o" OPTION; do
case $OPTION in
# The input ontology file
i) NXML_DIR=$OPTARG
;;
# The number of files to skip prior to processing
s) SKIP=$OPTARG
;;
# The number of files to process (after skip)
n) NUM_TO_PROCESS=$OPTARG
;;
# If set, use the -o flag when running maven
o) MAVEN_OFFLINE=-o
;;
# If set, use the -o flag when running maven
m) MAVEN_BIN=$OPTARG
;;
# HELP!
h) print_usage; exit 0
;;
esac
done

if [[ -z $NXML_DIR ]]; then
echo "missing input arguments!!!!!"
print_usage
exit 1
fi

if ! [[ -e README.md ]]; then
echo "Please run from the root of the project."
exit 1
fi

$MAVEN_BIN $MAVEN_OFFLINE -e -f scripts/pom-convert-pmc-nxml.xml exec:exec \
-DnxmlDir=$NXML_DIR \
-Dskip=$SKIP \
-DnumToProcess=$NUM_TO_PROCESS