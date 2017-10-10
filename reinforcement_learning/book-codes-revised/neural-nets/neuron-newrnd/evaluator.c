void evaluator(double weights[NO_INPUTS],double bias_weight, double
x[NO_INPUTS])
{
/* evaluates the output for x*/

double sum;
int i;

            sum=0.0;
            for(i=0;i<NO_INPUTS;i++)
            {/* over all inputs */
            sum=sum+(weights[i]*x[i]);
            printf("weight[%d]=%lf\n",i,weights[i]);
            }
       sum=sum+bias_weight;
       
       

       printf("The bias weight is %lf\n",bias_weight);
       printf("The output value is %lf\n",sum);

return;
}
