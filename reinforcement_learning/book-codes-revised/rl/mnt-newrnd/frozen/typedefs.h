
typedef struct 
{
double time_for_failure; /* time remaining for failure */
double prod_time; /* time of production */
int buffer; /* current buffer */
int prodcount;	/* current production count */
}system_data;

typedef struct 
{
double revenue; 
int policy[UPPER_LIMIT+1][PCMAX];
}alg_data;


