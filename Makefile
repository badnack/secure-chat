JSRC = src
JBIN = bin
JFLAGS = -g
JCOMP = javac
JTEST = Client
PORT1 = 1234
PORT2 = 1345
JTEST2 = TestPriorityServer

cptest:
	$(JCOMP) $(JFLAGS) -sourcepath $(JSRC) -d $(JBIN) $(JSRC)/Client.java


all: cptest

test1:
	java -classpath $(JBIN) $(JTEST) $(PORT1)

test2:
	java -classpath $(JBIN) $(JTEST) $(PORT2)

cleansource:
	$(RM) -r $(JBIN)/*

cleanall: cleansource

documentation:
	javadoc -sourcepath $(JSRC) -d doc/html $(JSRC)/SecureChat/*/*.java
