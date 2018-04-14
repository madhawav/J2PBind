package org.madhawav.j2pcg;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private String javaExportPath;
    private String pythonExportPath;
    private String pythonPrefix;
    private List<Class> classList;
    private String javaExportPathRelativeToPythonExportPath;

    private List<String> compilerClassPaths;


    public Config(String rootPath){
        this.javaExportPath = rootPath + "/java";
        this.pythonExportPath = rootPath + "/python";
        this.pythonPrefix = "pybind";
        this.javaExportPathRelativeToPythonExportPath = "../java";
        this.classList = new ArrayList<>();
        this.compilerClassPaths = new ArrayList<>();
    }

    public List<String> getCompilerClassPaths() {
        return compilerClassPaths;
    }

    public void addClass(Class c){
        this.classList.add(c);
    }

    public void removeClass(Class c){
        this.classList.remove(c);
    }

    public List<Class> getClasses(){
        return this.classList;
    }

    public String getPythonPrefix() {
        return pythonPrefix;
    }

    public void setPythonPrefix(String pythonPrefix) {
        this.pythonPrefix = pythonPrefix;
    }

    public String getJavaExportPath() {
        return javaExportPath;
    }

    public String getPythonExportPath() {
        return pythonExportPath;
    }

    public void setJavaExportPath(String javaExportPath) {
        this.javaExportPath = javaExportPath;
    }

    public void setPythonExportPath(String pythonExportPath) {
        this.pythonExportPath = pythonExportPath;
    }

    public String getJavaExportPathRelativeToPythonExportPath() {
        return javaExportPathRelativeToPythonExportPath;
    }

    public void setJavaExportPathRelativeToPythonExportPath(String value){
        this.javaExportPathRelativeToPythonExportPath = value;
    }
}
