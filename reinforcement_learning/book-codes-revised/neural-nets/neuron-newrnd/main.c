# include <stdio.h>
# include <stdlib.h>
# include <math.h>
# include "global.h"
# include "fun_prots.h"  
# include "fun_list.h"  

main()
{
double weights[NO_INPUTS],input_values[DATA_SIZE][NO_INPUTS],
target_values[DATA_SIZE],bias_weight,x[NO_INPUTS];
long seed; 

seed=10;

srand48(seed);


init_net(weights,&bias_weight); 

reader(input_values,target_values);

neuron(weights,&bias_weight,input_values,target_values);   


x[0]=0.2;
x[1]=0.5;

evaluator(weights,bias_weight,x);

return;
}


