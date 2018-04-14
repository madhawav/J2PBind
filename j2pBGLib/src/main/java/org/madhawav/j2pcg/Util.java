package org.madhawav.j2pcg;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

    public static List<Method> getExposedMethods(Class _class){
        ArrayList<Method> results = new ArrayList<>();
        for(Method m : _class.getMethods()){
            boolean passed = true;
//            if(!Modifier.isPublic(m.getModifiers()))
//                passed = false;
//            if(!Modifier.isPublic(m.getReturnType().getModifiers()))
//                passed = false;


//            for(Parameter p : m.getParameters()){
//                if(!Modifier.isPublic(p.getType().getModifiers()))
//                {
//                    passed = false;
//                }
//            }

            if(m.isBridge())
                passed = false;

            if(passed)
                results.add(m);
        }
        return results;
    }

    public static boolean isBasic(Type t){
        if(t.equals(boolean.class)){
            return true;
        }
        else if(t.equals(byte.class)){
            return true;
        }
        else if(t.equals(char.class)){
            return true;
        }
        else if(t.equals(short.class)){
            return true;
        }
        else if(t.equals(int.class)){
            return true;
        }
        else if(t.equals(long.class)){
            return true;
        }
        else if(t.equals(float.class)){
            return true;
        }
        else if(t.equals(double.class)){
            return true;
        }
        else if(t.equals(void.class)){
            return true;
        }
        else if(t.equals(String.class)){
            return true;
        }
        return false;
    }

    public static boolean isProxyAvailable(Type t){
        return true;
    }

    public static String getBasicConverterName(Type t){
        if(t.equals(boolean.class)){
            return "bool";
        }
        else if(t.equals(byte.class)){
            return "byte";
        }
        else if(t.equals(char.class)){
            return "str";
        }
        else if(t.equals(short.class)){
            return "int";
        }
        else if(t.equals(int.class)){
            return "int";
        }
        else if(t.equals(long.class)){
            return "int";
        }
        else if(t.equals(float.class)){
            return "float";
        }
        else if(t.equals(double.class)){
            return "float";
        }
        else if(t.equals(String.class)){
            return "str";
        }
        return "";
    }

    public static String encodeParameterType(Type t){
        if(t.equals(boolean.class)){
            return "Z";
        }
        else if(t.equals(byte.class)){
            return "B";
        }
        else if(t.equals(char.class)){
            return "C";
        }
        else if(t.equals(short.class)){
            return "S";
        }
        else if(t.equals(int.class)){
            return "I";
        }
        else if(t.equals(long.class)){
            return "J";
        }
        else if(t.equals(float.class)){
            return "F";
        }
        else if(t.equals(double.class)){
            return "D";
        }
        else if(t.equals(void.class)){
            return "V";
        }
        else{
            return "L" + t.getTypeName().replace(".","/") + ";";
        }
    }
    public static String encodeSignature(Method m){
        StringBuilder paramString = new StringBuilder();
        for(Parameter p: m.getParameters()){
            paramString.append(encodeParameterType(p.getType()));
        }
        return "(" + paramString.toString() + ")" + encodeParameterType(m.getReturnType());
    }

    public static void main(String[] args) throws NoSuchMethodException {
        List<Method> methods = getExposedMethods(StringBuilder.class);
        for(Method m : methods){
            String paramPart = "";
            for(Parameter p: m.getParameters()){
                paramPart += p.getType().getCanonicalName() + " " + p.getName() + ",";
            }
            System.out.println(m.getReturnType().getCanonicalName() + " " + m.getName() + "(" + paramPart + ")");
        }

    }
}
