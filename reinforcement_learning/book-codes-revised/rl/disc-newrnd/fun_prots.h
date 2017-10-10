double unifrnd(double, double);
void simulator_mc(void);
void initialize(statistics*);
int jump_learn(statistics*);
int state_finder(statistics*);
void qlearn(statistics*);
int action_selector(void);
void pol_finder(statistics,int [NS]);
