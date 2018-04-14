package org.madhawav.j2pbind;

import org.apache.commons.cli.*;
import org.madhawav.j2pbind.Config;
import org.madhawav.j2pbind.J2PBG;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * Command line tool for Java-2-Python Binding Generator
 */
public class J2PBindCLI {
    private static List<File> findClassFiles(File path) {
        if (path.isFile()) {
            if (path.getName().endsWith(".class")) {
                return Collections.singletonList(path);
            }
        } else {
            List<File> foundFiles = new ArrayList<>();
            for (File subFile : Objects.requireNonNull(path.listFiles())) {
                if (subFile != null)
                    foundFiles.addAll(findClassFiles(subFile));
            }
            return foundFiles;
        }
        return new ArrayList<>();
    }

    private static Class loadClass(File packageRootDirectory, File fileName) throws IOException, ClassNotFoundException {
        String remainingPath = fileName.getCanonicalFile().getAbsolutePath().substring(packageRootDirectory.getCanonicalFile().getAbsolutePath().length());
        remainingPath = remainingPath.substring(0, remainingPath.length() - ".class".length());
        while (remainingPath.startsWith("/"))
            remainingPath = remainingPath.substring(1);

        remainingPath = remainingPath.replace("/", ".");

        return loadClass(packageRootDirectory, remainingPath);
    }

    private static Class loadClass(File packageRoot, String className) throws MalformedURLException, ClassNotFoundException {
        //Code adapted from https://stackoverflow.com/questions/6219829/method-to-dynamically-load-java-class-files

        // Convert File to a URL
        URL url = packageRoot.toURI().toURL();
        URL[] urls = new URL[]{url};

        // Create a new class loader with the directory
        ClassLoader cl = new URLClassLoader(urls);

        // Load in the class; MyClass.class should be located in
        // the directory file:/c:/myclasses/com/mycompany
        Class cls = cl.loadClass(className);
        return cls;
    }

    public static void addCodeDirectory(Config config, File searchPath, File packageRoot) throws IOException, ClassNotFoundException {
        List<File> files = findClassFiles(searchPath);
        List<Class> foundClasses = new ArrayList<>();
        for (File file : files) {
            Class cls = loadClass(packageRoot, file);
            foundClasses.add(cls);
        }

        config.getCompilerClassPaths().add(packageRoot.getCanonicalFile().getAbsolutePath());
        config.getClasses().addAll(foundClasses);
    }

    private static String argFailString = "Invalid input.\nInput format [Source directory] [Output directory] [Options]\n" +
            "Options:\n\t\t -p\t\tSpecify package root\n\t\t -cp\tAdditional class paths\n\nFlags:\n\t\t-nocompile\t\t" +
            "Don't compile generated java files.\n\t\t-noloader\t\tDon't generate loader.py\n";

    public static void main(String[] args) throws IOException, ClassNotFoundException, ParseException {
        // Args
        Options options = new Options();

        options.addOption("p", true, "Package root directory");
        options.addOption("cp", true, "Additional class path");
        options.addOption("noloader", false, "Don't generate loader");
        options.addOption("nocompile", false, "Don't compile");


        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.getArgs().length != 2) {
            System.out.println(argFailString);
            return;
        }

        String sourcePath = cmd.getArgs()[0];
        String destinationPath = cmd.getArgs()[1];

        File searchPath = new File(sourcePath);
        File packageRoot;

        if (!cmd.hasOption("p")) {
            packageRoot = new File(sourcePath);
        } else {
            packageRoot = new File(cmd.getOptionValue("p"));
        }

        Config config = new Config(destinationPath);
        if (cmd.hasOption("cp")) {
            for (String optionValue : cmd.getOptionValues("cp")) {
                config.getCompilerClassPaths().add(optionValue);
            }
        }

        addCodeDirectory(config, searchPath, packageRoot);

        J2PBG j2PBG = new J2PBG(config);
        j2PBG.generateSourceFiles();
        if (!cmd.hasOption("nocompile"))
            j2PBG.compileJavaFiles();

        if (!cmd.hasOption("noloader")) {
            File loaderFile = j2PBG.generateLoader();
            System.out.println("Loader file created: " + loaderFile.getAbsolutePath());
        }
        System.out.println("Done!");
    }

}
