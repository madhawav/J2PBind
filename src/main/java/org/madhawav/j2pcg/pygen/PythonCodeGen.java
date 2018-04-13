package org.madhawav.j2pcg.pygen;

import org.madhawav.j2pcg.Util;

import javax.xml.stream.FactoryConfigurationError;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

class GenClass extends CodeBlock {
    private String className;
    private ArrayList<String> globalFields = new ArrayList<>();

    private ArrayList<GenMethod> methods = new ArrayList<>();
    private String parentClassName = null;

    public GenClass(String className){
        this.className = className;
    }
    public ArrayList<GenMethod> getMethods() {
        return methods;
    }

    public ArrayList<String> getGlobalFields() {
        return globalFields;
    }

    public String getParentClassName() {
        return parentClassName;
    }

    public void setParentClassName(String parentClassName) {
        this.parentClassName = parentClassName;
    }

    public String getClassName() {
        return className;
    }

    public void generatePython(PythonWriter writer) {
        String firstLine = "class " + this.className;
        if(this.parentClassName != null)
            firstLine += "(" + parentClassName + ")";

        writer.write(firstLine + ":");
        writer.increaseIndent();

        for(String globalField : globalFields){
            writer.write(globalField);
        }

        boolean emptyBody = true;
        for (GenMethod method : methods) {
            method.generatePython(writer);
            emptyBody = false;
        }

        if(emptyBody)
        {
            writer.write("pass");
        }
        writer.decreaseIndent();
    }
}

class GenAnnotate extends CodeBlock{
    private String anotationName;
    private ArrayList<String> parameters;

    public GenAnnotate(String anotationName){
        this.anotationName = anotationName;
        this.parameters = new ArrayList<>();
    }

    public ArrayList<String> getParameters() {
        return parameters;
    }

    public String getAnotationName() {
        return anotationName;
    }

    @Override
    public void generatePython(PythonWriter writer) {
        StringBuilder str_params = new StringBuilder();
        boolean first = true;
        for(String param: this.parameters){
            if(!first){
                str_params.append(", ");
            }
            str_params.append(param);
            first = false;
        }
        writer.write("@" + anotationName +"(" + str_params.toString() + ")");
    }
}

class GenMethod extends CodeBlock{
    public GenMethod(String methodName, String[] methodParams){
        this.methodName = methodName;
        this.methodParams = new ArrayList<String>();
        this.methodParams.addAll(Arrays.asList(methodParams));
    }

    private List<GenAnnotate> annotations = new ArrayList<>();

    public List<GenAnnotate> getAnnotations() {
        return annotations;
    }

    private String methodName;
    private ArrayList<String> methodParams;

    private CodeBlock methodBody = new EmptyCodeBlock();

    public ArrayList<String> getMethodParams() {
        return methodParams;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodBody(CodeBlock methodBody) {
        this.methodBody = methodBody;
    }

    public CodeBlock getMethodBody() {
        return methodBody;
    }

    public void generatePython(PythonWriter writer){
        StringBuilder str_params = new StringBuilder();
        boolean first = true;
        for(String param: this.methodParams){
            if(!first){
                str_params.append(", ");
            }
            str_params.append(param);
            first = false;
        }
        for(GenAnnotate annotate: this.annotations){
            annotate.generatePython(writer);
        }
        writer.write("def " + this.methodName + "(" + str_params +  ")" + ":");
        writer.increaseIndent();
        methodBody.generatePython(writer);
        writer.decreaseIndent();
    }
}

class EmptyCodeBlock extends CodeBlock{

    public void generatePython(PythonWriter writer) {
        writer.write("pass");
    }
}

abstract class CodeBlock{
    public abstract void generatePython(PythonWriter writer);
}

class CodeBlockGroup{
    private List<CodeBlock> codeBlocks;
    public CodeBlockGroup(){
        this.codeBlocks = new ArrayList<CodeBlock>();
    }

