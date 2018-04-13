package org.madhawav.j2pcg;

import org.madhawav.j2pcg.javagen.JavaCodeGen;
import org.madhawav.j2pcg.javagen.JavaWriter;
import org.madhawav.j2pcg.pygen.PythonCodeGen;
import org.madhawav.j2pcg.pygen.PythonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class App {

    public static void generate(Class _class, String rootPath) throws FileNotFoundException {
        java.io.File rootDirectory = new File(rootPath);
        rootDirectory.mkdirs();

        File javaProxyClassFile = new File(rootPath +"/java/proxy/" + _class.getCanonicalName().replace(".","/") + ".java");
        File javaCallbackInterfaceFile = new File(rootPath +"/java/proxy/" + _class.getCanonicalName().replace(".","/") + "_callbackInterface.java");

        File pythonProxyClassFile = new File(rootPath +"/python/" + _class.getCanonicalName().replace(".","/") + ".py");

        pythonProxyClassFile.getParentFile().mkdirs();
        javaProxyClassFile.getParentFile().mkdirs();

        FileOutputStream fos;
        PrintStream ps;
        if(!Modifier.isFinal(_class.getModifiers())){
            // We cannot override non final classes
            JavaWriter jWriter = new JavaWriter();
            JavaCodeGen.generateProxyCode(_class, jWriter);

            fos = new FileOutputStream(javaProxyClassFile);
            ps = new PrintStream(fos);
            ps.println(jWriter.getOutput());
            ps.close();

            jWriter = new JavaWriter();
            JavaCodeGen.generateCallbackInterfaceCode(_class, jWriter);

            fos = new FileOutputStream(javaCallbackInterfaceFile);
            ps = new PrintStream(fos);
            ps.println(jWriter.getOutput());
            ps.close();
        }


        PythonWriter pWriter = new PythonWriter();


        if(!Modifier.isFinal(_class.getModifiers())){
            PythonCodeGen.generateProxyClass(_class,pWriter);
            PythonCodeGen.generateProxyCallbackClass(_class,pWriter);
        }
        else {
            PythonCodeGen.generateDirectProxyClass(_class,pWriter);
        }

        fos = new FileOutputStream(pythonProxyClassFile);
        ps = new PrintStream(fos);
        ps.println(pWriter.getOutput());
        ps.close();

    }

    public static void main(String[] args) throws FileNotFoundException {

        generate(Random.class,"output");

        PythonWriter writer = new PythonWriter();


        PythonCodeGen.generateProxyClass(Random.class,writer);
        PythonCodeGen.generateProxyCallbackClass(Random.class, writer);

        FileOutputStream fos = new FileOutputStream("output.py");
        PrintStream ps = new PrintStream(fos);
        ps.println(writer.getOutput());
        ps.close();

        System.out.println(writer.getOutput());

        JavaWriter jWriter = new JavaWriter();
        JavaCodeGen.generateProxyCode(Random.class, jWriter);

        JavaCodeGen.generateCallbackInterfaceCode(Random.class,jWriter);

        fos = new FileOutputStream("output.java");
        ps = new PrintStream(fos);
        ps.println(jWriter.getOutput());
        ps.close();


    }
}
