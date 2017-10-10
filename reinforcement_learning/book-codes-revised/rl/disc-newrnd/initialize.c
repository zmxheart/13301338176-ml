void initialize(statistics* stat)     
{
/* initialize relevant statistics*/
int state,action;

/* System starts in the following condition */

stat->iter=0;
stat->old_state=0;
stat->old_action=0;

for(state=0;state<=NS-1;state++)
{
	for(action=0;action<=NA-1;action++)
	{
	stat->Q[state][action]=0;
	}
}






return;
}
