package org.madhawav.j2pcg.javagen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class JavaCodeGen {
    public static void generateProxyCode(Class input, JavaWriter writer){
        writer.write("package proxy." + input.getPackage().getName() + ";");
        writer.write("import java.util.*;");
        GenProxyClass genProxyClass = new GenProxyClass(input);
        genProxyClass.generateJava(writer);
    }

    public static void generateCallbackInterfaceCode(Class input, JavaWriter writer){
        writer.write("package proxy." + input.getPackage().getName() + ";");
        GenCallbackInterface genCallbackInterface = new GenCallbackInterface(input);
        genCallbackInterface.generateJava(writer);
    }
}

abstract class CodeBlock {
    public abstract void generateJava(JavaWriter javaWriter);
}

class EmptyCodeBlock extends CodeBlock{

    @Override
    public void generateJava(JavaWriter javaWriter) {
        javaWriter.write(" ");
    }
}

class GenInterface extends CodeBlock {
    private String modifier;
    private String interfaceName;
    private ArrayList<GenMethod> methods;

    public GenInterface(String interfaceName){
        this.interfaceName = interfaceName;
        this.modifier = "public";
        this.methods = new ArrayList<>();
    }

    public ArrayList<GenMethod> getMethods() {
        return methods;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public void generateJava(JavaWriter javaWriter) {
        String str = this.modifier + " interface " + this.interfaceName;

        javaWriter.write(str);
        javaWriter.write("{");
        javaWriter.increaseIndent();

        for(GenMethod method: methods){
            method.generateJava(javaWriter);
        }

        javaWriter.decreaseIndent();
        javaWriter.write("}");
    }
}
class GenClass extends CodeBlock {
    public String modifier;
    private String className;
    private String extendName;
    private ArrayList<String> globalFields;

    private ArrayList<GenMethod> methods;

    public ArrayList<GenMethod> getMethods() {
        return methods;
    }

    public GenClass(String className, String modifier){
        this.className = className;
        this.modifier = modifier;
        this.extendName = null;
        this.globalFields = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public String getModifier() {
        return modifier;
    }

    public ArrayList<String> getGlobalFields() {
        return globalFields;
    }

    public String getClassName() {
        return className;
    }

    public String getExtendName() {
        return extendName;
    }

    public void setExtendName(String extendName) {
        this.extendName = extendName;
    }

    @Override
    public void generateJava(JavaWriter javaWriter) {
        String str = this.modifier + " class " + this.className;
        if(this.extendName != null){
            str+= " extends " + this.extendName;
        }
        javaWriter.write(str);
        javaWriter.write("{");
        javaWriter.increaseIndent();

        for(String globalField: globalFields){
            javaWriter.write(globalField);
        }

        for(GenMethod method: methods){
            method.generateJava(javaWriter);
        }

        javaWriter.decreaseIndent();
        javaWriter.write("}");
    }
}

class GenParameter {
    private String name;
    private String type;

    public GenParameter(String name, String type){
        this.name = name;
        this.type= type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String generateJava() {
        return this.type + " " + this.name;
    }

    public static String generateJava(List<GenParameter> params){
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(GenParameter param: params){
            if(!first)
            {
                result.append(", ");
            }
            result.append(param.generateJava());
            first = false;
        }
        return result.toString();
    }
}

class GenMethod extends CodeBlock {
    private String methodName;
    private String modifier;
    private String returnType;
    private ArrayList<GenParameter> parameters;

    private ArrayList<String> throwExceptions;

    public ArrayList<String> getThrowExceptions() {
        return throwExceptions;
    }

    private CodeBlock methodBody;

    public GenMethod(String methodName, String modifier, String returnType){
        this.methodName = methodName;
        this.modifier = modifier;
        this.returnType = returnType;
        this.parameters = new ArrayList<>();
        this.methodBody = new EmptyCodeBlock();
        this.throwExceptions = new ArrayList<>();
    }

    public CodeBlock getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(CodeBlock methodBody) {
        this.methodBody = methodBody;
    }

    public String getMethodName() {
        return methodName;
    }

    public ArrayList<GenParameter> getParameters() {
        return parameters;
    }

    public String getModifier() {
        return modifier;
    }

    public String getReturnType() {
        return returnType;
    }

