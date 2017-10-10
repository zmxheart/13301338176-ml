void simu_net(double input_values[DATA_SIZE][NO_INPUTS],double
weights[NO_INPUTS],double bias_weight,double output_values[DATA_SIZE])
{
/* evaluates the values in the net; */
double sum;
int i,p;

   for(p=0;p<DATA_SIZE;p++)
   {/* for each data point */
            sum=0.0;
            for(i=0;i<NO_INPUTS;i++)
            {/* over all inputs */
            sum=sum+(weights[i]*input_values[p][i]);
            }
        output_values[p]=sum+bias_weight;
   }

return;
}
