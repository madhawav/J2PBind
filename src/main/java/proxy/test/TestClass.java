package proxy.test;

public class TestClass extends test.TestClass{
    private TestClass_callbackInterface callbackInterface = null;

    public TestClass(TestClass_callbackInterface callbackInterface){
        this.callbackInterface = callbackInterface;
    }

    public void _python_testMethod1(){
        super.testMethod1();
    }

    public void _python_testBoth(){
        super.testBoth();
    }

    public void testMethod1(){
            this.callbackInterface.testMethod1();
    }

    public void testMethod2(){
            this.callbackInterface.testMethod2();
    }

    public void testBoth(){
            this.callbackInterface.testBoth();
    }
}
