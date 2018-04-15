import loader
import sys
loader.load_library()

from pybind.test.AcceptAndReturnTest import AcceptAndReturnTest
from pybind.java.lang.String import String
from pybind.java.lang.Integer import Integer

from jnius import autoclass

import sys

def accept_test(v):
    t = AcceptAndReturnTest()
    print("Python: Passing", type(v), ":" , v.toString())
    sys.stdout.flush()

    r = t.accept(v)
    print("Python: Java returned:", type(r),":", str(r))
    sys.stdout.flush()
    return r

def accept_test_int(v):
    t = AcceptAndReturnTest()
    print("Python: Passing", type(v), ":" , str(v))
    sys.stdout.flush()

    r = t.accept(v)
    print("Python: Java returned:", type(r),":", str(r))
    sys.stdout.flush()
    return r

if __name__ == "__main__":
    print("The Java class implements the 'accept' method which receives and returns java.lang.Object. \n"
          "Python will pass objects of various type to this method.\n")

    print("Passing and returning Strings")
    if accept_test(String("Hi")) != "Hi":
        sys.exit(1)

    print("\n")

    print("Passing and returning Integer")
    if accept_test(Integer(5)) != 5:
        sys.exit(1)

    print("\n")
    print("Now we test with accept method which accepts and return int.")
    if accept_test_int(5) != 10:
        sys.exit(1)


