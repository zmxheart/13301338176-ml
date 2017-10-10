void evaluator(double ih_weights[NO_INPUTS][NO_HIDDEN],double
ho_weights[NO_HIDDEN],double bias_weight,double
vbias_weights[NO_HIDDEN],double x[NO_INPUTS])
{
/* evaluates the output for x*/
/* sigmoid thresholding is done only from input to hidden */
/* layer but not from hidden to output layer */

double sum1,sum2,hidden[NO_HIDDEN];
int h,i;

       for(h=0;h<NO_HIDDEN;h++)
       {/* calculate the sum of all inputs to each hidden node */
            sum1=0.0;
            for(i=0;i<NO_INPUTS;i++)
            {/* over all inputs */
            sum1=sum1+(ih_weights[i][h]*x[i]);
            printf("ih_weight[%d][%d]=%lf\n",i,h,ih_weights[i][h]);
            }
       sum1=sum1+vbias_weights[h];
       hidden[h]=compute_sigmoid(sum1);
       }

       sum2=0;
       for(h=0;h<NO_HIDDEN;h++)
       {/* to calculate output; notice this output is not thresholded
       /* with any sigmoid unit */
       sum2=sum2+(ho_weights[h]*hidden[h]);
       printf("ho_weight[%d]=%lf\n",h,ho_weights[h]);
       printf("vbias_weight[%d]=%lf\n",h,vbias_weights[h]);
       }
       sum2=sum2+bias_weight;
       printf("The bias weight is %lf\n",bias_weight);
       printf("The output value is %lf\n",sum2);

return;
}
