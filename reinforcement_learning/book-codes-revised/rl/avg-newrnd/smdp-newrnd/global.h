int NO_REPLICATIONS=30; /* No of replications of simulation */
int ITERMAX=10000; /* No of iterations of learning */
int ITERFIXED=5000; /* No of iterations of simulation with a policy*/
double EXPLORE_PROB_INITIAL=0.5; /* Initial exploration prob; SHOULD BE SET TO 1/NA */
# define NA 2 /* Number of actions in each state */
# define NS 2 /* Number of states */

double SMALL=-1000000; /* a very small number */
double TPM[NA][NS][NS]={
    {
        {0.7,0.3},
        {0.4,0.6}
    },
    {
        {0.9,0.1},
        {0.2,0.8}
    }
                        };   
double TRM[NA][NS][NS]={
    {
        {6,-5},
        {7,12}
    },
    {
        {10,17},
        {-14,13}
    }
                        };   

double TTM[NA][NS][NS]={
       {
        {1,5},
        {120,60}
       },
       {
        {50,75},
        {7,2}
                 }
                        };      

double ETA=0.99;
