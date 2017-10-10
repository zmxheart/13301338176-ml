int reader(double [DATA_SIZE][NO_INPUTS],double [DATA_SIZE]);
double compute_sigmoid(double);
double unifrnd(double,double);
void neuron(double [NO_INPUTS], double*,
double [DATA_SIZE][NO_INPUTS],double [DATA_SIZE]);
void simu_net(double [DATA_SIZE][NO_INPUTS],double
[NO_INPUTS],double,double [DATA_SIZE]);
void init_net(double [NO_INPUTS],double* );
void evaluator(double [NO_INPUTS],double, double [NO_INPUTS]);
