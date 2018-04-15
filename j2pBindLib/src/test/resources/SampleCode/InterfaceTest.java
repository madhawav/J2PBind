package test;
import test.SampleInterface;
public class InterfaceTest {

    public int interfaceTest(SampleInterface sampleInterface){
        System.out.println("InterfaceTest @ Java: Calling sampleInterface.testMethod(5)");
        int a = sampleInterface.testMethod(5);
        System.out.println("InterfaceTest @ Java: sampleInterface.testMethod returned " + a);
        return a;
    }
}