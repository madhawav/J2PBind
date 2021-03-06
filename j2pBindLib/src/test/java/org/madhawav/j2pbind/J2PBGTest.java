package org.madhawav.j2pbind;

import org.madhawav.j2pbind.Config;
import org.madhawav.j2pbind.J2PBG;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class J2PBGTest {
    @org.junit.BeforeClass
    public static void setup() {

    }

    @org.junit.Test
    public void generateSourceFilesAndCompile() throws IOException, ClassNotFoundException, InterruptedException {
        // Copy sample class to output directory and compile
        copyResourceToFile("SampleCode/TestClass.java", "output/java/test/TestClass.java");
        compileJavaFile(new File("output/java/test/TestClass.java"), new String[] {});

        // Generate bindings
        Class testClass = loadClass(new File("output/java/"), "test.TestClass");
        Config config = new Config("output");
        config.getCompilerClassPaths().add(new File("output/java").getAbsolutePath());
        config.addClass(testClass);

        J2PBG j2PBG = new J2PBG(config);
        j2PBG.generateSourceFiles();
        j2PBG.compileJavaFiles();
        j2PBG.generateLoader();

        // Copy and run the test python app
        copyResourceToFile("SampleCode/TestApp.py", "output/python/TestApp.py");

        Process p = Runtime.getRuntime().exec("python3 TestApp.py", null, new File("output/python"));
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        // read the output from the command
        System.out.println("Standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        System.out.println();
        System.out.println();

        // read the error from the command
        System.out.println("Standard error of the command (This should be blank):");
        s = null;
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        // We need a 0 exit code if everything goes well
        assertEquals(0, p.waitFor());

    }

    @org.junit.Test
    public void acceptAndReturnTest() throws IOException, ClassNotFoundException, InterruptedException {
        // Copy sample class to output directory and compile
        copyResourceToFile("SampleCode/AcceptAndReturnTest.java", "output/java/test/AcceptAndReturnTest.java");
        compileJavaFile(new File("output/java/test/AcceptAndReturnTest.java"), new String[] {});

        // Generate bindings
        Class testClass = loadClass(new File("output/java/"), "test.AcceptAndReturnTest");
        Config config = new Config("output");
        config.getCompilerClassPaths().add(new File("output/java").getAbsolutePath());
        config.addClass(testClass);
        config.addClass(String.class);
        config.addClass(Integer.class);
        config.addClass(Object.class);

        J2PBG j2PBG = new J2PBG(config);
        j2PBG.generateSourceFiles();
        j2PBG.compileJavaFiles();
        j2PBG.generateLoader();

        // Copy and run the test python app
        copyResourceToFile("SampleCode/AcceptAndReturnTest.py", "output/python/AcceptAndReturnTest.py");

        Process p = Runtime.getRuntime().exec("python3 AcceptAndReturnTest.py", null, new File("output/python"));
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        // read the output from the command
        System.out.println("Standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        System.out.println();
        System.out.println();

        // read the error from the command
        System.out.println("Standard error of the command (This should be blank):");
        s = null;
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        // We need a 0 exit code if everything goes well
        assertEquals(0, p.waitFor());

    }

    @org.junit.Test
    public void InterfaceTest() throws IOException, ClassNotFoundException, InterruptedException {
        String[] additionalCP = { new File("output/java").getAbsolutePath() };
        // Copy sample class to output directory and compile
        copyResourceToFile("SampleCode/SampleInterface.java", "output/java/test/SampleInterface.java");
        copyResourceToFile("SampleCode/InterfaceTest.java", "output/java/test/InterfaceTest.java");

        compileJavaFile(new File("output/java/test/SampleInterface.java"), additionalCP);
        compileJavaFile(new File("output/java/test/InterfaceTest.java"), additionalCP);


        // Generate bindings
        Class testClass = loadClass(new File("output/java/"), "test.InterfaceTest");
        Class sampleInterface = loadClass(new File("output/java/"), "test.SampleInterface");

        Config config = new Config("output");
        config.getCompilerClassPaths().add(new File("output/java").getAbsolutePath());
        config.addClass(testClass);
        config.addClass(sampleInterface);


        J2PBG j2PBG = new J2PBG(config);
        j2PBG.generateSourceFiles();
        j2PBG.compileJavaFiles();
        j2PBG.generateLoader();

        // Copy and run the test python app
        copyResourceToFile("SampleCode/InterfaceTest.py", "output/python/InterfaceTest.py");

        Process p = Runtime.getRuntime().exec("python3 InterfaceTest.py", null, new File("output/python"));
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(p.getErrorStream()));

        // read the output from the command
        System.out.println("Standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        System.out.println();
        System.out.println();

        // read the error from the command
        System.out.println("Standard error of the command (This should be blank):");
        s = null;
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        // We need a 0 exit code if everything goes well
        assertEquals(0, p.waitFor());

    }

    private static Class loadClass(File classPath, String className) throws MalformedURLException, ClassNotFoundException {
        //Code adapted from https://stackoverflow.com/questions/6219829/method-to-dynamically-load-java-class-files

        // Convert File to a URL
        URL url = classPath.toURI().toURL();
        URL[] urls = new URL[]{url};

        // Create a new class loader with the directory
        ClassLoader cl = new URLClassLoader(urls);

        // Load in the class; MyClass.class should be located in
        // the directory file:/c:/myclasses/com/mycompany
        Class cls = cl.loadClass(className);
        return cls;
    }

    private static void compileJavaFile(File file, String[] additionalClassPaths) {
        String additionalClasses = "";
        List<String> optionList = new ArrayList<String>();
        for(String classPath : additionalClassPaths){
            additionalClasses += classPath + ":";
        }
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path") + ":" + additionalClasses);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> compilationUnits1 =
                fileManager.getJavaFileObjectsFromFiles(Collections.singletonList(file));
        compiler.getTask(null, fileManager, null, optionList, null, compilationUnits1).call();

    }

    private static void copyResourceToFile(String resourcePath, String filePath) {
        ClassLoader classLoader = J2PBGTest.class.getClassLoader();
        File file = new File(classLoader.getResource(resourcePath).getFile());
        File target = new File(filePath);
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        target.getParentFile().mkdirs();
        try {
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(target);
            while (fileInputStream.available() > 0) {
                fileOutputStream.write(fileInputStream.read());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

