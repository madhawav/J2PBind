import loader
import sys
loader.load_library()

from pybind.test.SampleInterface import SampleInterface
from pybind.test.InterfaceTest import InterfaceTest

from jnius import autoclass

import sys
class SampleInterfaceImpl(SampleInterface):
    def testMethod(self, a):
        print("SampleInterfaceImpl @ Python: testMethod called (" + str(a) + ")")
        sys.stdout.flush()
        print("SampleInterfaceImpl @ Python: returning " + str(a) + " * 2")
        sys.stdout.flush()
        return a * 2

if __name__ == "__main__":
    i = InterfaceTest()
    a = i.interfaceTest(SampleInterfaceImpl())
    if a != 10:
        sys.exit(1)



