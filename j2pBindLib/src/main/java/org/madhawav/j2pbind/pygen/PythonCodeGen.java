package org.madhawav.j2pbind.pygen;

import org.madhawav.j2pbind.Util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
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
        this.useParams = true;
    }

    public boolean isUseParams() {
        return useParams;
    }

    public void setUseParams(boolean useParams) {
        this.useParams = useParams;
    }

    private boolean useParams;

    public ArrayList<String> getParameters() {
        return parameters;
    }

    public String getAnotationName() {
        return anotationName;
    }

    @Override
    public void generatePython(PythonWriter writer) {
        if(useParams){
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
        else{
            writer.write("@" + anotationName);
        }

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

        if (Modifier.isStatic(method.getModifiers()))
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
                "r = self._proxy.[MethodName](*args)\n";

        if(method.getReturnType() != void.class)
        {
            if(!Util.isBasic(method.getReturnType())){
                block += "if \"_proxy\" in r.__dict__:\n" +
                        "    r = r.__dict__[\"_proxy\"]\n";
            }

            block += "return r\n";
        }

        block += "\n \n";


        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[MethodName]", method.getName());

//        if(method.getReturnType() == void.class)
//            simpleSubstituteCodeBlock.addStringSubstitution("[Return]","");
//        else
//            simpleSubstituteCodeBlock.addStringSubstitution("[Return]", "return ");

        genMethod.setMethodBody(simpleSubstituteCodeBlock);
        return genMethod;
    }

}

class GenDirectProxyClass extends GenClass{
    // Direct proxy is used on final classes
    public GenDirectProxyClass(Class input, String pythonPrefix) {
        super(input.getSimpleName());
        this.getMethods().add(generateInitMethod(input));
        this.getMethods().add(generateFromProxy(input));

        for(Method m : input.getMethods()){
            GenMethod _m = generateMethod(input, m, pythonPrefix);
            if(_m != null)
                this.getMethods().add(_m);
        }
    }

    private GenMethod generateFromProxy(Class _class){
        GenMethod fromProxyMethod = new GenMethod("_from_proxy", new String[] {"cls", "proxy"});
        GenAnnotate clsmethod = new GenAnnotate("classmethod");
        clsmethod.setUseParams(false);
        fromProxyMethod.getAnnotations().add(clsmethod);
        String block =
                "new_class = [ClassName].__new__(cls)\n" +
                        "new_class._proxy = proxy\n" +
                        "return new_class\n \n";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[ClassName]", _class.getSimpleName());
        fromProxyMethod.setMethodBody(simpleSubstituteCodeBlock);
        return fromProxyMethod;
    }

    private GenMethod generateMethod(Class _class, Method method, String pythonPrefix){
        if(method.isBridge())
            return null;

        if(!Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers()))
            return null;

        if(!Modifier.isPublic(method.getReturnType().getModifiers()) && !Modifier.isProtected(method.getReturnType().getModifiers()))
            return null;

        if (Modifier.isStatic(method.getModifiers()))
            return null;

        for(Parameter p : method.getParameters()){
            if(!Modifier.isPublic(p.getType().getModifiers()) && !Modifier.isProtected(p.getType().getModifiers()))
                return null;
        }

        GenMethod genMethod = new GenMethod(method.getName(), new String[] {"self", "*args"});

        String block =
                "res = self._proxy.[MethodName](*args)\n";

        if(method.getReturnType() != void.class) {
            if(Util.isBasic(method.getReturnType())){
                block += "res = " + Util.getBasicConverterName(method.getReturnType()) + "(res)\n";
            }
            else {
                // Unwrap and obtain proxy
                block += "if \"_proxy\" in res.__dict__:\n" +
                        "    res = res.__dict__[\"_proxy\"]\n";

                if (Util.isProxyAvailable(method.getReturnType())) {

                    block += "from [PythonPrefix].[FullReturnType] import [SimpleReturnType] as tc\n";
                    block += "res = tc._from_proxy(res)\n";
                }
            }
            block += "return res\n";
        }

        block += "\n \n";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[MethodName]", method.getName());
        simpleSubstituteCodeBlock.addStringSubstitution("[FullReturnType]",method.getReturnType().getCanonicalName());
        simpleSubstituteCodeBlock.addStringSubstitution("[SimpleReturnType]",method.getReturnType().getSimpleName());
        simpleSubstituteCodeBlock.addStringSubstitution("[PythonPrefix]", pythonPrefix);

//        if(method.getReturnType() == void.class)
//            simpleSubstituteCodeBlock.addStringSubstitution("[Return]","");
//        else
//            simpleSubstituteCodeBlock.addStringSubstitution("[Return]", "return ");

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
                " \n" +
                "common = [ClassName].__dict__.keys() & self.__class__.__dict__.keys()\n" +
                "diff = [m for m in common if [ClassName].__dict__[m] != self.__class__.__dict__[m]]\n" +
                " \n"+
                "self._proxy = autoclass(\"proxy.[RootedClassName]\")(self.override_callback, diff ,*args)\n" +
                "\n ";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[ClassName]", input.getSimpleName());
        simpleSubstituteCodeBlock.addStringSubstitution("[RootedClassName]", input.getCanonicalName());

