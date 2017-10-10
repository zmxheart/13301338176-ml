double expornd(double mu)
{
	/* Generates an exponentially distributed random number with a 
	   mean equal to 1/mu */
	
	double x;
	
	do
	x = unifrnd(0.0, 1.0);
	while(x == 0.0);
	
	return (-log(x)/mu);
}
