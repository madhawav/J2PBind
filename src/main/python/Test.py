import os

import jnius_config

print(os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))), "target","classes"))
jnius_config.add_classpath(os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.dirname(__file__)))), "target","classes"))

from jnius import autoclass, PythonJavaClass, java_method, JavaClass, MetaJavaClass


class TestClassOverrideCallback(PythonJavaClass):
    __javainterfaces__ = ['proxy/test/TestClass_callbackInterface']

    def __init__(self, proxy):
        super(TestClassOverrideCallback, self).__init__()
        self._proxy = proxy

    @java_method('()V',"testMethod1")
    def testMethod1(self, *args):
        self._proxy.testMethod1(*args)

    @java_method('()V', "testMethod2")
    def testMethod2(self):
        self._proxy.testMethod2()

    @java_method('()V', "testBoth")
    def testBoth(self):
        self._proxy.testBoth()

class TestClass:
    def __init__(self):
        super(TestClass, self).__init__()
        self.override_callback = TestClassOverrideCallback(self)

        print(self.__class__)
        print(TestClass)

        self._proxy = autoclass("proxy.test.TestClass")(self.override_callback)

    def testMethod1(self, *args):
        self._proxy._python_testMethod1(*args)

    def testMethod2(self):
        self._proxy._python_testMethod2()

    def testBoth(self):
        self._proxy._python_testBoth()


class TestClassImpl(TestClass):
    def testMethod2(self):
        print("Python: TestMethod2")
    #
    # def testMethod1(self):
    #     print("Python: TestMethod1")

    # def testBoth(self):
    #     super(TestClassImpl, self).testBoth()
    #     print("Python: TestBoth")



if __name__ == "__main__":
    t = TestClassImpl()
    t.testBoth()