    public void generatePython(PythonWriter writer){
        for (CodeBlock cb : codeBlocks){
            cb.generatePython(writer);
        }
    }
}

class SimpleSubstituteCodeBlock extends CodeBlock {
    private String text;
    private HashMap<String, String> substitutionMap;
    public SimpleSubstituteCodeBlock(String text){
        this.text = text;
        this.substitutionMap = new HashMap<>();
    }
    @Override
    public void generatePython(PythonWriter writer) {
        String[] output = {text};
        this.substitutionMap.forEach((k,v)->{
            output[0] = output[0].replace(k,v);
        });
        writer.write(output[0]);
    }

    public HashMap<String, String> getSubstitutionMap() {
        return substitutionMap;
    }

    public void addStringSubstitution(String k, String v){
        this.substitutionMap.put(k,v);
    }

    public void addParameterSubstitution(String k, String[] parameters){
        String output = "";
        boolean first = true;

        for(String param : parameters){
            if(!first){
                output += ", ";
            }
            output += param;
            first = false;
        }

        this.substitutionMap.put(k, output);
    }
}

class GenProxyCallbackClass extends GenClass{

    public GenProxyCallbackClass(Class input) {
        super("_" + input.getSimpleName() + "OverrideCallback");
        this.setParentClassName("PythonJavaClass");
        this.getMethods().add(generateInitMethod(input));

        String str_interface = "proxy/" + input.getName().replace(".","/") + "_callbackInterface";
        this.getGlobalFields().add("__javainterfaces__ = ['" + str_interface + "']");

        int i = 0;
        for(Method m : input.getMethods()){
            GenMethod _m = generateMethod(input, m, i++);
            if(_m != null)
                this.getMethods().add(_m);
        }
    }

    private GenMethod generateInitMethod(Class input){
        GenMethod initMethod = new GenMethod("__init__", new String[] {"self", "proxy"});

        String block =
                "super(_[ClassName]OverrideCallback, self).__init__()\n" +
                        "self._proxy = proxy\n ";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[ClassName]", input.getSimpleName());
        simpleSubstituteCodeBlock.addStringSubstitution("[RootedClassName]", input.getCanonicalName());

        initMethod.setMethodBody(simpleSubstituteCodeBlock);
        return initMethod;
    }

    public GenMethod generateMethod(Class _class, Method method, int index){
        if(method.isBridge())
            return null;

        if(!Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers()))
            return null;

        if(!Modifier.isPublic(method.getReturnType().getModifiers()) && !Modifier.isProtected(method.getReturnType().getModifiers()))
            return null;

        for(Parameter p : method.getParameters()){
            if(!Modifier.isPublic(p.getType().getModifiers()) && !Modifier.isProtected(p.getType().getModifiers()))
                return null;
        }

        GenMethod genMethod = new GenMethod(method.getName()+String.valueOf(index), new String[] {"self", "*args"});

        GenAnnotate javaMethodAnnot = new GenAnnotate("java_method");
        javaMethodAnnot.getParameters().add('"' + Util.encodeSignature(method) + '"');
        javaMethodAnnot.getParameters().add('"' + method.getName() + '"');
        genMethod.getAnnotations().add(javaMethodAnnot);

        String block =
                "self._proxy.[MethodName](*args)\n ";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[MethodName]", method.getName());

        genMethod.setMethodBody(simpleSubstituteCodeBlock);
        return genMethod;
    }

}

class GenDirectProxyClass extends GenClass{
    // Direct proxy is used on final classes
    public GenDirectProxyClass(Class input) {
        super(input.getSimpleName());
        this.getMethods().add(generateInitMethod(input));

        for(Method m : input.getMethods()){
            GenMethod _m = generateMethod(input, m);
            if(_m != null)
                this.getMethods().add(generateMethod(input, m));
        }
    }

    private GenMethod generateMethod(Class _class, Method method){
        if(method.isBridge())
            return null;

        if(!Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers()))
            return null;

        if(!Modifier.isPublic(method.getReturnType().getModifiers()) && !Modifier.isProtected(method.getReturnType().getModifiers()))
            return null;

        for(Parameter p : method.getParameters()){
            if(!Modifier.isPublic(p.getType().getModifiers()) && !Modifier.isProtected(p.getType().getModifiers()))
                return null;
        }

