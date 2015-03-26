from random import randrange
# This Python script is called for the generation of the string data
# object. Note that the parameter of this function is the actual case
# id of the ongoing simulation (you can use this value to customize
# your data object). The function name has to be "generate".

def generate(caseId):
	return "test-value-" + str(randrange(100))