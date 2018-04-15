package test;
public class AcceptAndReturnTest {
    public AcceptAndReturnTest getMe(){
        return this;
    }

    public void accept(Object m){
        System.out.println("Java: Accepted: " + m.getClass().getCanonicalName() + " : " + m.toString());
    }
}