        initMethod.setMethodBody(simpleSubstituteCodeBlock);
        return initMethod;
    }

    private GenMethod generateFromProxy(Class _class){
        GenMethod fromProxyMethod = new GenMethod("_from_proxy", new String[] {"cls", "proxy"});
        GenAnnotate clsmethod = new GenAnnotate("classmethod");
        clsmethod.setUseParams(false);
        fromProxyMethod.getAnnotations().add(clsmethod);

        String block =
                "new_class = [ClassName].__new__(cls)\n" +
                "new_class._proxy = proxy\n" +
                "new_class.override_callback = None\n" +
                "return new_class\n \n";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[ClassName]", _class.getSimpleName());
        fromProxyMethod.setMethodBody(simpleSubstituteCodeBlock);
        return fromProxyMethod;
    }

    private GenMethod generateMethod(Class _class, Method method, String pythonPrefix){
        if(method.isBridge())
            return null;

        if(!Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers()))
            return null;

        if(!Modifier.isPublic(method.getReturnType().getModifiers()) && !Modifier.isProtected(method.getReturnType().getModifiers()))
            return null;

        if (Modifier.isStatic(method.getModifiers()))
            return null;

        for(Parameter p : method.getParameters()){
            if(!Modifier.isPublic(p.getType().getModifiers()) && !Modifier.isProtected(p.getType().getModifiers()))
                return null;
        }

        GenMethod genMethod = new GenMethod(method.getName(), new String[] {"self", "*args"});

        String block = "if type(self) != [ClassName]:\n" +
                "    res = self._proxy._python_[MethodName](*args)\n" +
                "else:\n" +
                "    res = self._proxy.[MethodName](*args)\n";

        if(method.getReturnType() != void.class) {
            if(Util.isBasic(method.getReturnType())){
                block += "res = " + Util.getBasicConverterName(method.getReturnType()) + "(res)\n";
            }
            else {
                // Unwrap and obtain proxy
                block += "if \"_proxy\" in res.__dict__:\n" +
                "    res = res.__dict__[\"_proxy\"]\n";

                if (Util.isProxyAvailable(method.getReturnType())) {

                    block += "from [PythonPrefix].[FullReturnType] import [SimpleReturnType] as tc\n";
                    block += "res = tc._from_proxy(res)\n";
                }
            }
            block += "return res\n";
        }

        block += " \n \n";

//        String block =
//                "[Return]self._proxy._python_[MethodName](*args)\n ";

        SimpleSubstituteCodeBlock simpleSubstituteCodeBlock = new SimpleSubstituteCodeBlock(block);
        simpleSubstituteCodeBlock.addStringSubstitution("[MethodName]", method.getName());
        simpleSubstituteCodeBlock.addStringSubstitution("[ClassName]", _class.getSimpleName());
        simpleSubstituteCodeBlock.addStringSubstitution("[FullReturnType]",method.getReturnType().getCanonicalName());
        simpleSubstituteCodeBlock.addStringSubstitution("[SimpleReturnType]",method.getReturnType().getSimpleName());
        simpleSubstituteCodeBlock.addStringSubstitution("[PythonPrefix]", pythonPrefix);

//        if(method.getReturnType() == void.class)
//            simpleSubstituteCodeBlock.addStringSubstitution("[Return]","");
//        else
//            simpleSubstituteCodeBlock.addStringSubstitution("[Return]", "return ");

        genMethod.setMethodBody(simpleSubstituteCodeBlock);
        return genMethod;
    }

    public GenProxyClass(Class input, String pythonPrefix) {
        super(input.getSimpleName());
        this.getMethods().add(generateInitMethod(input));
        this.getMethods().add(generateFromProxy(input));

        for(Method m : input.getMethods()){
            GenMethod _m = generateMethod(input, m, pythonPrefix);
            if(_m != null)
                this.getMethods().add(_m);
        }
    }

}

public class PythonCodeGen
{
    public static void generateProxyClass(Class input, PythonWriter writer, String pythonPrefix){
        GenProxyClass genProxyClass = new GenProxyClass(input, pythonPrefix);

        writer.write("from jnius import autoclass, PythonJavaClass, java_method, JavaClass, MetaJavaClass");

        genProxyClass.generatePython(writer);
    }

    public static void generateDirectProxyClass(Class input, PythonWriter writer, String pythonPrefix){
        GenDirectProxyClass genProxyClass = new GenDirectProxyClass(input, pythonPrefix);

        writer.write("from jnius import autoclass, PythonJavaClass, java_method, JavaClass, MetaJavaClass");

        genProxyClass.generatePython(writer);
    }
    public static void generateProxyCallbackClass(Class input, PythonWriter writer){
        GenProxyCallbackClass genProxyClass = new GenProxyCallbackClass(input);

        writer.write("from jnius import autoclass, PythonJavaClass, java_method, JavaClass, MetaJavaClass");

        genProxyClass.generatePython(writer);
    }
}

