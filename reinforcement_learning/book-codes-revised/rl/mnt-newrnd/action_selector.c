int action_selector(system_data* syst,alg_data* alg)
{
int b,c,action;
double ran_no;

b=syst->buffer;
c=syst->prodcount;


ran_no=unifrnd(0,1);  

if(ran_no<alg->exp_rate)
{
/* must do exploration */
alg->flag=1;
}
else
{ /* select greedy action */
alg->flag=0;
}

if(alg->Q[b][c][0]>=alg->Q[b][c][1])
{
action=0;
}
else
{
action=1;
}

if(1==alg->flag)
{
/* reverse action */

     if(0==action)
     {
     action=1;
     }
     else
     {
     action=0;
     }

}

return(action);

}
