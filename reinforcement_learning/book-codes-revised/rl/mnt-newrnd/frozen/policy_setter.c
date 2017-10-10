void policy_setter(alg_data* alg)
{

/* policy is set here using threshold levels from global.h" */

int count;

for(count=0;count<PCMAX;count++)
{

alg->policy[1][count]=T1;

}

for(count=0;count<PCMAX;count++)
{

alg->policy[2][count]=T2;

}

for(count=0;count<PCMAX;count++)
{

alg->policy[3][count]=T3;

}

for(count=0;count<PCMAX;count++)
{

alg->policy[0][count]=100;

}

return;
}
