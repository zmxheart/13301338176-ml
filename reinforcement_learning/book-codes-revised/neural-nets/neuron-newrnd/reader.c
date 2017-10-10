int reader(double input_values[DATA_SIZE][NO_INPUTS],double
target_values[DATA_SIZE])
{/* Reads input and target values from a file; */
/* each line has a different value. */
/* The first few lines contain the input values and then the next line */
/* contains the target value and then the sequence repeats */

   FILE *fid;
   int p,i;
   fid=fopen("input.dat","r");
         for(p=0;p<DATA_SIZE;p++)
         {
           for(i=0;i<NO_INPUTS;i++)
           {
           fscanf(fid,"%lf \n",&input_values[p][i]);
           }
         fscanf(fid,"%lf \n",&target_values[p]);
         }    
fclose(fid);

return;

}                   
