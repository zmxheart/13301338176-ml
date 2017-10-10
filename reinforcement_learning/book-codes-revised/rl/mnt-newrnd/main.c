# include <stdio.h>
# include <stdlib.h>
# include <math.h>
# include <stdlib.h>
# include "global.h"
# include "typedefs.h"
# include "fun_prots.h"
# include "fun_list.h"

main()    
{
/* A simulator for a production-inventory system that learns on its own */

	int next_event,done,count1,count2,count3;
	double event_clock[5];
        long seed;



	system_data syst;
	alg_data alg;
	
        seed=10;
        srand48(seed);

        /* Initialize variables*/
		
        initialize(event_clock,&syst,&alg);
		
		
        done = 0;  /* Pnemonic for simulation, 1 stands for end */
                   /* 0 stands for continue */

        while(0 == done)
        {
        /* Find out which event should take place next */
        
        next_event=timer(event_clock);
        
        /* Schedule the next event */ 
			
                switch(next_event)
                {
                case 0:
                done = 1;
                break;

                case 1:
                arrive(event_clock,&syst,&alg);
                break;
				
                case 2:
                produce(event_clock,&syst,&alg);
                break;

                case 3:
                repair(event_clock,&syst,&alg);
                break;

                case 4:
                maintain(event_clock,&syst,&alg);
                break;
				
		}
		/* End of switch statements */	

        }
        /* End of while loop */          
        reporter(alg);
printf("avg reward %lf\n",alg.total_revenue/TNOW);
printf("exp rate at end %lf\n",alg.exp_rate);
printf("iterations at end %d\n",alg.iter);

		
return;
}
