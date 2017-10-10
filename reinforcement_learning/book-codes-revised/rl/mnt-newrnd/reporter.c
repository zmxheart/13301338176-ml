void reporter(alg_data alg)
{
int i,j,k;

FILE* report_file;
report_file=fopen("report.dat","a");  

for(i=1;i<=UPPER_LIMIT;i=i+1)
{
   for(j=0;j<=PCMAX;j=j+1)
   {
      for(k=0;k<=1;k=k+1)
      {/* initialize the Q-factors to 0 */

fprintf(report_file,"Q[%d][%d][%d]=%lf\n",i,j,k,alg.Q[i][j][k]);
      }
    }
}

fprintf(report_file,"number of learning iterations=%d\n",alg.iter); 
fclose(report_file);
return;
}
