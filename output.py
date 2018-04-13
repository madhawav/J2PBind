from jnius import autoclass, PythonJavaClass, java_method, JavaClass, MetaJavaClass
class Random:
    def __init__(self, *args):
        super(Random, self).__init__(*args)
        self.override_callback = _RandomOverrideCallback(self)
        
        self._proxy = autoclass("proxy.java.util.Random")(self.override_callback, *args)
        
         
    def setSeed(self, *args):
        self._proxy._python_setSeed(*args)
         
    def nextBytes(self, *args):
        self._proxy._python_nextBytes(*args)
         
    def nextLong(self, *args):
        return self._proxy._python_nextLong(*args)
         
    def nextBoolean(self, *args):
        return self._proxy._python_nextBoolean(*args)
         
    def nextFloat(self, *args):
        return self._proxy._python_nextFloat(*args)
         
    def nextGaussian(self, *args):
        return self._proxy._python_nextGaussian(*args)
         
    def ints(self, *args):
        return self._proxy._python_ints(*args)
         
    def ints(self, *args):
        return self._proxy._python_ints(*args)
         
    def ints(self, *args):
        return self._proxy._python_ints(*args)
         
    def ints(self, *args):
        return self._proxy._python_ints(*args)
         
    def longs(self, *args):
        return self._proxy._python_longs(*args)
         
    def longs(self, *args):
        return self._proxy._python_longs(*args)
         
    def longs(self, *args):
        return self._proxy._python_longs(*args)
         
    def longs(self, *args):
        return self._proxy._python_longs(*args)
         
    def doubles(self, *args):
        return self._proxy._python_doubles(*args)
         
    def doubles(self, *args):
        return self._proxy._python_doubles(*args)
         
    def doubles(self, *args):
        return self._proxy._python_doubles(*args)
         
    def doubles(self, *args):
        return self._proxy._python_doubles(*args)
         
    def nextInt(self, *args):
        return self._proxy._python_nextInt(*args)
         
    def nextInt(self, *args):
        return self._proxy._python_nextInt(*args)
         
    def nextDouble(self, *args):
        return self._proxy._python_nextDouble(*args)
         
    def wait(self, *args):
        self._proxy._python_wait(*args)
         
    def wait(self, *args):
        self._proxy._python_wait(*args)
         
    def wait(self, *args):
        self._proxy._python_wait(*args)
         
    def equals(self, *args):
        return self._proxy._python_equals(*args)
         
    def toString(self, *args):
        return self._proxy._python_toString(*args)
         
    def hashCode(self, *args):
        return self._proxy._python_hashCode(*args)
         
    def getClass(self, *args):
        return self._proxy._python_getClass(*args)
         
    def notify(self, *args):
        self._proxy._python_notify(*args)
         
    def notifyAll(self, *args):
        self._proxy._python_notifyAll(*args)
         
from jnius import autoclass, PythonJavaClass, java_method, JavaClass, MetaJavaClass
class _RandomOverrideCallback(PythonJavaClass):
    __javainterfaces__ = ['proxy/java/util/Random_callbackInterface']
    def __init__(self, proxy):
        super(_RandomOverrideCallback, self).__init__()
        self._proxy = proxy
         
    @java_method("(J)V", "setSeed")
    def setSeed0(self, *args):
        self._proxy.setSeed(*args)
         
    @java_method("(Lbyte[];)V", "nextBytes")
    def nextBytes1(self, *args):
        self._proxy.nextBytes(*args)
         
    @java_method("()J", "nextLong")
    def nextLong2(self, *args):
        self._proxy.nextLong(*args)
         
    @java_method("()Z", "nextBoolean")
    def nextBoolean3(self, *args):
        self._proxy.nextBoolean(*args)
         
    @java_method("()F", "nextFloat")
    def nextFloat4(self, *args):
        self._proxy.nextFloat(*args)
         
    @java_method("()D", "nextGaussian")
    def nextGaussian5(self, *args):
        self._proxy.nextGaussian(*args)
         
    @java_method("()Ljava/util/stream/IntStream;", "ints")
    def ints6(self, *args):
        self._proxy.ints(*args)
         
    @java_method("(JII)Ljava/util/stream/IntStream;", "ints")
    def ints7(self, *args):
        self._proxy.ints(*args)
         
    @java_method("(II)Ljava/util/stream/IntStream;", "ints")
    def ints8(self, *args):
        self._proxy.ints(*args)
         
    @java_method("(J)Ljava/util/stream/IntStream;", "ints")
    def ints9(self, *args):
        self._proxy.ints(*args)
         
    @java_method("(JJJ)Ljava/util/stream/LongStream;", "longs")
    def longs10(self, *args):
        self._proxy.longs(*args)
         
    @java_method("(J)Ljava/util/stream/LongStream;", "longs")
    def longs11(self, *args):
        self._proxy.longs(*args)
         
    @java_method("()Ljava/util/stream/LongStream;", "longs")
    def longs12(self, *args):
        self._proxy.longs(*args)
         
    @java_method("(JJ)Ljava/util/stream/LongStream;", "longs")
    def longs13(self, *args):
        self._proxy.longs(*args)
         
    @java_method("(DD)Ljava/util/stream/DoubleStream;", "doubles")
    def doubles14(self, *args):
        self._proxy.doubles(*args)
         
    @java_method("(JDD)Ljava/util/stream/DoubleStream;", "doubles")
    def doubles15(self, *args):
        self._proxy.doubles(*args)
         
    @java_method("()Ljava/util/stream/DoubleStream;", "doubles")
    def doubles16(self, *args):
        self._proxy.doubles(*args)
         
    @java_method("(J)Ljava/util/stream/DoubleStream;", "doubles")
    def doubles17(self, *args):
        self._proxy.doubles(*args)
         
    @java_method("(I)I", "nextInt")
    def nextInt18(self, *args):
        self._proxy.nextInt(*args)
         
    @java_method("()I", "nextInt")
    def nextInt19(self, *args):
        self._proxy.nextInt(*args)
         
    @java_method("()D", "nextDouble")
    def nextDouble20(self, *args):
        self._proxy.nextDouble(*args)
         
    @java_method("(JI)V", "wait")
    def wait21(self, *args):
        self._proxy.wait(*args)
         
    @java_method("(J)V", "wait")
    def wait22(self, *args):
        self._proxy.wait(*args)
         
    @java_method("()V", "wait")
    def wait23(self, *args):
        self._proxy.wait(*args)
         
    @java_method("(Ljava/lang/Object;)Z", "equals")
    def equals24(self, *args):
        self._proxy.equals(*args)
         
    @java_method("()Ljava/lang/String;", "toString")
    def toString25(self, *args):
        self._proxy.toString(*args)
         
    @java_method("()I", "hashCode")
    def hashCode26(self, *args):
        self._proxy.hashCode(*args)
         
    @java_method("()Ljava/lang/Class;", "getClass")
    def getClass27(self, *args):
        self._proxy.getClass(*args)
         
    @java_method("()V", "notify")
    def notify28(self, *args):
        self._proxy.notify(*args)
         
    @java_method("()V", "notifyAll")
    def notifyAll29(self, *args):
        self._proxy.notifyAll(*args)
         

