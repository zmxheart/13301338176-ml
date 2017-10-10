void produce(double event_clock[],system_data* syst, alg_data*
alg)
{


/* A successful production has just been completed */    

double repair_time,maint_time,prod_time,ran_no;
int action;

alg->last_action=0;

syst->prodcount=syst->prodcount+1;
syst->buffer=syst->buffer+1;

/* the following is reduced by the age */
syst->time_for_failure=syst->time_for_failure-syst->prod_time;

/* since we have reached a new state, it is time to update */

update(syst,alg); 

/* We now store information about the current state in a box. */
/* We will open this box in our next meeting with update. */

alg->last_buffer=syst->buffer;
alg->last_prodcount=syst->prodcount;  


/* Now to schedule the next event */

action=action_selector(syst,alg);

if(0==action)
{
/* start a production cycle, if the buffer has not maxed*/


           if(UPPER_LIMIT==syst->buffer)
           {/* time to take a vacation */
           event_clock[2]=BIG;
           }
           else
           {
           prod_time=gamrnd(N_PROD,LAMBDA_PROD);
           
              if(prod_time<syst->time_for_failure)
              {/* production will occur successfully */
              event_clock[2]=TNOW+prod_time;
              syst->prod_time=prod_time;
              }
              else
              {/* schedule a repair */
              repair_time=gamrnd(N_REPAIR,LAMBDA_REPAIR);
              event_clock[2]=BIG;
              event_clock[3]=TNOW+syst->time_for_failure+repair_time;
              }
            }
}
else
{
/* schedule a maintenance */
maint_time=unifrnd(MAINT_MIN,MAINT_MAX);
event_clock[2]=BIG;
event_clock[4]=TNOW+maint_time;
}


return;
}
