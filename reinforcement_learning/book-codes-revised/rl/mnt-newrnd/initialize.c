void initialize(double event_clock[],system_data* syst, alg_data 
*alg)    
{/* Initialize the seeds for random number generation */

        int count1,count2,count3;        

       

	/* Initialize TNOW, the statistics, the event clock etc */

	TNOW = 0;
	
	/* Initialize the end of simulation event */
	event_clock[0]=LENGTH;
	/* Initialize the first arrival */
        event_clock[1]=expornd(MU);
	/* Initialize the first production */
        event_clock[2]=gamrnd(N_PROD,LAMBDA_PROD);
        syst->prod_time=event_clock[2];
        syst->time_for_failure=gamrnd(N_FAIL,LAMBDA_FAIL);
        syst->prodcount=0;
        syst->buffer=0;
        event_clock[3]=BIG;
        event_clock[4]=BIG;  
        alg->last_action=0; /* production action */
        alg->last_buffer=0;
        alg->last_prodcount=0;

        alg->revenue=0;
        alg->time=TNOW;
        alg->total_revenue=0;
        alg->total_time=0;
        alg->flag=0;

        alg->rho=0;
        alg->alpha=0.01;
        alg->iter=0;
        alg->exp_rate=0.5;
        for(count1=0;count1<=UPPER_LIMIT;count1=count1+1)
        {
        for(count2=0;count2<=PCMAX;count2=count2+1)
        {
        for(count3=0;count3<=1;count3=count3+1)
        {/* initialize the Q-factors to 0 */
        alg->Q[count1][count2][count3]=0;
        }
        }
        }
	return;

}
