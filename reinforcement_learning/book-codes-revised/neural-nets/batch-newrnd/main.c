# include <stdio.h>
# include <stdlib.h>
# include "math.h"
# include "global.h"
# include "fun_prots.h"  
# include "fun_list.h"  

main()
{
double ih_weights[NO_INPUTS][NO_HIDDEN],ho_weights[NO_HIDDEN],
input_values[DATA_SIZE][NO_INPUTS],hidden_values[NO_HIDDEN],
target_values[DATA_SIZE],x[NO_INPUTS],bias_weight,vbias_weights[NO_HIDDEN];
long seed;

seed=10;
srand48(seed);


init_net(ih_weights,ho_weights,&bias_weight,vbias_weights); 

reader(input_values,target_values);
backprop_batch(ih_weights,ho_weights,&bias_weight,vbias_weights,
input_values,target_values);   


x[0]=0.2;
x[1]=0.5;

evaluator(ih_weights,ho_weights,bias_weight,vbias_weights,x);

return;
}


