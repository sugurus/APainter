#jpen-2.jar path
JPENPATH=jpen/jpen-2.jar
#classfile directory
CLASSDIR=classdir
#if unix=> :
CLASSPATHSEP=\;
###########################################
##make
##		compile java files
##make build
##		compile java files with javac option g:none
##make run
##		run application
##make jar
##		create jar file
##make clean
##		remove CLASSDIR
##########################################

SOURCEDIR=src

JAVAC=javac
ENCODING=-encoding utf-8


APainter=apainter.APainter
DEMO=demo/APainterDemo
DEMORUN=demo.APainterDemo

JAR=apainter.jar
MANIFEST=apainter.mf

MOVE=m




CP=-classpath $(JPENPATH)


$(CLASSDIR)/$(DEMO).class:$(SOURCEDIR)/$(DEMO).java
	java $(CP)$(CLASSPATHSEP)make Make $(CLASSDIR) $(JPENPATH)

$(JAR):$(CLASSDIR)/$(APainter).class
	jar cfm $(JAR) $(MANIFEST) -C $(CLASSDIR) apainter -C $(CLASSDIR) nodamushi

run:$(CLASSDIR)/$(DEMO).class
	java $(CP)$(CLASSPATHSEP)$(CLASSDIR)$(CLASSPATHSEP)./ $(DEMORUN)

jar:$(JAR)



clean:
	rm -rf $(CLASSDIR)


