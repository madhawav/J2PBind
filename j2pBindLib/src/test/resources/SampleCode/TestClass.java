package test;
public abstract class TestClass {
    public int testMethod1(){
        System.out.println("testMethod1 @ Java: Called and returning 1");
        return 1;

    }

    public abstract int testMethod2();

    public int testBoth(){
        System.out.println("testBoth @ Java: Called");
        int a = this.testMethod1();
        System.out.println("testBoth @ Java: testMethod1 returned " + a);
        int b = this.testMethod2();
        System.out.println("testBoth @ Java: testMethod2 returned " + b);
        System.out.println("testBoth @ Java: Returning  " + (a + b));
        return a + b;
    }

    public TestClass getMe(){
        return this;
    }

}
