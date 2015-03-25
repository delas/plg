# This Python script is called for setting the time before starting
# the following activity. In particular, the time_after(caseid) function
# is called and PLG expects it to return the number of second.
#
# Note that the parameter of this function is the actual case id  of the
# ongoing simulation. You can use this value for customize the duration
# according to the actual instance.
#
# The function proposed returns a random value between 1 and 5 minutes.

from random import randint

def time_after(caseid):
	return randint(60*1, 60*5)