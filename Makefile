#jpen-2.jar path
JPENPATH=C:/javaclasspath/jpen/jpen-2.jar
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
DEMO=demo.APainterDemo

JAR=apainter.jar
MANIFEST=apainter.mf






CP=-classpath $(JPENPATH)


$(CLASSDIR)/$(APainter).class:build

$(JAR):$(CLASSDIR)/$(APainter).class
	jar cfm $(JAR) $(MANIFEST) -C $(CLASSDIR) apainter -C $(CLASSDIR) nodamushi

run:$(CLASSDIR)/$(DEMO).class
	java $(CP)$(CLASSPATHSEP)$(CLASSDIR) $(DEMO)

jar:$(JAR)

build:$(SOURCEDIR)/$(DEMO).java
	mkdir $(CLASSDIR)
	$(JAVAC) $(CP) $(ENCODING) -d $(CLASSDIR) -sourcepath $(SOURCEDIR) $<

clean:
	rm -rf $(CLASSDIR)


