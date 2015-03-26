from random import randint
# This Python script is called for the generation of the time related
# features of the activity. Note that the parameter of these functions
# is the actual case id  of the ongoing simulation (you can use this
# value for customize the behavior according to the actual instance).

# The time_lasted(caseid) function returns the number of seconds the
# activity is supposed to last.
def time_lasted(caseid):
	return randint(60*5, 60*15)

# The time_after(caseid) function is returns the number of second to
# wait before the following activity can start.
def time_after(caseid):
	return randint(60*1, 60*5)