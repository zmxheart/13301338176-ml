void pia(double tpm[NA][NS][NS],double trm[NA][NS][NS])
{
/* Policy Iteration for Average Reward MDPs. */
/* The strategy adopted for the under-determine linear system is */
/* to replace first value by 0 */

int state,next_state,iteration,done,row,col,policy[NS],action,best_action;
double G[NS][NS+1],x[NS],large,sum,rho;

/* We start with an arbitrary policy. */

for(state=0;state<NS;state++)
{
policy[state]=0;
}

iteration=0;
done=1;

while(1==done)
{/* As long as two consecutive policies do not become identical. */


   /* 1. Policy evaluation stage */
   /* The linear equations to be solved are Gx=0. */
   /* Initializing a part of the G Matrix. */

    for(row=0;row<NS;row++)
    {  
	for(col=0;col<NS;col++)
        {

           if(col==0)
           { /* because the first value is replaced by rho */
           G[row][col]=1;
           }
           else
           {
       		if(row==col)
       		{
       		G[row][col]=1-tpm[policy[row]][row][col];
       		}
       		else
       		{
       		G[row][col]=-tpm[policy[row]][row][col];
       		}
            }
     	}
     }

     /* Initializing the (NS+1)th column of G matrix */

    for(state=0;state<NS;state++)
    {
    sum=0.0;
     	for(next_state=0;next_state<NS;next_state++)
     	{
     	sum=sum+(tpm[policy[state]][state][next_state]*
             trm[policy[state]][state][next_state]);
        }
    G[state][NS]=sum;
    }

    solver(G,x); /* x comes out as the solution */

    /* Determine the avg reward */

    rho=x[0];



    printf("Average reward in iteration % d:%lf\n",iteration,x[0]);


    /* The first value is 0 */

    x[0]=0;

    for(state=0;state<NS;state++)
    {
    printf("Value for state %d in current iteration is %lf\n",state,x[state]);
    }


    /* 2. Policy improvement stage */

    done=0;

    for(state=0;state<NS;state++)
    {
    large=SMALL; 
    best_action=0;
  
    	for(action=0;action<NA;action++)
    	{ /* determine the best action for the state */
    	sum=0;
    
       	    for(next_state=0;next_state<NS;next_state++)
       	    {
       	    sum=sum+ (tpm[action][state][next_state]*
            (trm[action][state][next_state]+x[next_state]));
            }
       	    if(sum>large)
            {
       	    large=sum;
       	    best_action=action;
       	    }

         }



         if(policy[state]!=best_action)
         {/* Policy has improved; record new action */
         policy[state]=best_action;
         done=1;/* to ensure that one more iteration is done */
         }

     }

iteration=iteration+1;
}
  
printf("Number of iterations needed: %d\n",iteration);

for(state=0;state<NS;state++)
{
printf("Optimal action for state %d is %d\n",state,policy[state]);
}
for(state=0;state<NS;state++)
{
printf("Optimal value for state %d is %lf\n",state,x[state]);
}


return;
}
