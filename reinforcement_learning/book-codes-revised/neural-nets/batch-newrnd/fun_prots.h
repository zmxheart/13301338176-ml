int reader(double [DATA_SIZE][NO_INPUTS],double [DATA_SIZE]);
void backprop_batch(double [NO_INPUTS][NO_HIDDEN],double 
[NO_HIDDEN],double*, double [NO_HIDDEN],double
[DATA_SIZE][NO_INPUTS],double [DATA_SIZE]);
double compute_sigmoid(double);
void init_net(double [NO_INPUTS][NO_HIDDEN],double
[NO_HIDDEN],double*,double [NO_HIDDEN]);
void simu_net(double [DATA_SIZE][NO_INPUTS],double 
[NO_INPUTS][NO_HIDDEN],double [NO_HIDDEN],double, double
[NO_HIDDEN],double [DATA_SIZE][NO_HIDDEN],double [DATA_SIZE]);
double unifrnd(double,double);
void evaluator(double [NO_INPUTS][NO_HIDDEN],double
[NO_HIDDEN],double,double [NO_HIDDEN],double[NO_INPUTS]);
