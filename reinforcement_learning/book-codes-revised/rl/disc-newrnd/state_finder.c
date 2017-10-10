int state_finder(statistics* stat)
{
double ran,sum;
int candidate,old_action,old_state,complete;




ran=unifrnd(0,1);


/* Find current state */

old_action=stat->old_action;
old_state=stat->old_state;



sum=TPM[old_action][old_state][0];

candidate=0;

complete=0;

while(0==complete)
{
        if(ran<sum)
        {
        complete=1;
        }
        else
        {
        candidate=candidate+1;
        sum=sum+TPM[old_action][old_state][candidate];
        }
}
                  
return(candidate);
}
