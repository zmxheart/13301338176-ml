
typedef struct 
{
double time_for_failure; /* time remaining for failure */
double prod_time; /* time of production */
int buffer; /* current buffer */
int prodcount;	/* current production count */
}system_data;

typedef struct 
{
double revenue; /* cumulative revenue since last update */
double time; /* time of last update */
double last_buffer;
double last_prodcount;
double last_action;
double alpha; /* learning rate for Q-factors */
double rho; /* average reward per unit time */
double Q[UPPER_LIMIT+1][PCMAX+1][2]; /* Q-factors */
int iter; /* number of updates */
double total_revenue;
double total_time;
double exp_rate; /* rate of exploration */
int flag; /* for exploration */
}alg_data;


