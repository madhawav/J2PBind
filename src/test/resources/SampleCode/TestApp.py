import loader
import sys
loader.load_library()

from pybind.test.TestClass import TestClass

class TestClassImpl(TestClass):
    def testMethod2(self):
        print("testMethod2 @ Python: Called and returning 5")
        sys.stdout.flush()
        return 5


    def testMethod1(self):
        print("testMethod1 @ Python: Called")
        sys.stdout.flush()

        print("testMethod1 @ Python: Calling parent method")
        sys.stdout.flush()

        r = super(TestClassImpl, self).testMethod1()
        print("testMethod1 @ Python: Parent returned",r)
        sys.stdout.flush()

        print("testMethod1 @ Python: Returning",r)
        sys.stdout.flush()

        return r


if __name__ == "__main__":
    t = TestClassImpl()
    print("TestApp @ Python: Calling testBoth of TestClassImpl (Python child class of TestClass written in Java)")
    sys.stdout.flush()
    r = t.testBoth()
    print("TestApp @ Python: Test Both returned", r)
    sys.stdout.flush()
