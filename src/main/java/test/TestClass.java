package test;
public abstract class TestClass {
    public void testMethod1(){
        System.out.println("Java: TestMethod1");

    }
    public abstract void testMethod2();

    public void testBoth(){
        System.out.println("Java: TestBoth");
        this.testMethod1();
        this.testMethod2();
    }
}
