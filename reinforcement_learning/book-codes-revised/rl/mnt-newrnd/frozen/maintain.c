void maintain(double event_clock[],system_data* syst,alg_data* alg)
{
/* a maintenance has just been finished */

double repair_time,maint_time,prod_time;          




syst->prodcount=0;
syst->time_for_failure=gamrnd(N_FAIL,LAMBDA_FAIL);
alg->revenue=alg->revenue-MAINT_COST;


event_clock[4]=BIG;  


/* Now to schedule the next event */

if(syst->prodcount<alg->policy[syst->buffer][syst->prodcount])
{     


/* start a production cycle, if the buffer has not maxed*/

           if(UPPER_LIMIT!=syst->buffer)
           {
           prod_time=gamrnd(N_PROD,LAMBDA_PROD);
           if(prod_time<syst->time_for_failure)
           {/* production will occur successfully */
           event_clock[2]=TNOW+prod_time;
           }
           else
           {/* schedule a repair */
           repair_time=gamrnd(N_REPAIR,LAMBDA_REPAIR);
           event_clock[3]=TNOW+syst->time_for_failure+repair_time;
           }
           }
}
else
{
/* schedule a maintenance */
maint_time=unifrnd(MAINT_MIN,MAINT_MAX);
event_clock[4]=TNOW+maint_time;
}


return;
}
