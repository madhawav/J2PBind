package org.madhawav.j2pbind.javagen;


public class JavaWriter {
    private StringBuilder outputBuilder = new StringBuilder();
    private int indentLevel = 0;
    public JavaWriter() {

    }
    public void write(String str){
        String[] lines = str.split("\n");
        for(String line : lines){
            for(int i = 0; i < indentLevel; i++){
                outputBuilder.append("    ");
            }
            outputBuilder.append(line);
            outputBuilder.append("\n");
        }
    }

    public void increaseIndent(){
        this.indentLevel += 1;
    }

    public void decreaseIndent(){
        this.indentLevel -= 1;
    }

    public String getOutput(){
        return this.outputBuilder.toString();
    }


}
