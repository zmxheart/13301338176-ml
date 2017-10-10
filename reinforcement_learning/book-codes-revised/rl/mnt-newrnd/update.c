void update(system_data *syst,alg_data *alg)
{

double rimm,timm,rho1,rho2,rho,q,qnext,alpha,beta;
int action,prodcount,buffer,next_prodcount,next_buffer;
double k;



alg->iter=alg->iter+1;
k=(double)(alg->iter);

rimm=alg->revenue;
timm=TNOW-alg->time;
/* alpha=alg->alpha; */

alpha=150/(300+k);
beta=10/(300+k); 

printf("ratio=%lf\n",beta/alpha); 


if(alg->flag==0)
{
/* no exploration; so increment total reward and time */
alg->total_revenue=alg->total_revenue+rimm;
alg->total_time=alg->total_time+timm;
alg->rho=(1-beta)*(alg->rho)+(beta*alg->total_revenue/alg->total_time); 
rho=alg->rho; 
}
else
{
rho=alg->rho;  /* i.e., it remains unchanged from previous iteration */
}



action=alg->last_action;
prodcount=alg->last_prodcount;
buffer=alg->last_buffer;

next_prodcount=syst->prodcount;
next_buffer=syst->buffer;
qnext=max(alg->Q[next_buffer][next_prodcount][0],
alg->Q[next_buffer][next_prodcount][1]);

/* actual updating of the concerned Q value */

q=alg->Q[buffer][prodcount][action];

q=((1-alpha)*q)+(alpha*(rimm-(rho*timm)+(0.999*qnext)));

alg->Q[buffer][prodcount][action]=q;


alg->revenue=0;
alg->time=TNOW;
alg->exp_rate=alg->exp_rate*0.9999; 

return;
}

