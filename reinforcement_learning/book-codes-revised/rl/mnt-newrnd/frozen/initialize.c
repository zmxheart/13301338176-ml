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

        alg->revenue=0;

	return;

}
