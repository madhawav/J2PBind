package proxy.java.util;
public class Random extends java.util.Random
{
    private Random_callbackInterface callbackInterface = null;
    public  Random(Random_callbackInterface callbackInterface)
    {
        super();
        this.callbackInterface = callbackInterface;
    }
    public  Random(Random_callbackInterface callbackInterface, long arg0)
    {
        super(arg0);
        this.callbackInterface = callbackInterface;
    }
    public void _python_setSeed(long arg0)
    {
        super.setSeed(arg0);
    }
    public void setSeed(long arg0)
    {
        if(this.callbackInterface != null) { this.callbackInterface.setSeed(arg0); }
        else { super.setSeed(arg0); }
    }
    public void _python_nextBytes(byte[] arg0)
    {
        super.nextBytes(arg0);
    }
    public void nextBytes(byte[] arg0)
    {
        if(this.callbackInterface != null) { this.callbackInterface.nextBytes(arg0); }
        else { super.nextBytes(arg0); }
    }
    public long _python_nextLong()
    {
        return super.nextLong();
    }
    public long nextLong()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.nextLong(); }
        else { return super.nextLong(); }
    }
    public boolean _python_nextBoolean()
    {
        return super.nextBoolean();
    }
    public boolean nextBoolean()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.nextBoolean(); }
        else { return super.nextBoolean(); }
    }
    public float _python_nextFloat()
    {
        return super.nextFloat();
    }
    public float nextFloat()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.nextFloat(); }
        else { return super.nextFloat(); }
    }
    public double _python_nextGaussian()
    {
        return super.nextGaussian();
    }
    public double nextGaussian()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.nextGaussian(); }
        else { return super.nextGaussian(); }
    }
    public java.util.stream.IntStream _python_ints()
    {
        return super.ints();
    }
    public java.util.stream.IntStream ints()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.ints(); }
        else { return super.ints(); }
    }
    public java.util.stream.IntStream _python_ints(long arg0, int arg1, int arg2)
    {
        return super.ints(arg0, arg1, arg2);
    }
    public java.util.stream.IntStream ints(long arg0, int arg1, int arg2)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.ints(arg0, arg1, arg2); }
        else { return super.ints(arg0, arg1, arg2); }
    }
    public java.util.stream.IntStream _python_ints(int arg0, int arg1)
    {
        return super.ints(arg0, arg1);
    }
    public java.util.stream.IntStream ints(int arg0, int arg1)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.ints(arg0, arg1); }
        else { return super.ints(arg0, arg1); }
    }
    public java.util.stream.IntStream _python_ints(long arg0)
    {
        return super.ints(arg0);
    }
    public java.util.stream.IntStream ints(long arg0)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.ints(arg0); }
        else { return super.ints(arg0); }
    }
    public java.util.stream.LongStream _python_longs(long arg0, long arg1, long arg2)
    {
        return super.longs(arg0, arg1, arg2);
    }
    public java.util.stream.LongStream longs(long arg0, long arg1, long arg2)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.longs(arg0, arg1, arg2); }
        else { return super.longs(arg0, arg1, arg2); }
    }
    public java.util.stream.LongStream _python_longs(long arg0)
    {
        return super.longs(arg0);
    }
    public java.util.stream.LongStream longs(long arg0)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.longs(arg0); }
        else { return super.longs(arg0); }
    }
    public java.util.stream.LongStream _python_longs()
    {
        return super.longs();
    }
    public java.util.stream.LongStream longs()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.longs(); }
        else { return super.longs(); }
    }
    public java.util.stream.LongStream _python_longs(long arg0, long arg1)
    {
        return super.longs(arg0, arg1);
    }
    public java.util.stream.LongStream longs(long arg0, long arg1)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.longs(arg0, arg1); }
        else { return super.longs(arg0, arg1); }
    }
    public java.util.stream.DoubleStream _python_doubles(double arg0, double arg1)
    {
        return super.doubles(arg0, arg1);
    }
    public java.util.stream.DoubleStream doubles(double arg0, double arg1)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.doubles(arg0, arg1); }
        else { return super.doubles(arg0, arg1); }
    }
    public java.util.stream.DoubleStream _python_doubles(long arg0, double arg1, double arg2)
    {
        return super.doubles(arg0, arg1, arg2);
    }
    public java.util.stream.DoubleStream doubles(long arg0, double arg1, double arg2)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.doubles(arg0, arg1, arg2); }
        else { return super.doubles(arg0, arg1, arg2); }
    }
    public java.util.stream.DoubleStream _python_doubles()
    {
        return super.doubles();
    }
    public java.util.stream.DoubleStream doubles()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.doubles(); }
        else { return super.doubles(); }
    }
    public java.util.stream.DoubleStream _python_doubles(long arg0)
    {
        return super.doubles(arg0);
    }
    public java.util.stream.DoubleStream doubles(long arg0)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.doubles(arg0); }
        else { return super.doubles(arg0); }
    }
    public int _python_nextInt(int arg0)
    {
        return super.nextInt(arg0);
    }
    public int nextInt(int arg0)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.nextInt(arg0); }
        else { return super.nextInt(arg0); }
    }
    public int _python_nextInt()
    {
        return super.nextInt();
    }
    public int nextInt()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.nextInt(); }
        else { return super.nextInt(); }
    }
    public double _python_nextDouble()
    {
        return super.nextDouble();
    }
    public double nextDouble()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.nextDouble(); }
        else { return super.nextDouble(); }
    }
    public void _python_wait(long arg0, int arg1) throws java.lang.InterruptedException
    {
        super.wait(arg0, arg1);
    }
    public void _python_wait(long arg0) throws java.lang.InterruptedException
    {
        super.wait(arg0);
    }
    public void _python_wait() throws java.lang.InterruptedException
    {
        super.wait();
    }
    public boolean _python_equals(java.lang.Object arg0)
    {
        return super.equals(arg0);
    }
    public boolean equals(java.lang.Object arg0)
    {
        if(this.callbackInterface != null) { return this.callbackInterface.equals(arg0); }
        else { return super.equals(arg0); }
    }
    public java.lang.String _python_toString()
    {
        return super.toString();
    }
    public java.lang.String toString()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.toString(); }
        else { return super.toString(); }
    }
    public int _python_hashCode()
    {
        return super.hashCode();
    }
    public int hashCode()
    {
        if(this.callbackInterface != null) { return this.callbackInterface.hashCode(); }
        else { return super.hashCode(); }
    }
    public java.lang.Class _python_getClass()
    {
        return super.getClass();
    }
    public void _python_notify()
    {
        super.notify();
    }
    public void _python_notifyAll()
    {
        super.notifyAll();
    }
}
package proxy.java.util;
public interface Random_callbackInterface
{
    public void setSeed(long arg0) ;
    public void nextBytes(byte[] arg0) ;
    public long nextLong() ;
    public boolean nextBoolean() ;
    public float nextFloat() ;
    public double nextGaussian() ;
    public java.util.stream.IntStream ints() ;
    public java.util.stream.IntStream ints(long arg0, int arg1, int arg2) ;
    public java.util.stream.IntStream ints(int arg0, int arg1) ;
    public java.util.stream.IntStream ints(long arg0) ;
    public java.util.stream.LongStream longs(long arg0, long arg1, long arg2) ;
    public java.util.stream.LongStream longs(long arg0) ;
    public java.util.stream.LongStream longs() ;
    public java.util.stream.LongStream longs(long arg0, long arg1) ;
    public java.util.stream.DoubleStream doubles(double arg0, double arg1) ;
    public java.util.stream.DoubleStream doubles(long arg0, double arg1, double arg2) ;
    public java.util.stream.DoubleStream doubles() ;
    public java.util.stream.DoubleStream doubles(long arg0) ;
    public int nextInt(int arg0) ;
    public int nextInt() ;
    public double nextDouble() ;
    public boolean equals(java.lang.Object arg0) ;
    public java.lang.String toString() ;
    public int hashCode() ;
}

