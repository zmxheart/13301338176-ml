int jump_fixed(statistics* stat,int policy[NS])
{
/* This function simulates a jump and also updates the learning */
/* statistics. */

int current_state,next_action,old_state,old_action;

/* Extract data related to the old state */

old_state=stat->old_state;
old_action=stat->old_action;


/* Determine current state */

current_state=state_finder(stat);

/* Record Feedback in stat */

stat->current_state=current_state;
stat->rimm=TRM[old_action][old_state][current_state];
stat->iter=stat->iter+1;
stat->total_reward=stat->total_reward+stat->rimm;


next_action=policy[current_state];

/* Get ready to get out of this function */

stat->old_state=current_state;
stat->old_action=next_action;

	if(stat->iter>=ITERFIXED)
	{
	/* simulation should end */
	return(1);
	}
	else
	{
	return(0);
	}

}





