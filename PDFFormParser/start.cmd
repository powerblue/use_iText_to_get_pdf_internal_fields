@Echo off
rem SET JAVA_HOME=C:\Java\jdk1.7.0_67
SET CLASSPATH=.;@classpath@
rem ECHO start with JAVA_HOME: %JAVA_HOME%
rem java -version

java fr.jp.pdf.FormParser %1 %2