        GenMethod genMethod = new GenMethod(method.getName(), new String[] {"self", "*args"});

        String block =
                "[Return]self._proxy.[MethodName](*args)\n ";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[MethodName]", method.getName());

        if(method.getReturnType() == void.class)
            simpleSubstituteCodeBlock.addStringSubstitution("[Return]","");
        else
            simpleSubstituteCodeBlock.addStringSubstitution("[Return]", "return ");

        genMethod.setMethodBody(simpleSubstituteCodeBlock);
        return genMethod;
    }

    private GenMethod generateInitMethod(Class input){
        GenMethod initMethod = new GenMethod("__init__", new String[] {"self", "*args"});

        String block =
                "super([ClassName], self).__init__(*args)\n" +
                        "self._proxy = autoclass(\"[RootedClassName]\")(*args)\n" +
                        "\n ";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[ClassName]", input.getSimpleName());
        simpleSubstituteCodeBlock.addStringSubstitution("[RootedClassName]", input.getCanonicalName());

        initMethod.setMethodBody(simpleSubstituteCodeBlock);
        return initMethod;
    }

}
class GenProxyClass extends GenClass {
    private GenMethod generateInitMethod(Class input){
        GenMethod initMethod = new GenMethod("__init__", new String[] {"self", "*args"});

        String block =
                "super([ClassName], self).__init__(*args)\n" +
                "self.override_callback = _[ClassName]OverrideCallback(self)\n" +
                "\n" +
                "self._proxy = autoclass(\"proxy.[RootedClassName]\")(self.override_callback, *args)\n" +
                "\n ";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[ClassName]", input.getSimpleName());
        simpleSubstituteCodeBlock.addStringSubstitution("[RootedClassName]", input.getCanonicalName());

        initMethod.setMethodBody(simpleSubstituteCodeBlock);
        return initMethod;
    }

    private GenMethod generateMethod(Class _class, Method method){
        if(method.isBridge())
            return null;

        if(!Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers()))
            return null;

        if(!Modifier.isPublic(method.getReturnType().getModifiers()) && !Modifier.isProtected(method.getReturnType().getModifiers()))
            return null;

        for(Parameter p : method.getParameters()){
            if(!Modifier.isPublic(p.getType().getModifiers()) && !Modifier.isProtected(p.getType().getModifiers()))
                return null;
        }

        GenMethod genMethod = new GenMethod(method.getName(), new String[] {"self", "*args"});

        String block =
                "[Return]self._proxy._python_[MethodName](*args)\n ";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[MethodName]", method.getName());

        if(method.getReturnType() == void.class)
            simpleSubstituteCodeBlock.addStringSubstitution("[Return]","");
        else
            simpleSubstituteCodeBlock.addStringSubstitution("[Return]", "return ");

        genMethod.setMethodBody(simpleSubstituteCodeBlock);
        return genMethod;
    }

    public GenProxyClass(Class input) {
        super(input.getSimpleName());
        this.getMethods().add(generateInitMethod(input));

        for(Method m : input.getMethods()){
            GenMethod _m = generateMethod(input, m);
            if(_m != null)
                this.getMethods().add(generateMethod(input, m));
        }
    }

}

public class PythonCodeGen
{
    public static void generateProxyClass(Class input, PythonWriter writer){
        GenProxyClass genProxyClass = new GenProxyClass(input);

        writer.write("from jnius import autoclass, PythonJavaClass, java_method, JavaClass, MetaJavaClass");

        genProxyClass.generatePython(writer);
    }

    public static void generateDirectProxyClass(Class input, PythonWriter writer){
        GenDirectProxyClass genProxyClass = new GenDirectProxyClass(input);

        writer.write("from jnius import autoclass, PythonJavaClass, java_method, JavaClass, MetaJavaClass");

        genProxyClass.generatePython(writer);
    }
    public static void generateProxyCallbackClass(Class input, PythonWriter writer){
        GenProxyCallbackClass genProxyClass = new GenProxyCallbackClass(input);

        writer.write("from jnius import autoclass, PythonJavaClass, java_method, JavaClass, MetaJavaClass");

        genProxyClass.generatePython(writer);
    }
}