    @Override
    public void generateJava(JavaWriter javaWriter) {
        StringBuilder str = new StringBuilder(this.modifier + " " + this.getReturnType() + " " + this.getMethodName() + "(" + GenParameter.generateJava(this.parameters) + ")");
        if(this.throwExceptions.size() > 0){
            str.append(" throws ");
            boolean first = true;
            for(String ex: this.throwExceptions){
                if(!first)
                    str.append(", ");
                str.append(ex);
                first = false;
            }
        }

        if(this.methodBody != null){
            javaWriter.write(str.toString());
            javaWriter.write("{");
            javaWriter.increaseIndent();

            methodBody.generateJava(javaWriter);

            javaWriter.decreaseIndent();
            javaWriter.write("}");
        }
        else{
            javaWriter.write(str + " ;");
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
    public void generateJava(JavaWriter writer) {
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

    public void addParameterSubstitution(String k, List<String> parameters){
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

class GenCallbackInterface extends GenInterface{

    public GenCallbackInterface(Class input) {
        super(input.getSimpleName() + "_callbackInterface");

        for(Method m : input.getMethods()){
            if(!Modifier.isFinal(m.getModifiers())){
                if(Modifier.isPublic(m.getModifiers()) | Modifier.isProtected(m.getModifiers())){
                    GenMethod _m = generateCallbackMethod(input,m);
                    if(_m != null)
                        this.getMethods().add(_m);
                }
            }
        }
    }
    private GenMethod generateCallbackMethod(Class _class, Method method){
        if(!Modifier.isPublic(method.getReturnType().getModifiers()) & !Modifier.isProtected(method.getReturnType().getModifiers()))
            return null;

        if(method.isBridge())
            return null;

        for(Parameter p : method.getParameters()){
            if(!Modifier.isPublic(p.getType().getModifiers()) & !Modifier.isProtected(p.getType().getModifiers()))
                return null;
        }

        GenMethod r = new GenMethod(method.getName(), "public", method.getReturnType().getCanonicalName());
        r.getParameters().addAll(Arrays.stream(method.getParameters()).map((m)->{
            return new GenParameter(m.getName(), m.getType().getCanonicalName());
        }).collect(Collectors.toList()));

        r.setMethodBody(null);
        return r;
    }
}
class GenProxyClass extends GenClass{

    public GenProxyClass(Class input) {
        super(input.getSimpleName(), "public");
        if(!Modifier.isFinal(input.getModifiers()))
            this.setExtendName(input.getCanonicalName());

        String callbackField = "private " + input.getSimpleName() + "_callbackInterface callbackInterface = null;";
        this.getGlobalFields().add(callbackField);

        String overridenMethodsField = "private List<String> overridenMethods = null;";
        this.getGlobalFields().add(overridenMethodsField);

        for(Constructor cons: input.getConstructors()){
            this.getMethods().add(generateConstructor(input, cons));
        }

        for(Method m : input.getMethods()){
            if(!Modifier.isAbstract(m.getModifiers())){
                if(Modifier.isPublic(m.getModifiers()) | Modifier.isProtected(m.getModifiers())){
                    GenMethod _m = generatePythonCallMethod(input,m);
                    if (_m != null)
                        this.getMethods().add(_m);
                }
            }

            if(!Modifier.isFinal(m.getModifiers())){
                if(Modifier.isPublic(m.getModifiers()) | Modifier.isProtected(m.getModifiers())){
                    GenMethod _m = generateCallbackMethod(input,m);
                    if (_m != null)
                        this.getMethods().add(_m);
                }
            }
        }
    }

    public GenMethod generatePythonCallMethod(Class input, Method method){
        GenMethod r = new GenMethod("_python_" + method.getName(),"public", method.getReturnType().getCanonicalName());

        if(!Modifier.isPublic(method.getReturnType().getModifiers()) & !Modifier.isProtected(method.getReturnType().getModifiers()))
            return null;

        if(method.isBridge())
            return null;

        for(Parameter p : method.getParameters()){
            if(!Modifier.isPublic(p.getType().getModifiers()) & !Modifier.isProtected(p.getType().getModifiers()))
                return null;
        }

        r.getParameters().addAll(Arrays.stream(method.getParameters()).map((m)->{
            return new GenParameter(m.getName(), m.getType().getCanonicalName());
        }).collect(Collectors.toList()));

        r.getThrowExceptions().addAll(Arrays.stream(method.getExceptionTypes()).map((v)-> v.getCanonicalName()).collect(Collectors.toList()));

        String strBody = "[doReturn]super.[MethodName]([Parameters]);";
        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(strBody);
        simpleSubstituteCodeBlock.addStringSubstitution("[MethodName]", method.getName());
        simpleSubstituteCodeBlock.addParameterSubstitution("[Parameters]", Arrays.stream(method.getParameters()).map((p)->{
           return p.getName();
        }).collect(Collectors.toList()));
        if(method.getReturnType() != void.class)
        {
            simpleSubstituteCodeBlock.addStringSubstitution("[doReturn]", "return ");
        }
        else{
            simpleSubstituteCodeBlock.addStringSubstitution("[doReturn]","");
        }
        r.setMethodBody(simpleSubstituteCodeBlock);
        return r;
    }

    public GenMethod generateCallbackMethod(Class input, Method method){

        if(!Modifier.isPublic(method.getReturnType().getModifiers()) & !Modifier.isProtected(method.getReturnType().getModifiers()))
            return null;

        if(method.isBridge())
            return null;

        for(Parameter p : method.getParameters()){
            if(!Modifier.isPublic(p.getType().getModifiers()) & !Modifier.isProtected(p.getType().getModifiers()))
                return null;
        }

        String modifier = "";
        if(Modifier.isPublic(method.getModifiers()))
            modifier = "public";
        else if (Modifier.isProtected(method.getModifiers()))
            modifier = "protected";


        GenMethod r = new GenMethod(method.getName(),modifier, method.getReturnType().getCanonicalName());
        r.getParameters().addAll(Arrays.stream(method.getParameters()).map((m)->{
            return new GenParameter(m.getName(), m.getType().getCanonicalName());
        }).collect(Collectors.toList()));

        String superCall = "";
        if(!Modifier.isFinal(input.getModifiers()) && !Modifier.isAbstract(method.getModifiers()))
            superCall = "\nelse { " +
                    "[doReturn]super.[MethodName]([Parameters]); }";
        else
            superCall = "\nelse { throw new RuntimeException(\"Method not implemented\"); }";

//        String strBody = "if(this.callbackInterface != null) { [doReturn]this.callbackInterface.[MethodName]([Parameters]); }" +
//                superCall;

        String strBody = "if(this.overridenMethods.contains(\"[MethodName]\")) { [doReturn]this.callbackInterface.[MethodName]([Parameters]); }" + superCall;


        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(strBody);
        simpleSubstituteCodeBlock.addStringSubstitution("[MethodName]", method.getName());
        simpleSubstituteCodeBlock.addParameterSubstitution("[Parameters]", Arrays.stream(method.getParameters()).map((p)->{
            return p.getName();
        }).collect(Collectors.toList()));

        if(method.getReturnType() != void.class)
        {
            simpleSubstituteCodeBlock.addStringSubstitution("[doReturn]", "return ");
        }
        else{
            simpleSubstituteCodeBlock.addStringSubstitution("[doReturn]","");
        }

        r.setMethodBody(simpleSubstituteCodeBlock);
        return r;
    }

    private GenMethod generateConstructor(Class input, Constructor method){
        GenMethod constructor = new GenMethod(input.getSimpleName(), "public","");
        GenParameter callbackParameter = new GenParameter("callbackInterface",input.getSimpleName() + "_callbackInterface");
        constructor.getParameters().add(callbackParameter);

        GenParameter overridenMethodsParameter = new GenParameter("overiddenMethods","String[]");
        constructor.getParameters().add(overridenMethodsParameter);

        constructor.getParameters().addAll(Arrays.stream(method.getParameters()).map((m)->{
            return new GenParameter(m.getName(), m.getType().getCanonicalName());
        }).collect(Collectors.toList()));

        String superCall = "";
        if(!Modifier.isFinal(input.getModifiers()))
            superCall = "super([Parameters]);" + "\n";

        String strBody =
                superCall +
                "this.callbackInterface = callbackInterface;\n" +
                "this.overridenMethods = new ArrayList<String>();\n" +
                "for(String overridenMethod: overiddenMethods)\n" +
                "            this.overridenMethods.add(overridenMethod);\n";
        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(strBody);
        simpleSubstituteCodeBlock.addParameterSubstitution("[Parameters]", Arrays.stream(method.getParameters()).map((p)->{
            return p.getName();
        }).collect(Collectors.toList()));
        constructor.setMethodBody(simpleSubstituteCodeBlock);


        return constructor;
    }


    public static void main(String[] args) {
        JavaWriter jw = new JavaWriter();
        GenProxyClass gc = new GenProxyClass(GenProxyClass.class);

        gc.generateJava(jw);
        System.out.println(jw.getOutput());
    }
}

