// this code is for policy iteration for SMDPs
# define NEGATIVE -1000.0
# define ITERMAX 100
# include "solver.cpp"
int poiter(double [][NS][NA],double [][NS][NA],double [][NS][NA]);
int poiter(double tpm[][NS][NA],double reward[][NS][NA],double
time[][NS][NA],int policy[])
{
// tpm is teh transition probability matrix ; reward matrix contains
//reward obtained in going from one state to another taking a certain
//action while time gives teh sojourn times from one state to another
//under a given action

double value[NS],A[NS][NS],b[NS],sum,large,sum1,sum2,gain,g[ITERMAX+1];
int action,state,next_state,best_action,row,col,change,iter;
// Note here we set the value of the first state to 0
// and the coefficient of gain(ie expected time ) in that place because
// this position is replaced by g, the gain term ie average reward per
//unit time.
value[0]=0;
// we start with an arbitrary policy
  for(state=0;state<NS;state++)
  {// setting all actions in policy to first action
  policy[state]=0;
  }

change=1;
while(change==1)
{// as long as two policies do not become identical

iter=iter+1;


// value determination;policy evaluation
// The linear eqautions to be solved are Ax=b 
// To initialize the A matrix

   for(row=0;row<NS;row++)
   {
     for(col=0;col<NS;col++)
     {
       if(row==col)
       {
       A[row][col]=1-tpm[row][col][policy[row]];
       }
       else if(row!=col) 
       {
       A[row][col]=-tpm[row][col][policy[row]];
       }
       if(col==0)
       {// first column contains g terms
       sum=0;
          for(next_state=0;next_state<NS;next_state++)
          {
          sum=sum+(tpm[row][next_state][policy[row]]*
                  time[row][next_state][policy[row]]);
          }
        A[row][col]=sum;
       }
     }
   }

// initializing the b matrix

   for(state=0;state<NS;state++)
   {
   sum=0.0;
     for(next_state=0;next_state<NS;next_state++)
     {
     sum=sum+(tpm[state][next_state][policy[state]]*
             reward[state][next_state][policy[state]]);
     }
   b[state]=sum;
   }

  solver(A,b);// b comes out as teh solution
  for(state=0;state<NS;state++)
  {// setting b to value
  value[state]=b[state];
//  cout<<"value when determined["<<state<<"]"<<value[state]<<endl;
  }

  g[iter]=value[0];// first element of vector value is gain in our
// convention and the first value is actually 0
  value[0]=0;
  
  // policy improvement stage:
  change=0;

  for(state=0;state<NS;state++)
  {// for every state we find by making use of the Bellman equation the
   //action that maximzes g with the values obtained above 
  large=-10000; 
  best_action=0;
  
    for(action=0;action<NA;action++)
    {
    sum1=0;
    sum2=0;
    
       for(next_state=0;next_state<NS;next_state++)
       {
       sum1=sum1+ (reward[state][next_state][action]
                  +value[next_state])*
                 (tpm[state][next_state][action]);

//cout<<"state="<<state<<endl;
//cout<<"next-state="<<next_state<<endl;
//cout<<"tpm"<<tpm[state][next_state][action]<<endl;
//cout<<"reward"<<reward[state][next_state][action]<<endl;
//cout<<"value="<<value[next_state]<<endl;
       sum2=sum2+ (tpm[state][next_state][action]*
                  time[state][next_state][action]);
       }
    gain=sum1-(g[iter]*sum2);
//cout<<"sum1"<<sum1<<endl;
cout<<"gain"<<gain<<endl;
      if(gain>large)
      {// to calculate teh best action so far
      large=gain;
      best_action=action;
      }
    }
    if(policy[state]!=best_action)
    {
//    cout<<"state where pol changes="<<state<<endl;
//    cout<<"old-action="<<policy[state]<<endl;
    policy[state]=best_action;
//    cout<<"new-action="<<policy[state]<<endl;

    change=1;// to mean that policy changed and hence one more iteration
             // has to be performed
    }
  }// for each state ended
  
if(iter==ITERMAX)
{
change=0;
}
//cout<<g[iter]<<endl;
cout<<"iteration in policy iteration="<<iter<<endl;





}// end of iterations  

return (1);
}
