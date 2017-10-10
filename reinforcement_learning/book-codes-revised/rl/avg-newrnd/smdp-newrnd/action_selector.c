int action_selector(statistics* stat)
{
/* This program is written for two actions */

double ran,na,sum;
int action,complete,state,act;


ran=unifrnd(0,1);


/* State for which action is being selected */

state=stat->current_state;


if(stat->Q[state][0]>stat->Q[state][1])
{/* greedy action is 0 */
	if(ran<stat->explore_prob)
	{/* select non-greedy action */
        action=1;
        stat->flag=1;
        }
        else
        {/* greedy action */
	action=0;
        stat->flag=0;
	}
}
else
{/* greedy action is 1 */
	if(ran<stat->explore_prob)
	{/* select non-greedy action */
        action=0;
        stat->flag=1;
        }
        else
        {/* greedy action */
	action=1;
        stat->flag=0;
	}
}

stat->explore_prob=stat->explore_prob*0.999;


return(action);
}
