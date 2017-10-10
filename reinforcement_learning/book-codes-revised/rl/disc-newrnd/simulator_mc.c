void simulator_mc(void)
{
/* Simulator for a Markov Chain*/

int policy[NS],done;
statistics stat;
long seed;

seed=10;
srand48(seed);



        
       
       /* initialize system */

        initialize(&stat);

        done=0;/* Pnemonic for simulation, 1 stands for end*/
               /* 0 stands for continue*/

        	while(0==done)
                {
                done=jump_learn(&stat);
                }
		/* Done with Learning */
		/* Find learned policy */

                pol_finder(stat,policy);

                

return;
}
