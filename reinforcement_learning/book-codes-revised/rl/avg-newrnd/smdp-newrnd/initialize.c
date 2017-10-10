void initialize(statistics* stat)     
{
/* initialize relevant statistics*/
int state,action;

/* System starts in the following condition */

stat->iter=0;
stat->greedy_iter=0;
stat->flag=1;
stat->old_state=0;
stat->old_action=0;
stat->theta=0; 
stat->total_reward=0;
stat->total_time=0;
stat->explore_prob=EXPLORE_PROB_INITIAL;
for(state=0;state<=NS-1;state++)
{
	for(action=0;action<=NA-1;action++)
	{/* initialize all Q-factors to 0 */
	stat->Q[state][action]=0;
	}
}






return;
}
