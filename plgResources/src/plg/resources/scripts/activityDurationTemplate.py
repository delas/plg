# This Python script is called for the generation of the duration of each
# activity. In particular, the time_lasted(caseid) function is called
# and PLG expects it to return the number of seconds the activity is
# supposed to last.
#
# Note that the parameter of this function is the actual case id  of the
# ongoing simulation. You can use this value for customize the duration
# according to the actual instance.
#
# The function proposed returns a random value between 5 and 15 minutes.

from random import randint

def time_lasted(caseid):
	return randint(60*5, 60*15)