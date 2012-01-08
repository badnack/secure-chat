JSRC = src
JBIN = bin
JDOC = doc
JFLAGS = -g
JCOMP = javac
JTEST = Client
PORT1 = 1234
PORT2 = 1345
JTEST2 = TestPriorityServer

cptest:
	$(JCOMP) $(JFLAGS) -sourcepath $(JSRC) -d $(JBIN) $(JSRC)/Client.java -deprecation


all: cptest

test1:
	java -classpath $(JBIN) $(JTEST) $(PORT1)

test2:
	java -classpath $(JBIN) $(JTEST) $(PORT2)

cleansource:
	$(RM) -r $(JBIN)/*

cleandoc:
	$(RM) -r $(JDOC)/html/*


cleanall: cleansource cleandoc


documentation:
	javadoc -sourcepath $(JSRC) -d doc/html $(JSRC)/SecureChat/*/*.java
