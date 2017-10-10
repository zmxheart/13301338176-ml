int jump_learn(statistics* stat)
{
/* This function simulates a jump and also updates the learning */
/* statistics. */

int current_state,next_action,old_state,old_action;
double beta;

/* Extract data related to the old state */

old_state=stat->old_state;
old_action=stat->old_action;


/* Determine current state */

current_state=state_finder(stat);

/* Record Feedback in stat */

stat->current_state=current_state;
stat->rimm=TRM[old_action][old_state][current_state];
stat->timm=TTM[old_action][old_state][current_state];

if(0==stat->flag)
{/* greedy action selected */
stat->greedy_iter=stat->greedy_iter+1;
stat->total_reward=stat->total_reward+stat->rimm;
stat->total_time=stat->total_time+stat->timm;

/* update theta */

beta=1/(log(1+(double)stat->greedy_iter)*(1+(double)stat->greedy_iter)); 


stat->theta=(1-beta)*stat->theta+(beta*(stat->total_reward/stat->total_time));  
}

/* DO LEARNING of Q Values*/

rsmart(stat);

/* Select next action */

next_action=action_selector(stat);

/* Get ready to get out of this function */

stat->old_state=current_state;
stat->old_action=next_action;

	if(stat->iter>=ITERMAX)
	{
	/* Learning should end */
	return(1);
	}
	else
	{
	return(0);
	}

}





