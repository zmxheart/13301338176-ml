void via(double tpm[NA][NS][NS],double trm[NA][NS][NS],double epsilon)
{
/* The code is for value iteration in which values become unbounded */
int done,iter=0,state,next_state,action,policy[NS];
double value[NS],value_old[NS],best,sum,span,max_val,min_val,sub_factor;




/* Initialize values */

for(state=0;state<=NS-1;state=state+1)
{ 
        value[state]=0;
}

done=1;

/****************************************************************/
	
while(done!=0)
{/* main loop of value iteration */
	/* Keeping a copy of the old value function */
	for(state=0;state<=NS-1;++state)
    { 
                value_old[state]=value[state];
	}

       /* Value Update */

	for(state=0;state<=NS-1;state=state+1)
	{ /* Value updated state by state */
	best=SMALL;

	   for(action=0;action<=NA-1;action=action+1)
	   {/* Find the best value for each state */
	   sum=0;
              for(next_state=0;next_state<=NS-1;++next_state)
              {
              sum=sum+(tpm[action][state][next_state]*
              (trm[action][state][next_state]+value_old[next_state]));
              }
              if(sum>best)
              {
              best=sum;
              policy[state]=action;
              value[state]=best;
              }
           }
	}

sub_factor=value[0];

       /* Determine the span of the difference vector */
       max_val=value[0]-value_old[0];
       min_val=value[0]-value_old[0];

       for(state=1;state<=NS-1;state=state+1)
       {
            if(value[state]-value_old[state]>max_val)
            {
            max_val=value[state]-value_old[state];
            }
            if(value[state]-value_old[state]<min_val)
            {
            min_val=value[state]-value_old[state];
            }

            
         }

         span=max_val-min_val;
printf("span of difference vector=%lf\n",span);

         /* Determine whether to terminate */
         if(span<epsilon)
         { /* terminate */
         done=0;
		 }
	
for(state=0;state<=NS-1;++state)
{
printf("The value function for state %d=%lf\n",state,value[state]);
}


iter=iter+1;

}

/* Display policy, and value function */

for(state=0;state<=NS-1;state=state+1)
{
printf("Epsilon-optimal action for state %d=%d\n",state,policy[state]);
}
for(state=0;state<=NS-1;state=state+1)
{
printf("The value function for state %d=%lf\n",state,value[state]);
}

printf("The number of iterations needed to converge= %d\n",iter);

return;
}


