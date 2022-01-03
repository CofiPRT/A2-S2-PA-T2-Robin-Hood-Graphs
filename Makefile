JFLAGS = -g:none
JVMFLAGS = -Xmx128m -Xss128m
JC = javac
JVM = java



FILE_P1 = P1
HELPERS_P1 = 

FILE_P2 = P2
HELPERS_P2 = 'P2$$Edge'

FILE_P3 = P3
HELPERS_P3 = 'P3$$Edge'

FILE_P4 = Bonus
HELPERS_P4 = Coord Log Move Instance



MAIN_FILES = $(FILE_P1) $(FILE_P2) $(FILE_P3) $(FILE_P4)
HELPER_FILES = $(HELPERS_P1) $(HELPERS_P2) $(HELPERS_P3) $(HELPERS_P4)

ALL_FILES = $(MAIN_FILES) $(HELPER_FILES)



.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $(addsuffix .java, $(MAIN_FILES))

default: build

build: $(addsuffix .class, $(MAIN_FILES))

run-p1:
	$(JVM) $(JVMFLAGS) $(FILE_P1)

run-p2:
	$(JVM) $(JVMFLAGS) $(FILE_P2)

run-p3:
	$(JVM) $(JVMFLAGS) $(FILE_P3)

run-p4:
	$(JVM) $(JVMFLAGS) $(FILE_P4)

clean:
	rm -f $(addsuffix .class, $(ALL_FILES))