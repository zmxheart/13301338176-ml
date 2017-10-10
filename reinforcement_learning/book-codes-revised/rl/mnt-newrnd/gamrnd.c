double gamrnd(int n,double lambda)
{ 
/* generates an n-erlang distributed random no. for parameters :*/
/* (n,lambda); mean=n/lambda*/
/* Using inverse function */
/* if n>8, other methods are recommended; see Law and Kelton */
   int count;
   double x;

   /* adding exponential random numbers */
   x=1.0;
   
    for(count=1;count<=n;count=count+1)
    {
    x=x*unifrnd(0,1);
    }
    x=-log(x);

    return (x/lambda);

}
