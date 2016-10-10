# The PDF Form Parser.
this maven's java project

## Contents:
--------------
build/                     - prepared for standalone executions the current build a "PDF FormParser console app"
lib/                       - lib for integrate ANT & MAVEN, uses in "build-InStandaloneEnv.xml"
src/                       - java sources Project (see pom.xml)
build-InStandaloneEnv.xml  - ANT build file for assembly the app with all required libs and prepared start.sh & start.cmd bath scripts;
pom.xml                    - main maven project's POM file. Open projects from this file! Build the all subProjects from this file;
Readme.md                  - this file;
start.cmd                  - template bath script for MS Windows OS, uses in "build-InStandaloneEnv.xml"
start.sh                   - template bath script for *Nix OS [bash], uses in "build-InStandaloneEnv.xml"

## Build using Intellij:
--------------
If using IntelliJ (set to Java Level 8):

1. Download the .zip from GitHub.
2. IntelliJ > open project > PDFFormParser.
3. IntelliJ > Build > Make.
4. Now get inside the build dir and run the following, this will read the internal fields in form.pdf and output them to form.txt

./start.sh -LIST_FIELDS form.pdf 2>form.txt

## Build:
--------------
This is a maven project. 
Need make a build in this directory.

For build all modules:
>mvn clean package -Dmaven.test.skip=true

For build all modules & assembly the stanalone RC-consoleapp in folder "build/":
>ant -f build-InStandaloneEnv.xml assemble-build


## Run:
> cd build
### Windows
> start.cmd -LIST_FIELDS form_1.pdf 2>form_1.txt 
### *NIX
> start.sh -LIST_FIELDS form_1.pdf 2>form_1.txt 
