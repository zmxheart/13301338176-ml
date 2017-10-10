int jump_learn(statistics* stat)
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

/* DO LEARNING */

rqlearn(stat);

/* Select next action */

next_action=action_selector();

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





