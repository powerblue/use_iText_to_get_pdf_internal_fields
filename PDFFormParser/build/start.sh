#!/bin/bash

AA=.
CLASSPATH=
CLASSPATH=.:$AA/PDFFormParser-16.10.11.3.jar:$AA/commons-logging-1.2.jar:$AA/fontbox-2.0.0.jar:$AA/pdfbox-2.0.0.jar:$AA/slf4j-api-1.7.5.jar:$AA/slf4j-simple-1.7.5.jar
export CLASSPATH
#echo $CLASSPATH
java -cp $CLASSPATH fr.jp.pdf.FormParser "$1" "$2"