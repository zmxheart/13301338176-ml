# include <stdio.h>
# include <stdlib.h>
# include <math.h>
# include <stdlib.h>
# include "global2.h"
# include "typedefs.h"
# include "fun_prots.h"
# include "fun_list.h"

main()    
{
/* A simulator for a production-inventory system using a fixed policy*/

	int next_event,done,policy[4];
	double event_clock[5],number_of_reps,total_rho;
	system_data syst;
	alg_data alg;
	long rep;

       /* specify policy */

       policy_setter(&alg);		


for(rep=0;rep<30;rep=rep+1)
{

        /* Initialize variables*/
		
        initialize(event_clock,&syst,&alg);

       srand48(rep+40);

		
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

total_rho=total_rho+(alg.revenue/TNOW);
printf("Average reward =%lf\n",alg.revenue/TNOW);		


}

number_of_reps=(double)(rep);

printf("Average reward of policy=%lf\n",total_rho/number_of_reps);		

return;
}
