void simulator_mc(void)
{
/* Simulator for a Markov Chain*/

int policy[NS],done;
long seed;
statistics stat;

       /* initialize system */

        initialize(&stat);
        seed=10;
        srand48(seed);


        done=0;/* Pnemonic for simulation, 1 stands for end*/
               /* 0 stands for continue*/

        	while(0==done)
                {
                done=jump_learn(&stat);
                }


                pol_finder(stat,policy);

        /* To find the average reward associated with policy */

               rho_finder(&stat,policy);


return;
}
