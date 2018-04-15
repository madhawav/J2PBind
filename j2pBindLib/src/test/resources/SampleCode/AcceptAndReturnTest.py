import loader
import sys
loader.load_library()

from pybind.test.AcceptAndReturnTest import AcceptAndReturnTest
from jnius import autoclass

if __name__ == "__main__":
    t = AcceptAndReturnTest()
    print("Python: Passing string 'Hi'")
    sys.stdout.flush()
    JString = autoclass('java.lang.String')
    t.accept(JString("Hi"))
    print("Accept completed")
