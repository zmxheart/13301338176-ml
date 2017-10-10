void rho_finder(statistics* stat,int policy[NS])
{

int done;
double rho,sum_rho=0,k=0;
long replicate;

for(replicate=1;replicate<=NO_REPLICATIONS;replicate++)
{

/* initialize seeds; making sure every replicate starts with a */
/* different seed that is 30 apart*/

srand48(replicate+30);

stat->iter=0;
stat->old_state=0;
stat->old_action=0; 
stat->total_reward=0;
stat->total_time=0;

/* initialize statistics */

done=0;/* Pnemonic for simulation, 1 stands for end*/
               /* 0 stands for continue*/

                while(0==done)
                {
                done=jump_fixed(stat,policy);
                }        

rho=stat->total_reward/stat->total_time;


sum_rho=sum_rho+rho;

k=k+1.0;
}
printf("The average reward of learned policy is %lf\n",sum_rho/k);


return;
}
