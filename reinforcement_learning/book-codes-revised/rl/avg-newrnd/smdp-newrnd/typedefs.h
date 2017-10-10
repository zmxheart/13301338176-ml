typedef struct {
double Q[NS][NA];
int iter;
int old_action;
int old_state;
int current_state;
double rimm;
double timm;
double theta; 
double total_reward;
double total_time;
double explore_prob;
int flag;
int greedy_iter;
}statistics;

