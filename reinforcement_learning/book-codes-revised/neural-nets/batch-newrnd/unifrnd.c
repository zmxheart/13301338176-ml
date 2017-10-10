double unifrnd(double a, double b)
{
/* unif distributed random numbers between a and b */
double ran;

ran=drand48();

return(a+(b-a)*ran);


}


