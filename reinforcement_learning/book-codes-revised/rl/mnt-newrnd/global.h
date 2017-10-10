/* types of events 0=termination; 1=arrive; 2=produce;3=repair 4=maint */

int NUMBER_OF_EVENTS=5; 


double LENGTH=1000000;

double BIG   =1000001; /* has to be bigger than LENGTH */

# define PCMAX 100 /* max value for production count */
# define UPPER_LIMIT 3 /* S value for buffer */
# define LOWER_LIMIT 2 /* s value for buffer */
 

/* the following are the paramters of gamma distribution */
/* for PROD=production time, FAIL=time between failures, */
/* and REPAIR= repair time */
int N_PROD=8;
double LAMBDA_PROD=0.8;

int N_FAIL=8;
double LAMBDA_FAIL=0.01;

int N_REPAIR=4;
double LAMBDA_REPAIR=0.02;

double MU=0.06666666; /* exponential rate of arrival of demands */

/* the following parameters are for the unif dist. for maintenance */

double MAINT_MIN=5;
double MAINT_MAX=20;


double TNOW;

double PROFIT=1;

double MAINT_COST=2;

double REPAIR_COST=5;

