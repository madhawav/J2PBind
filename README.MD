# Java to Python Bind Generator (J2PBG)

Generate Python bindings (wrappers) on Java Classes. 
* Generates Intellisense friendly Python bindings on Java Classes.
* Bindings are based on Pyjnius.
* Compatible with Python 3.
* Supports overriding of Java Classes from Python.
  - Callbacks to Python from Java.

## How to run?
The _j2pBindCLI_ can be used to generate Python bindings using java .class files.
```
java -jar j2pBindCLI-0.1.0-SNAPSHOT-jar-with-dependencies.jar [path to root directory of compiled classes] [output path]
```
*Note: First follow the instructions in `How to Install` section.

## Example Code
This example code extends a Java Class using a Python Class. 

1) Create a directory for example code. (say `example_code_directory`)
2) Create the subdirectory structure `example_code_directory/java/test`.
3) Copy [TestClass.java](j2pBindLib/src/test/resources/SampleCode/TestClass.java) to `example_code_directory/java/test`. 
   - Note that _TestClass_ is a Java Class belonging to package _test_.
   - It is an abstract class with abstract methods.  
4) Compile TestClass.java. `javac TestClass.java`
5) Navigate to `source_directory/j2pBindCLI/target` and run `java -jar j2pBindCLI-0.1.0-SNAPSHOT-jar-with-dependencies.jar example_code_directory/java example_code_directory`.
   - Now you have python bindings generated for _TestClass.java_.
6) Copy [TestApp.py](j2pBindLib/src/test/resources/SampleCode/TestApp.py) to `example_code_directory/python/`.
   - TestApp.py has class _TestClassImpl_ which extends _TestClass.java_.
   - It overrides some of the methods of the extended Java class.
7) Run `python3 TestApp.py`
   - This executes a sequence of method calls with print lines explaining the control flow.
 

## How to Install?
1) Install dependencies
   - Java 8.0+ with maven
   - Python 3.5
   - Cython `sudo pip3 install cython`
   - Pyjnius `sudo pip3 install pyjnius` 
2) Clone the project and navigate to source directory
3) Run `mvn clean install`
4) If the build succeeds, _j2pBindCLI_ would be located at j2pBindCLI/target directory.

## Future Work
 - Extending Interfaces Support
 - Static functions support
 - Fields support
 - Maven binding generator plugin