void init_net(double weights[NO_INPUTS],double* bias_weight)
{
/* initialize weights in the neural net */

int i;

       for(i=0;i<NO_INPUTS;i++)
       {
       weights[i]=unifrnd(0.0,0.1);
       }

       *bias_weight=unifrnd(0.0,0.1);

return;
} 
 
