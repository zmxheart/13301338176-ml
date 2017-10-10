with open("/media/haijunz/27a263b4-e313-4b58-a422-0201e4cb11ed/13301338176_ML/use_case/dumpsys/usagestats1.txt") as f:
   for line in f:
      if line.startswith("package="):
         print "ssss"
      elif "times" in line:
         print line.strip()
