from random import randint
# This Python script is called for the generation of the integer data
# object. Note that the parameter of this function is the actual case
# id of the ongoing simulation (you can use this value to customize
# your data object). The function name has to be "generate".

def generate(caseId):
	return randint(0, 1000)