void arrive(double event_clock [],system_data* syst,alg_data* alg)
{ /* This function is called immediately after an arrival */

    double inter_arrival_time,prod_time,maint_time,repair_time;
    int action;    

    inter_arrival_time=expornd(MU);

    /* schedule next arrival */

    event_clock[1]=TNOW+inter_arrival_time;


    if(syst->buffer>0)
    {
    /* reduce the buffer size by 1 if buffer is not empty */
    /* if unit is sold, increase revenue */

    syst->buffer=syst->buffer-1;
    alg->revenue=alg->revenue+PROFIT;
    }
    

    /* test if the current arrival ends vacation */

    if((UPPER_LIMIT-1)==syst->buffer)
    {/* schedule production or maintenance */
    action=action_selector(syst,alg);  
      if(0==action)
      {
      /* start a production cycle */

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
      else
      {/* schedule a maintenance */
      maint_time=unifrnd(MAINT_MIN,MAINT_MAX);
      event_clock[4]=TNOW+maint_time;
      }   

  }

return;
}
