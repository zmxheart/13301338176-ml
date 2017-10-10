void simu_net(double input_values[DATA_SIZE][NO_INPUTS],double
ih_weights[NO_INPUTS][NO_HIDDEN],double ho_weights[NO_HIDDEN],double 
bias_weight,double vbias_weights[NO_HIDDEN],double
hidden_values[DATA_SIZE][NO_HIDDEN],double output_values[DATA_SIZE])
{

/* evaluates the values in the net; */
/* sigmoid thresholding is done only from input to hidden */
/* layer but not from hidden to output layer */

double sum1,sum2;
int h,i,p;

   for(p=0;p<DATA_SIZE;p++)
   {/* for each data point */
       for(h=0;h<NO_HIDDEN;h++)
       {/* calculate the sum of all inputs to each hidden node */
            sum1=0.0;
            for(i=0;i<NO_INPUTS;i++)
            {/* over all inputs */
            sum1=sum1+(ih_weights[i][h])*(input_values[p][i]);
            }
        sum1=sum1+vbias_weights[h];
        hidden_values[p][h]=compute_sigmoid(sum1);
        }
   }

   for(p=0;p<DATA_SIZE;p++)
   {
   sum2=0;
       for(h=0;h<NO_HIDDEN;h++)
       {/* to calculate output; notice this output is not thresholded
        /* with any sigmoid unit */
        sum2=sum2+(ho_weights[h]*hidden_values[p][h]);
       }
   sum2=sum2+bias_weight;
   output_values[p]=sum2;
   }

return;
}
