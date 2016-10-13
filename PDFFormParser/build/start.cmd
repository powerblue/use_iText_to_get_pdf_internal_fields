@Echo off
rem SET JAVA_HOME=C:\Java\jdk1.7.0_67
SET CLASSPATH=.;PDFFormParser-16.10.13.1.jar;commons-logging-1.2.jar;fontbox-2.0.0.jar;itextpdf-5.5.9.jar;pdfbox-2.0.0.jar;slf4j-api-1.7.5.jar;slf4j-simple-1.7.5.jar
rem ECHO start with JAVA_HOME: %JAVA_HOME%
rem java -version

java fr.jp.pdf.FormParser %1 %2
