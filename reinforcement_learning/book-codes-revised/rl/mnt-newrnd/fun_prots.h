
double unifrnd(double, double);
double expornd(double);
double gamrnd(int,double);
int timer(double []);
void arrive(double [],system_data *,alg_data *);
void produce(double [],system_data *,alg_data *); 
void repair(double [],system_data *,alg_data *);
void maintain(double [],system_data *,alg_data *); 
void update(system_data *,alg_data *); 
void initialize(double [],system_data*,alg_data*);
double max(double,double);
void reporter(alg_data);
int action_selector(system_data*,alg_data*);  
