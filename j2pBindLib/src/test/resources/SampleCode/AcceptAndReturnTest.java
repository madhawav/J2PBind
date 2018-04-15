package test;
public class AcceptAndReturnTest {

    public Object accept(Object m){
        System.out.println("Java: Accepted: " + m.getClass().getCanonicalName() + " : " + m.toString());
        System.out.println("Java: Returning input back to caller");
        return m;
    }

    public int acceptInt(int m){
        System.out.println("Java: int accepted: " + m);
        System.out.println("Java: Returning input back to caller");
        return m*2;
    }
}