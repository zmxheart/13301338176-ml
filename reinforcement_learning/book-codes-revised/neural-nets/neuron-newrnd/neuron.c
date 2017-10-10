void neuron(double weights[NO_INPUTS], double*
bias_weight, double input_values[DATA_SIZE][NO_INPUTS],double 
target_values[DATA_SIZE])
{

double output_values[DATA_SIZE],output_deltas[DATA_SIZE];
double best_sse,best_weights[NO_INPUTS],best_bias_weight;
double sse,change,denom,error,learning_rate; 
int iter,i,p;

/* This function trains a neuron net in batch mode; */
/* a bias unit has been used; */

learning_rate=0.01;
best_sse=100000; /* some large number */
simu_net(input_values,weights,*bias_weight,output_values);

iter=0;

while(iter<ITERMAX)
{/* one iteration of training */

    /* update bias weight */
      change=0;
      for(p=0;p<DATA_SIZE;p++)
      {/* computing deltas for output unit for each point */
      output_deltas[p]=target_values[p]-output_values[p];
      change=change+output_deltas[p];
      }
      *bias_weight=*bias_weight+(learning_rate*change); 
    

      /* updating weights from input units to output*/
       
      for(i=0;i<NO_INPUTS;i++)
      {
             change=0;
             for(p=0;p<DATA_SIZE;p++)
             {/* sum change over all points */
             change=change+(output_deltas[p]*input_values[p][i]);
             }
           weights[i]=weights[i]+(change*learning_rate);

      }
     

simu_net(input_values,weights,*bias_weight,output_values);

   sse=0;
   for(p=0;p<DATA_SIZE;p++)
   {/* finding sum of the SSE */
   error=output_values[p]-target_values[p];
   sse=sse+(error*error);
   }
   printf("# of training iterations=%d\n",iter);

   
   if(sse<best_sse)
   {/* save the weights as the best weights so far */
   best_sse=sse;
        for(i=0;i<NO_INPUTS;i++)
        {
        best_weights[i]=weights[i];
        }
   best_bias_weight=*bias_weight;  
   }

   printf("SSE=%lf\n",best_sse);
iter=iter+1;
learning_rate=learning_rate*0.99999;
}

/* return the best weights learned */
        for(i=0;i<NO_INPUTS;i++)
        {
        weights[i]=best_weights[i];
        } 
   *bias_weight=best_bias_weight;  




return;
}
