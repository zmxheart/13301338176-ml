int action_selector(void)
{

double ran,na,sum;
int action,complete,candidate;


ran=unifrnd(0,1);


candidate=0;
na=(double)(NA);
sum=1/na;

complete=0;
/* Selecting each action with equal probability */
while(0==complete)
{
        if(ran<sum)
        {/* action selected */
        action=candidate;
        complete=1;
        }
        else          
        {/* test if ran is associated with next action */
        candidate=candidate+1;
        sum=sum+(1/na);
        }
}

return(action);

}
