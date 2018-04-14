package org.madhawav.j2pcg;

import org.madhawav.j2pcg.javagen.JavaCodeGen;
import org.madhawav.j2pcg.javagen.JavaWriter;
import org.madhawav.j2pcg.pygen.PythonCodeGen;
import org.madhawav.j2pcg.pygen.PythonWriter;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class J2PCG {
    private Config config;

    private static Logger logger = Logger.getLogger(J2PCG.class.getName());

    private List<File> toCompileFiles = new ArrayList<>();

    /**
     * Instantiate with specified configuration
     * @param config
     */
    public J2PCG(Config config){
        this.config = config;
    }

    /**
     * Retrieve configuration
     * @return
     */
    public Config getConfig() {
        return config;
    }

    public File generateLoader() {
        StringBuilder loader_code = new StringBuilder("import os\n" +
                "def load_library(java_export_path = os.path.join(os.path.dirname(os.path.abspath(__file__)),\"[PYTHON_TO_JAVA_PATH]\")):\n" +
                "    import jnius_config\n" +
                "    p = os.path.join(java_export_path)\n" +
                "    jnius_config.add_classpath(p)\n");

        loader_code = new StringBuilder(loader_code.toString().replace("[PYTHON_TO_JAVA_PATH]", config.getJavaExportPathRelativeToPythonExportPath()));

        for (String classPath : config.getCompilerClassPaths()) {
            loader_code.append("    jnius_config.add_classpath(\"[Class_Path]\")\n".replace("[Class_Path]", classPath));
        }

        File target = new File(config.getPythonExportPath() + "/loader.py");
        PrintStream ps = null;

        try {
            ps = new PrintStream(target);
            ps.println(loader_code);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if(ps != null)
                ps.close();
        }
        return target;
    }

    /**
     * Copy NativeInvocationHandler class to source directory. This file is required by pyjnius for callbacks.
     */
    private void copyJniusClass(){

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("jnius_util/NativeInvocationHandler.java").getFile());
        File target = new File(config.getJavaExportPath() + "/org/jnius/NativeInvocationHandler.java");
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        target.getParentFile().mkdirs();
        try
        {
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(target);
            while(fileInputStream.available() > 0){
                fileOutputStream.write(fileInputStream.read());
            }
            this.toCompileFiles.add(target);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Compile generated .java files to obtain .class files
     */
    public void compileJavaFiles(){
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        List<String> optionList = new ArrayList<String>();
        String additionalClasses = "";
        for(String classPath : config.getCompilerClassPaths()){
            additionalClasses += classPath + ":";
        }
        optionList.add("-classpath");
        optionList.add(System.getProperty("java.class.path") + ":" + additionalClasses);

        Iterable<? extends JavaFileObject> compilationUnits1 =
                fileManager.getJavaFileObjectsFromFiles(this.toCompileFiles);
        compiler.getTask(null, fileManager, null, optionList, null, compilationUnits1).call();

    }

    /**
     * Generate Python and Java source files
     * @throws FileNotFoundException
     */
    public void generateSourceFiles() throws FileNotFoundException {

        for(Class c : this.config.getClasses()){
            if (!c.isInterface()) {
                // Interfaces are not supported yet
                logger.info("Generating " + c.getCanonicalName());
                generateClassSourceFiles(c);
            }
        }

        logger.info("Copying dependency files");
        this.copyJniusClass();


        logger.info("Done!");
    }

    /**
     * Generate java and python source files for the specified class
     * @param _class
     * @throws FileNotFoundException
     */
    private void generateClassSourceFiles(Class _class) throws FileNotFoundException {
        File javaProxyClassFile = new File(config.getJavaExportPath() + "/proxy/" + _class.getCanonicalName().replace(".","/") + ".java");
        File javaCallbackInterfaceFile = new File(config.getJavaExportPath() +"/proxy/" + _class.getCanonicalName().replace(".","/") + "_callbackInterface.java");

        File pythonProxyClassFile = new File(config.getPythonExportPath() +"/" + config.getPythonPrefix().replace(".","/") +"/" + _class.getCanonicalName().replace(".","/") + ".py");

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

            this.toCompileFiles.add(javaProxyClassFile);
            this.toCompileFiles.add(javaCallbackInterfaceFile);
        }


        PythonWriter pWriter = new PythonWriter();
        if(!Modifier.isFinal(_class.getModifiers())){
            PythonCodeGen.generateProxyClass(_class,pWriter,config.getPythonPrefix());
            PythonCodeGen.generateProxyCallbackClass(_class,pWriter);
        }
        else {
            PythonCodeGen.generateDirectProxyClass(_class,pWriter, config.getPythonPrefix());
        }

        fos = new FileOutputStream(pythonProxyClassFile);
        ps = new PrintStream(fos);
        ps.println(pWriter.getOutput());
        ps.close();

    }

}
