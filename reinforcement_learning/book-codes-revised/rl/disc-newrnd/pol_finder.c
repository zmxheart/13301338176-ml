void pol_finder(statistics stat,int policy[NS])
{
int action,state;
double max;

for(state=0;state<=NS-1;state++)
{/* find policy learned */
max=SMALL;
	for(action=0;action<=NA-1;action++)
        {
		if(stat.Q[state][action]>max)
            	{
            	max=stat.Q[state][action];
	        policy[state]=action;
		}
	}
} 

for(state=0;state<=NS-1;state++)
{
printf("Action learned for state %d:%d\n",state,policy[state]);
}
for(state=0;state<=NS-1;state++)
{
	for(action=0;action<=NA-1;action++)
        {
        printf("For state=%d\n",state);
        printf("For action=%d\n",action);
        printf("Q-factor=%lf\n",stat.Q[state][action]);
        }
}
return;
}
