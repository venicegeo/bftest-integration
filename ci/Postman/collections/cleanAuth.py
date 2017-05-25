import os
import re
import fnmatch

cwd = os.getcwd()

for dname, dirs, files in os.walk(cwd):
	for fname in fnmatch.filter(files, "*.postman_collection"):
		fpath = os.path.join(dname, fname)
		print("Opening: %s" % fpath)
		with open(fpath) as f:
			s = f.read()
		s = re.sub(r"Authorization: Basic (?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?\\n", "", s)
		with open(fpath, "w") as f:
			f.write(s)
		print("COMPLETE")