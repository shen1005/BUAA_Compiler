import os

os.system("java -jar lc.jar")
os.system("java -jar szc.jar")
filePath1 = "error.txt"
filePath2 = "error2.txt"
f1 = open(filePath1, "r")
f2 = open(filePath2, "r")
str1 = f1.read()
str2 = f2.read()
if str1 != str2:
    print("Wrong Answer")
else:
    print("Accepted")