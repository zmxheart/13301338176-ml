void rqlearn(statistics* stat)
{
/* Relative Q-Learning */

double q_next,q,learn_rate;
int action;

/* Finding the Max factor in the current state */

q_next=SMALL;

for(action=0;action<=NA-1;action++)
{
	if(stat->Q[stat->current_state][action]>q_next)
 	{
	q_next=stat->Q[stat->current_state][action];	
        }
}

q=stat->Q[stat->old_state][stat->old_action];

stat->iter=stat->iter+1;

learn_rate=10/(100+(double)(stat->iter));


/* Q(0,0) is the distinguished factor */

q=q*(1-learn_rate)+(learn_rate*(stat->rimm+q_next-stat->Q[0][0]));

stat->Q[stat->old_state][stat->old_action]=q;

return;

}
