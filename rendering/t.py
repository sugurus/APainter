#coding:utf-8
import re

F=0
A=1
R=2
G=3
B=4
C=5
field=re.compile("<%FIELD>")
alpha=re.compile("<%ALPHA>")
red=re.compile("<%RED>")
green=re.compile("<%GREEN>")
blue=re.compile("<%BLUE>")
colora0=re.compile("<%COLORa0>")

def readTempFile():
	f=open("template.ren",'r')
	str=""
	for s in f.readlines():
		str=str+s
	f.close()
	return str

def readDefFile(path):
	f=open(path,'r')
	dict={F:"",A:"",R:"",G:"",B:"",C:""}
	str=""
	key=""
	for s in f.readlines():
		if field.match(s):
			dict[key]=str
			str=""
			key=F
		elif alpha.match(s):
			dict[key]=str
			str=""
			key=A
		elif red.match(s):
			dict[key]=str
			str=""
			key=R
		elif green.match(s):
			dict[key]=str
			str=""
			key=G
		elif blue.match(s):
			dict[key]=str
			str=""
			key=B
		elif colora0.match(s):
			dict[key]=str
			str=""
			key=C
		else:
			str=str+s
	dict[key]=str
	f.close()
	return dict

temp=readTempFile()
file = raw_input('Enter file name:')
dict=readDefFile(file)

temp=field.sub(dict[F],temp)
temp=alpha.sub(dict[A],temp)
temp=red.sub(dict[R],temp)
temp=green.sub(dict[G],temp)
temp=blue.sub(dict[B],temp)
temp=colora0.sub(dict[C],temp)
f=open(file+".java.txt",'w')
f.write(temp)
f.close()



