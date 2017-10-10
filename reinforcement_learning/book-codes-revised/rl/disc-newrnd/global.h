
int ITERMAX=2000000; /* No of iterations of learning */
# define NA 2 /* Number of actions in each state */
# define NS 2 /* Number of states */

double SMALL=-1000000; /* a very small number */
double D_FACTOR=0.8; /* Discounting factor */
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

