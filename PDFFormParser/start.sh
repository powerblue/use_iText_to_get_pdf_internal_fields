#!/bin/bash

AA=.
CLASSPATH=
CLASSPATH=.:$AA/@classpath@
export CLASSPATH
#echo $CLASSPATH
java -cp $CLASSPATH fr.jp.pdf.FormParser "$1" "$2"