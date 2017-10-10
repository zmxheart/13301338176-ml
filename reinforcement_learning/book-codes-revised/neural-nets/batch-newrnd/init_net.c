void init_net(double ih_weights[NO_INPUTS][NO_HIDDEN],double
ho_weights[NO_HIDDEN],double* bias_weight,double vbias_weights[NO_HIDDEN])
{
/* initialize weights in the neural net */

int i,h;

   for(h=0;h<NO_HIDDEN;h++)
   {
    ho_weights[h]=unifrnd(0.0,0.1);
    vbias_weights[h]=unifrnd(0.0,0.1);
    
       for(i=0;i<NO_INPUTS;i++)
       {
       ih_weights[i][h]=unifrnd(0.0,0.1);
       }
   }

   /* obo(output bias to output layer weight) */
   *bias_weight=unifrnd(0.0,0.1);

return;
} 
 
