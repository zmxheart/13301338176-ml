void repair(double event_clock[],system_data *syst, alg_data*
alg)
{
/* repair has just been finished */

double repair_time,maint_time,prod_time;   
int action;

alg->last_action=0;

syst->prodcount=0;
syst->time_for_failure=gamrnd(N_FAIL,LAMBDA_FAIL);
alg->revenue=alg->revenue-REPAIR_COST;


event_clock[3]=BIG;  

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

           if(UPPER_LIMIT!=syst->buffer)
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
             event_clock[3]=TNOW+syst->time_for_failure+repair_time;
             }
           }
}
else
{/* schedule a maintenance */

maint_time=unifrnd(MAINT_MIN,MAINT_MAX);
event_clock[4]=TNOW+maint_time;
}

return;
}
