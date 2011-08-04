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


APainter=apainter/APainter

JAR=apainter.jar
MANIFEST=apainter.mf



#defaultfiles
SPLASH=splashImage.png


CP=-classpath $(JPENPATH)


$(CLASSDIR)/$(APainter).class:$(SOURCEDIR)/$(APainter).java
	mkdir -p $(CLASSDIR)
	$(JAVAC) $(CP) $(ENCODING) -d $(CLASSDIR) -sourcepath $(SOURCEDIR) $<
	cp $(SOURCEDIR)$(SPLASH) $(CLASSDIR)$(SPLASH)

$(JAR):$(CLASSDIR)/$(APainter).class
	jar cfm $(JAR) $(MANIFEST) -C $(CLASSDIR) apainter -C $(CLASSDIR) nodamushi

run:$(CLASSDIR)/$(APainter).class
	java $(CP)$(CLASSPATHSEP)$(CLASSDIR) $(APainter)

jar:$(JAR)

build:$(SOURCEDIR)/$(APainter).java
	mkdir -p $(CLASSDIR)
	$(JAVAC) $(CP) $(ENCODING) -d $(CLASSDIR) -g:none -sourcepath $(SOURCEDIR) $<
	cp $(SOURCEDIR)$(SPLASH) $(CLASSDIR)$(SPLASH)

clean:
	rm -rf $(CLASSDIR)


