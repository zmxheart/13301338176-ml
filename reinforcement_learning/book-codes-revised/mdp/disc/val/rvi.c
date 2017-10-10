void rvi(double tpm[NA][NS][NS],double trm[NA][NS][NS],double
dfactor,double epsilon)
{
/* The code assumes state 0 to be the subtraction factor state */
int done,iter=1,state,next_state,action,policy[NS];
double value[NS],value_old[NS],best,sum,norm;


/* Determine termination factor */
epsilon=epsilon*(1-dfactor)*0.5/dfactor;


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

	for(state=0;state<=NS-1;++state)
	{ /* Value updated state by state */
	best=SMALL;

	   for(action=0;action<=NA-1;++action)
	   {/* Find the best value for each state */
	   sum=0;
              for(next_state=0;next_state<=NS-1;++next_state)
              {
              sum=sum+(tpm[action][state][next_state]*
              (trm[action][state][next_state]+
              dfactor*value_old[next_state]));
              }
              sum=sum-value_old[0]; /* RVI subtraction */
              if(sum>best)
              {
              best=sum;
              policy[state]=action;
              value[state]=best;
              }
           }
	}
       /* Determine the norm of the difference vector */
       norm=-1;
       for(state=0;state<=NS-1;++state)
       {
            if(fabs(value[state]-value_old[state])>norm)
            {
            norm=fabs(value[state]-value_old[state]);
            }
            
         }
         /* Determine whether to terminate */
         if(norm<epsilon)
         { /* terminate */
         done=0;
		 }
	
iter=iter+1;
}

/* Display policy, and value function */

for(state=0;state<=NS-1;++state)
{
printf("Epsilon-optimal action for state %d=%d\n",state,policy[state]);
}
for(state=0;state<=NS-1;++state)
{
printf("The value function for state %d=%lf\n",state,value[state]);
}


printf("The number of iterations needed to converge= %d\n",iter);

return;
}


