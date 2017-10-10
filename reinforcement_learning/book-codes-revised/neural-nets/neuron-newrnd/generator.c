# include <stdio.h>
# include <math.h>
# include "uniformrnd.c"
# include "unifrnd.c"
# include "global.h"

main()
{
int i,p;
double input[NO_INPUTS];
FILE *fid;
double upper_limit,lower_limit;
long SEED=100;
upper_limit=(double)(DATA_SIZE*NO_INPUTS);
lower_limit=1;

fid=fopen("input.dat","w");

for(p=0;p<DATA_SIZE;p++)
{
  for(i=0;i<NO_INPUTS;i++)
  {
  input[i]=unifrnd(0,1,&SEED);
  fprintf(fid,"%f \n",input[i]);
  }
  fprintf(fid,"%f \n",3+(input[0])+(5*input[1]));
}

fclose(fid);

return;
}
