void backprop_batch(double ih_weights[NO_INPUTS][NO_HIDDEN],double
ho_weights[NO_HIDDEN],double* bias_weight,double vbias_weights[NO_HIDDEN],
double input_values[DATA_SIZE][NO_INPUTS],double target_values[DATA_SIZE])
{
double output_values[DATA_SIZE],hidden_values[DATA_SIZE][NO_HIDDEN];
double hidden_deltas[DATA_SIZE][NO_HIDDEN],output_deltas[DATA_SIZE];
double best_sse,best_ih_weights[NO_INPUTS][NO_HIDDEN],
best_ho_weights[NO_HIDDEN],best_vbias_weights[NO_HIDDEN],best_bias_weight;
double sse,change,denom,error,learning_rate; 
int iter,i,h,p;

/* This function trains a neural net in batch mode; */
/* uses a single hidden layer;using a sigmoid transfer function only from*/
/* input to hidden,not from hidden to output; */ 
/* a bias unit has been used; */

learning_rate=0.01;
best_sse=100000; /* some large number */
simu_net(input_values,ih_weights,ho_weights,*bias_weight,vbias_weights,
hidden_values,output_values);

iter=0;

while(iter<ITERMAX)
{/* one iteration of training */

      for(p=0;p<DATA_SIZE;p++)
      {/* computing deltas for output unit for each point */
      output_deltas[p]=target_values[p]-output_values[p];
      }
    
    /* update bias weight */

       change=0;
       for(p=0;p<DATA_SIZE;p++)
       {/* summing over all points */
       change=change+output_deltas[p];
       }
       *bias_weight=*bias_weight+(learning_rate*change); 

      /* compute hidden deltas */
      for (h=0;h<NO_HIDDEN;h++)
      {/* computing delta for hidden units for each point */
         for(p=0;p<DATA_SIZE;p++)
         {/* for each point (training example) */
         hidden_deltas[p][h]=output_deltas[p]*ho_weights[h]
         *hidden_values[p][h]*(1-hidden_values[p][h]);
         }
      }
  
      /* updating weights from input units to hidden units */
       
       for(i=0;i<NO_INPUTS;i++)
       {
          for(h=0;h<NO_HIDDEN;h++)
          {
             change=0;
             for(p=0;p<DATA_SIZE;p++)
             {/* sum change over all points */
             change=change+ 
             (hidden_deltas[p][h]*input_values[p][i]);
             }
          ih_weights[i][h]=ih_weights[i][h]+(change*learning_rate);

          }
       }

    /* updating virtual bias weights */
       for(h=0;h<NO_HIDDEN;h++)
       {
       change=0;
             for(p=0;p<DATA_SIZE;p++)
             {/* sum change over all points */
             change=change+hidden_deltas[p][h];
             }
          vbias_weights[h]=vbias_weights[h]+(change*learning_rate);
       }

    /* update weights from hidden to output units */
       for(h=0;h<NO_HIDDEN;h++)
       {
         change=0;
         for(p=0;p<DATA_SIZE;p++)
         {/* sum over all points */
         change=change+(output_deltas[p]*hidden_values[p][h]);
         }
         ho_weights[h]=ho_weights[h]+(learning_rate*change);
       }

simu_net(input_values,ih_weights,ho_weights,*bias_weight,vbias_weights,
hidden_values,output_values);

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
      for(h=0;h<NO_HIDDEN;h++)
      {
        best_ho_weights[h]=ho_weights[h];
        best_vbias_weights[h]=vbias_weights[h];
        for(i=0;i<NO_INPUTS;i++)
        {
        best_ih_weights[i][h]=ih_weights[i][h];
        }
      }   
   best_bias_weight=*bias_weight;  
   }

   printf("SSE=%lf\n",best_sse);
iter=iter+1;
learning_rate=learning_rate*0.99999;
}
/* return the best weights learned */
      for(h=0;h<NO_HIDDEN;h++)
      {
        ho_weights[h]=best_ho_weights[h];
        vbias_weights[h]=best_vbias_weights[h];
        for(i=0;i<NO_INPUTS;i++)
        {
        ih_weights[i][h]=best_ih_weights[i][h];
        }
      }   
   *bias_weight=best_bias_weight;  




return;
}
