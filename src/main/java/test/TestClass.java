package test;
public abstract class TestClass {
    public int testMethod1(){
        System.out.println("Java: TestMethod1");
        return 1;

    }
//    public int testMethod2(){
//        System.out.println("Java: TestMethod2");
//        return 4;
//    }

    public abstract int testMethod2();

    public int testBoth(){
        System.out.println("Java: TestBoth");
        int a = this.testMethod1();
        System.out.println("Calling 2nd");
        int b = this.testMethod2();
        return a + b;
    }

    public TestClass getMe(){
        return this;
    }
}
